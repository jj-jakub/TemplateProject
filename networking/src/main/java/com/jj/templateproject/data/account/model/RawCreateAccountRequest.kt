package com.jj.templateproject.data.account.model

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class RawCreateAccountRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
)
