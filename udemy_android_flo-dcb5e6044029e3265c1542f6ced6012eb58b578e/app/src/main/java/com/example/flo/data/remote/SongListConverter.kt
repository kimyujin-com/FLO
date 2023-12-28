package com.example.flo.data.remote

import androidx.room.TypeConverter
import com.example.flo.data.entities.Song
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SongListConverter {
    @TypeConverter
    fun listToJson(value: ArrayList<Song>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun jsonToList(value: String): ArrayList<Song>? {
        val arrayType = object : TypeToken<ArrayList<Song>>() {}.type
        return Gson().fromJson(value, arrayType)
//        return Gson().fromJson(value,ArrayList<Song>::class.java)?.toList()
    }
}