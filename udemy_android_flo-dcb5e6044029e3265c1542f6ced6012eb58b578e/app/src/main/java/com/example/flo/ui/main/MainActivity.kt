package com.example.flo.ui.main

import android.content.Intent
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.flo.R
import com.example.flo.data.entities.Album
import com.example.flo.data.entities.Song
import com.example.flo.data.local.SongDatabase
import com.example.flo.databinding.ActivityMainBinding
import com.example.flo.ui.main.home.HomeFragment
import com.example.flo.ui.main.locker.LockerFragment
import com.example.flo.ui.main.look.LookFragment
import com.example.flo.ui.main.search.SearchFragment
import com.example.flo.ui.song.SongActivity
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var timer: Timer
    private var mediaPlayer: MediaPlayer? = null
    private var song: Song = Song()
    private var gson: Gson = Gson()

    val songs = arrayListOf<Song>()
    lateinit var songDB: SongDatabase
    lateinit var albumDB: SongDatabase
    var nowPos = 0
    var albumJson = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        setTheme(R.style.Theme_FLO)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        inputDummyAlbums()
        inputDummySongs()
        initBottomNavigation()
//        val song = Song(binding.mainMiniplayerTitleTv.text.toString(), binding.mainMiniplayerSingerTv.text.toString(),0,60,false)

        binding.mainPlayerCl.setOnClickListener {
            val editor = getSharedPreferences("song", MODE_PRIVATE).edit()
            editor.putInt("songId",song.id)
            editor.apply()

            val intent = Intent(this, SongActivity::class.java)
            startActivity(intent)
        }
        initPlayList()
        initClickListener()
        initSong()
    }

    override fun onStart() {
        super.onStart()
//        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
//        val songJson = sharedPreferences.getString("songData", null)
//
//        song = if(songJson == null){
//            Song("라일락", "아이유(IU)", 0,60, false, "music_lilac")
//        } else {
//            gson.fromJson(songJson, Song::class.java)
//        }
        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId",0)

        val songDB = SongDatabase.getInstance(this)!!

        song = if (songId == 0){
            songDB.songDao().getSong(1)
        } else{
            songDB.songDao().getSong(songId)
        }
        Log.d("song ID", song.id.toString())
        setMiniPlayer(song)
    }

    private fun initBottomNavigation(){

        supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, HomeFragment())
            .commitAllowingStateLoss()

        binding.mainBnv.setOnItemSelectedListener{ item ->
            when (item.itemId) {

                R.id.homeFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, HomeFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }

                R.id.lookFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LookFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.searchFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, SearchFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
                R.id.lockerFragment -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.main_frm, LockerFragment())
                        .commitAllowingStateLoss()
                    return@setOnItemSelectedListener true
                }
            }
            false
        }
    }

    private fun setMiniPlayer(song : Song){
        binding.mainMiniplayerTitleTv.text = songs[nowPos].title
        binding.mainMiniplayerSingerTv.text = songs[nowPos].singer
        val music = resources.getIdentifier(song.music, "raw", this.packageName)
        mediaPlayer = MediaPlayer.create(this, music)
        setPlayerStatus(song.isPlaying)
    }

    override fun onPause() {
        super.onPause()


        songs[nowPos].second = ((binding.mainMiniplayerProgressSb.progress * songs[nowPos].playTime)/100)/1000
        songs[nowPos].isPlaying = false
        setPlayerStatus(false)

        val sharedPreferences = getSharedPreferences("song", MODE_PRIVATE)
        val editor = sharedPreferences.edit() // 에디터

        editor.putInt("songId",songs[nowPos].id)

        editor.apply()
    }

    override fun onDestroy() {
        super.onDestroy()
        timer.interrupt()
        mediaPlayer?.release()
        mediaPlayer = null
    }

     fun initPlayList(){
        val album: Album
        songDB = SongDatabase.getInstance(this)!!

         val spf = getSharedPreferences("miniPlaySongInfo", MODE_PRIVATE)
         val albumIdx = spf.getInt("albumIdx",0)
         if (albumIdx != 0){
             songs.addAll(songDB.songDao().getAlbumSongs(albumIdx))
             Log.d("tag", "앨범 들어감: $albumIdx")
         }
         else{
             songs.addAll(songDB.songDao().getSongs())
             Log.d("tag", "앨범 안 들어감")
         }
    }

    fun initClickListener(){
        binding.mainMiniplayerBtn.setOnClickListener {
            setPlayerStatus(true)
        }
        binding.mainPauseBtn.setOnClickListener {
            setPlayerStatus(false)
        }
        binding.mainMiniPlayerPrevBtn.setOnClickListener {
            moveSong(+1)
        }
        binding.mainMiniPlayerNextBtn.setOnClickListener {
            moveSong(-1)
        }
    }

    fun initSong(){
//        var bundle = Bundle()
//        var songInfo = bundle.getSerializable("miniPlaySongInfo")

        val spf = getSharedPreferences("song", MODE_PRIVATE)
        val songId = spf.getInt("songId",0)

        nowPos = getPlayingSongPosition(songId)

        Log.d("now Song ID",songs[nowPos].id.toString())

        startTimer()
        setMiniPlayer(songs[nowPos])
    }

    private fun moveSong(direct: Int){
        if (nowPos + direct < 0){
            Toast.makeText(this,"first song", Toast.LENGTH_SHORT).show()
            return
        }

        if (nowPos + direct >= songs.size){
            Toast.makeText(this,"last song", Toast.LENGTH_SHORT).show()
            return
        }

        nowPos += direct

        timer.interrupt()
        startTimer()

        mediaPlayer?.release()
        mediaPlayer = null

        setMiniPlayer(songs[nowPos])
    }

    private fun getPlayingSongPosition(songId: Int): Int{
        for (i in 0 until songs.size){
            if (songs[i].id == songId){
                return i
            }
        }
        return 0
    }
    private fun setPlayerStatus (isPlaying : Boolean){
        songs[nowPos].isPlaying = isPlaying
        timer.isPlaying = isPlaying

        if(isPlaying){
            binding.mainMiniplayerBtn.visibility = View.GONE
            binding.mainPauseBtn.visibility = View.VISIBLE
            mediaPlayer?.start()
        } else {
            binding.mainMiniplayerBtn.visibility = View.VISIBLE
            binding.mainPauseBtn.visibility = View.GONE
            if(mediaPlayer?.isPlaying == true){
                mediaPlayer?.pause()
            }
        }

    }

    private fun startTimer(){
        timer = Timer(songs[nowPos].playTime,songs[nowPos].isPlaying)
        timer.start()
    }

    inner class Timer(private val playTime: Int,var isPlaying: Boolean = true):Thread(){

        private var second : Int = 0
        private var mills: Float = 0f

        override fun run() {
            super.run()
            try {
                while (true){

                    if (second >= playTime){
                        break
                    }

                    if (isPlaying){
                        sleep(50)
                        mills += 50

                        runOnUiThread {
                            binding.mainMiniplayerProgressSb.progress = ((mills / playTime)*100).toInt()
                        }

                        if (mills % 1000 == 0f){
//                            runOnUiThread {
//                                binding.songStartTimeTv.text = String.format("%02d:%02d",second / 60, second % 60)
//                            }
                            second++
                        }

                    }

                }

            }catch (e: InterruptedException){
                Log.d("Song","쓰레드가 죽었습니다. ${e.message}")
            }

        }
    }


    private fun inputDummySongs(){
        val songDB = SongDatabase.getInstance(this)!!
        val songs = songDB.songDao().getSongs()

        if (songs.isNotEmpty()) return

        songDB.songDao().insert(
            Song(
                "Lilac",
                "아이유 (IU)",
                0,
                200,
                false,
                "music_lilac",
                R.drawable.img_album_exp2,
                false,
                1
            )
        )

        songDB.songDao().insert(
            Song(
                "Flu",
                "아이유 (IU)",
                0,
                200,
                false,
                "music_flu",
                R.drawable.img_album_exp2,
                false,
                1
            )
        )

        songDB.songDao().insert(
            Song(
                "Butter",
                "방탄소년단 (BTS)",
                0,
                190,
                false,
                "music_butter",
                R.drawable.img_album_exp,
                false,
                2
            )
        )

        songDB.songDao().insert(
            Song(
                "Next Level",
                "에스파 (AESPA)",
                0,
                210,
                false,
                "music_next",
                R.drawable.img_album_exp3,
                false,
                3
            )
        )


        songDB.songDao().insert(
            Song(
                "Boy with Luv",
                "music_boy",
                0,
                230,
                false,
                "music_lilac",
                R.drawable.img_album_exp4,
                false,
                4
            )
        )


        songDB.songDao().insert(
            Song(
                "BBoom BBoom",
                "모모랜드 (MOMOLAND)",
                0,
                240,
                false,
                "music_bboom",
                R.drawable.img_album_exp5,
                false,
                5
            )
        )

        val _songs = songDB.songDao().getSongs()
        Log.d("DB data", _songs.toString())
    }
