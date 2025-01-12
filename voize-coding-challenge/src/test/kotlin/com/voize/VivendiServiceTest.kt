package com.voize

import com.apollographql.apollo.ApolloClient
import com.voize.client.VivendiGraphQLClient
import com.voize.client.VivendiRestClient
import com.voize.service.VivendiService
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.client.engine.cio.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import org.junit.After
import org.junit.Before
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class VivendiServiceTest {
    private lateinit var httpClient: HttpClient
    private lateinit var apolloClient: ApolloClient
    private lateinit var service: VivendiService

    @Before
    fun setup() {
        httpClient = HttpClient(CIO) {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                    isLenient = true
                })
            }
        }

        apolloClient = ApolloClient.Builder()
            .serverUrl("https://vivapp.vivendi.de:4482/api/vivendi/v1/graphql")
            .build()

        service = VivendiService(
            restClient = VivendiRestClient("https://vivapp.vivendi.de:4482/api/vivendi/v1", httpClient),
            graphQLClient = VivendiGraphQLClient(apolloClient)
        )
    }

    @Test
    fun `authentication succeeds with valid credentials`() = runBlocking {
        val result = service.authenticate("demo01", "demo01")
        println("successful authentication result: $result")
        assertTrue(result.isSuccess,"Authentication should succeed")
        assertNotNull(result.getOrNull()) {
            "Authentication should return a token"
        }
    }

    @Test
    fun `authentication fails with invalid credentials`() = runBlocking {
        val result = service.authenticate("wrong", "credentials")
        println("unsuccessful authentication result: $result")
        assertTrue(result.isFailure,"Authentication shouldn't succeed")
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
    }

    @Test
    fun `call getResidents should return some residents`() = runBlocking {
        val authResult = service.authenticate("demo01", "demo01")
        assertTrue(authResult.isSuccess,"Authentication should succeed")
        val token = authResult.getOrNull()
        requireNotNull(token) {
            "Authentication token should not be null"
        }
        val residents = service.getResidents(token)
        print("residents: $residents")
        assertNotNull(residents) {
            "Get residents should return some residents"
        }
        assertTrue(residents.isNotEmpty())
    }

    @After
    fun tearDown() {
        httpClient.close()
        apolloClient.close()
    }
}