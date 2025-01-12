package com.voize.client

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.apollographql.apollo.exception.ApolloHttpException
import com.voize.GetResidentsQuery
import com.voize.model.resident.Resident
import org.slf4j.LoggerFactory

class VivendiGraphQLClient(
    private val apolloClient: ApolloClient = ApolloClient.Builder()
        .serverUrl("https://vivapp.vivendi.de:4482/api/vivendi/v1/graphql")
        .build()
) {
    private val logger = LoggerFactory.getLogger(VivendiGraphQLClient::class.java)
    suspend fun getResidents(authToken:String): List<Resident> {
        try {

        val response = apolloClient.query(GetResidentsQuery(
            bereichId = Optional.present(0),
            nurPdBereiche = Optional.present(true),
            auchAbwesende = Optional.present(true)
        ))
        .addHttpHeader("Cookie", "Auth-Token=$authToken")
        .addHttpHeader("Content-Type", "application/json")
        .addHttpHeader("Accept", "*/*")
        .execute()

        if (response.hasErrors()) {
            logger.error("GraphQL Errors: ${response.errors?.joinToString { it.message }}")
        }

        if (response.exception != null) {
            logger.error("Network Exception: ${response.exception}")
            logger.error("Cause: ${response.exception?.cause}")
            logger.error("Stack trace: ${response.exception?.stackTraceToString()}")
        }


            return response.data?.klienten?.mapNotNull { klient ->
                try {
                    Resident(
                        id = klient.id,
                        name = klient.name,
                        firstName = klient.vorname,
                        birthDate = klient.geburtsdatum,
                        levelOfCare = klient.pflegegrad,
                        present = klient.anwesend,
                        areaName = klient.bereichName
                    )
                } catch (e: Exception) {
                    logger.error("Failed to map resident: ${e.message}")
                    null
                }
            } ?: emptyList()
    } catch (e: ApolloHttpException) {
        logger.error("Unexpected error: ${e.message}")
        e.printStackTrace()
        throw e
    }
    }
}