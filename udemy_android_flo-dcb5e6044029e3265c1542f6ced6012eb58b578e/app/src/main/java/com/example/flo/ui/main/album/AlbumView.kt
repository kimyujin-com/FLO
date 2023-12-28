package com.example.flo.ui.main.album

import com.example.flo.data.remote.FloTodayResult

interface AlbumView {
    fun onGetAlbumLoading()
    fun onGetAlbumSuccess(code: Int, result: FloTodayResult)
    fun onGetAlbumFailure(code: Int, message: String)
}