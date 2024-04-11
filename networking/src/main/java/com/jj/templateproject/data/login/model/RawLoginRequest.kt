package com.jj.templateproject.data.login.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RawLoginRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
)
