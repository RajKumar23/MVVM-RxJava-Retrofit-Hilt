package com.rajkumarrajan.mvvm_architecture.data.model

import com.google.gson.annotations.SerializedName

data class UserObject(

    @field:SerializedName("DeviceId")
    val deviceId: String? = null,

    @field:SerializedName("FirstName")
    val firstName: String? = null,

    @field:SerializedName("LastName")
    val lastName: String? = null,

    @field:SerializedName("MobileNumber")
    val mobileNumber: String? = null,

    @field:SerializedName("Email")
    val email: String? = null,

    @field:SerializedName("AccessToken")
    val accessToken: String? = null

)