package com.example.simpleapicalling

/**
 * This class is created to obtain the data as a GSON object from the website
 */
data class ResponseData(
    val message: String,
    val user_id: Int,
    val name: String,
    val email: String,
    val mobile: Long,
    val profile_details: ProfileDetails,
    val data_list: List<DataListDetails>
)

data class DataListDetails(
    val id: Int,
    val value: String
)

data class ProfileDetails(
    val is_profile_completed: Boolean,
    val rating: Double
)