//    private fun inputDummyAlbums(){
//        val albumDB = SongDatabase.getInstance(this)!!
//        val albums = albumDB.albumDao().getAlbums()
//
//        if (albums.isNotEmpty()) return
//
//        albumDB.albumDao().insert(
//            Album(
//                "Lilac",
//                "아이유 (IU)",
//                0
//            )
//        )
//
//        albumDB.albumDao().insert(
//            Album(
//                "Butter",
//                "BTS",
//                0
//            )
//        )
//
//        albumDB.albumDao().insert(
//            Album(
//                "Next Level",
//                "에스파",
//                0
//            )
//        )
//
//        albumDB.albumDao().insert(
//            Album(
//                "MapOfTheSoul",
//                "BTS",
//                0
//            )
//        )
//        albumDB.albumDao().insert(
//            Album(
//                "Great!",
//                "모모랜드",
//                0
//            )
//        )
//    }
    private fun inputDummyAlbums() {
        val songDB = SongDatabase.getInstance(this)!!
        val albums = songDB.albumDao().getAlbums()

        if (albums.isNotEmpty()) return

        songDB.albumDao().insert(
            Album(
                "IU 5th Album 'LILAC'", "아이유 (IU)", R.drawable.img_album_exp2
            )
        )

        songDB.albumDao().insert(
            Album(
                "Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp
            )
        )

        songDB.albumDao().insert(
            Album(
                "iScreaM Vol.10 : Next Level Remixes", "에스파 (AESPA)", R.drawable.img_album_exp3
            )
        )

        songDB.albumDao().insert(
            Album(
                "MAP OF THE SOUL : PERSONA", "방탄소년단 (BTS)", R.drawable.img_album_exp4
            )
        )

        songDB.albumDao().insert(
            Album(
                "GREAT!", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5
            )
        )

    }
}