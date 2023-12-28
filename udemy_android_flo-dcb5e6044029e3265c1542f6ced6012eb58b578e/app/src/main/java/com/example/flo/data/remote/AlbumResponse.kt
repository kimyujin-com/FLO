package com.example.flo.data.remote

import com.google.gson.annotations.SerializedName

data class AlbumResponse(
    @SerializedName("isSuccess") val isSuccess: Boolean,
    @SerializedName("code") val code: Int,
    @SerializedName("message") val message: String,
    @SerializedName("result") val result: FloTodayResult
)
data class FloTodayResult(
    @SerializedName("albums") val albums: List<FloTodayAlbums>
)

data class FloTodayAlbums(
    @SerializedName("albumIdx") val albumIdx: Int,
    @SerializedName("title") val title:String,
    @SerializedName("singer") val singer: String,
    @SerializedName("coverImgUrl") val coverImgUrl : String
)

