package com.voize.service

import com.voize.client.VivendiGraphQLClient
import com.voize.client.VivendiRestClient
import com.voize.model.resident.Resident
import org.slf4j.LoggerFactory

class VivendiService(
    private val restClient: VivendiRestClient,
    private val graphQLClient: VivendiGraphQLClient
) {
    private val logger = LoggerFactory.getLogger(VivendiService::class.java)

    suspend fun authenticate(username: String, password: String): Result<String> {
        return restClient.authenticate(username, password)
    }


    suspend fun getResidents(authToken:String):List<Resident>? {
        return try {
            graphQLClient.getResidents(authToken)
        } catch (e: Exception) {
            logger.error("Error fetching residents: ${e.message}")
            emptyList()
        }
    }
}