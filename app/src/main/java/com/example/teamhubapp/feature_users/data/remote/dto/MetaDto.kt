package com.example.teamhubapp.feature_users.data.remote.dto


import com.google.gson.annotations.SerializedName

data class MetaDto(

    @SerializedName("total_count")
    val totalCount: Int?,

    @SerializedName("page")
    val page: Int?,

    @SerializedName("page_size")
    val pageSize: Int?,

    @SerializedName("has_next_page")
    val hasNextPage: Boolean?
)
