package com.example.teamhubapp.feature_users.data.remote.api

import com.example.teamhubapp.feature_users.data.remote.dto.EmployeesResponseDto
import retrofit2.http.GET

interface EmployeeApi {

    @GET("employees")   // <-- put your correct endpoint here
    suspend fun getEmployees(): EmployeesResponseDto
}