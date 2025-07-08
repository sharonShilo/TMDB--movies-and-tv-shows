package com.regev.poalim.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Media(
    @SerializedName("id") val id: Int,
    @SerializedName("title", alternate = ["name"]) val title: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val voteAverage: Float,
    @SerializedName("media_type") val mediaType: String? = null,
    @SerializedName("popularity") val popularity: String? = null,
    val cachedPosterFullPath: String? = null,
    val isFavorite: Boolean? = null
) : Parcelable
