package com.example.teamhubapp.feature_users.data.remote.dto

import com.google.gson.annotations.SerializedName

data class EmployeeDto(

    @SerializedName("id")
    val id: String?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("designation")
    val designation: String?,

    @SerializedName("department")
    val department: String?,

    @SerializedName("isactive")
    val isActive: Boolean?,

    @SerializedName("img_url")
    val imgUrl: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("city")
    val city: String?,

    @SerializedName("country")
    val country: String?,

    @SerializedName("joining_date")
    val joiningDate: String?
)
