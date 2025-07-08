package com.regev.poalim.model

import com.google.gson.annotations.SerializedName

data class Video(
    @SerializedName("key") val key: String,
    @SerializedName("site") val site: String,
    @SerializedName("type") val type: String,
    @SerializedName("name") val name: String
)
