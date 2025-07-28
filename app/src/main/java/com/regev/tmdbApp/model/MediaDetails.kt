package com.regev.tmdbApp.model

import com.google.gson.annotations.SerializedName

data class MediaDetails(
    @SerializedName("id") val id: Int,
    @SerializedName("title", alternate = ["name"]) val title: String,
    @SerializedName("overview") val overview: String,
    @SerializedName("poster_path") val posterPath: String?,
    @SerializedName("vote_average") val voteAverage: Float,
    @SerializedName("release_date", alternate = ["first_air_date"]) val releaseDate: String?,
    @SerializedName("genres") val genres: List<Genre>,
    @SerializedName("runtime", alternate = ["episode_run_time"]) val runtime: Any?,
    @SerializedName("videos") val videos: VideoResponse? = null
)

data class Genre(
    @SerializedName("name") val name: String
)
