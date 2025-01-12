package com.voize.model.resident

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Resident(
    val id: Int,
    val name: String,
    @SerialName("vorname")
    val firstName: String,
    @SerialName("geburtsdatum")
    val birthDate: String?,
    @SerialName("pflegegrad")
    val levelOfCare: String?,
    @SerialName("anwesend")
    val present: Boolean,
    @SerialName("bereichname")
    val areaName: String?,
    )