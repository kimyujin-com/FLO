package com.example.flo.data.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.example.flo.data.entities.Album
import java.io.Serializable


//제목, 가수, 사진,재생시간,현재 재생시간, isplaying(재생 되고 있는지)

@Entity(tableName = "SongTable",
        foreignKeys = [
                ForeignKey(
                        entity = Album::class,
                        parentColumns = ["id"],
                        childColumns = ["albumIdx"],
                        onDelete = ForeignKey.CASCADE
                )
        ])
data class Song(
        var title: String = "",
        var singer: String = "",
        var second: Int = 0,
        var playTime: Int = 0,
        var isPlaying: Boolean = false,
        var music: String = "",
        var coverImg: Int? = null,
        var isLike: Boolean = false,
        var albumIdx: Int? = 0
): Serializable {
        @PrimaryKey(autoGenerate = true) var id: Int = 0
}
