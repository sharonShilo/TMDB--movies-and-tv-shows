package com.regev.poalim.model

import com.google.gson.annotations.SerializedName

data class MediaResponse(
    @SerializedName("results") val results: List<Media>
)
