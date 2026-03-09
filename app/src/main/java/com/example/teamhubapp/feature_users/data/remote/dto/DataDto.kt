package com.example.teamhubapp.feature_users.data.remote.dto


import com.google.gson.annotations.SerializedName

// Wrapper around employee list
data class DataDto(

    @SerializedName("employees")
    val employees: List<EmployeeDto>?
)