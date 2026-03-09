package com.example.teamhubapp.feature_users.data.remote.api

import com.example.teamhubapp.feature_users.data.remote.dto.EmployeesResponseDto
import retrofit2.http.GET

interface EmployeeApi {

    // Fetches all employees from remote API

    @GET("employees")
    suspend fun getEmployees(): EmployeesResponseDto
}