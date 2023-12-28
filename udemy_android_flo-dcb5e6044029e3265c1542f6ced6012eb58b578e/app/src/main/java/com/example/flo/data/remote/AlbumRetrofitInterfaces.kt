package com.example.flo.data.remote

import com.example.flo.data.remote.AlbumResponse
import retrofit2.Call
import retrofit2.http.GET

interface AlbumRetrofitInterfaces {
    @GET("/albums")
    fun getAlbums(): Call<AlbumResponse>
}