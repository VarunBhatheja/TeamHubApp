package com.example.teamhubapp.feature_users.data.mapper


import com.example.teamhubapp.feature_users.data.local.UserEntity
import com.example.teamhubapp.feature_users.data.remote.dto.EmployeeDto
import com.example.teamhubapp.feature_users.domain.model.User

fun EmployeeDto.toDomain(): User {
    return User(
        id = id ?: "",
        name = name ?: "",
        designation = designation ?: "",
        department = department ?: "",
        isActive = isActive ?: false,
        email = email ?: "",
        city = city ?: "",
        country = country ?: "",
        joiningDate = joiningDate ?: "",
        imageUrl = imgUrl?:""
    )
}

fun User.toEntity(): UserEntity {
    return UserEntity(
        id = id,
        name = name,
        designation = designation,
        department = department,
        isActive = isActive,
        email = email,
        city = city,
        country = country,
        joiningDate = joiningDate,
        imageUrl = imageUrl
    )
}


fun UserEntity.toDomain(): User {
    return User(
        id = id,
        name = name,
        designation = designation,
        department = department,
        isActive = isActive,
        email = email,
        city = city,
        country = country,
        joiningDate = joiningDate,
        imageUrl = imageUrl
    )
}
