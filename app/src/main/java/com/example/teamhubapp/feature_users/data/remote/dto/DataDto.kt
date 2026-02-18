package com.example.teamhubapp.feature_users.data.remote.dto


import com.google.gson.annotations.SerializedName

data class DataDto(

    @SerializedName("employees")
    val employees: List<EmployeeDto>?
)