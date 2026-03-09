package com.example.teamhubapp.feature_users.data.remote.dto


import com.google.gson.annotations.SerializedName


// Top-level API response wrapper
data class EmployeesResponseDto(

    @SerializedName("status")
    val status: String?,

    @SerializedName("message")
    val message: String?,

    @SerializedName("data")
    val data: DataDto?,

    @SerializedName("meta")
    val meta: MetaDto?
)
