package com.example.teamhubapp.feature_users.data.mapper

import com.example.teamhubapp.feature_users.data.local.UserEntity
import com.example.teamhubapp.feature_users.data.remote.dto.EmployeeDto
import com.example.teamhubapp.feature_users.domain.model.User

// API → Domain
// Handles nulls from network response
// Note: API field "imgUrl" mapped to domain "imageUrl"
fun EmployeeDto.toDomain(): User = User(
    id          = id          ?: "",
    name        = name        ?: "",
    email       = email       ?: "",
    designation = designation ?: "",
    department  = department  ?: "",
    isActive    = isActive    ?: false,
    imageUrl    = imgUrl      ?: "",
    city        = city        ?: "",
    country     = country     ?: "",
    joiningDate = joiningDate ?: ""
)

// Domain → Database
// No null checks needed — domain values are guaranteed
fun User.toEntity(): UserEntity = UserEntity(
    id          = id,
    name        = name,
    email       = email,
    designation = designation,
    department  = department,
    isActive    = isActive,
    imageUrl    = imageUrl,
    city        = city,
    country     = country,
    joiningDate = joiningDate
)

// Database → Domain
// No null checks needed — Room only stores valid data
fun UserEntity.toDomain(): User = User(
    id          = id,
    name        = name,
    email       = email,
    designation = designation,
    department  = department,
    isActive    = isActive ?: false ,
    imageUrl    = imageUrl,
    city        = city,
    country     = country,
    joiningDate = joiningDate
)
