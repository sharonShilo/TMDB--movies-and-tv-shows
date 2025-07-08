package com.regev.poalim.model

import com.google.gson.annotations.SerializedName

data class VideoResponse(
    @SerializedName("results") val results: List<Video>
)