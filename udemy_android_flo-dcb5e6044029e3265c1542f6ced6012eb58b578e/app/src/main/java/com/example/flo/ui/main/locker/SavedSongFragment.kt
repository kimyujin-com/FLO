package com.example.flo.ui.main.locker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.flo.R
import com.example.flo.data.entities.Song
import com.example.flo.data.local.SongDatabase
import com.example.flo.databinding.FragmentLockerSavedsongBinding

class SavedSongFragment : Fragment() {
    lateinit var binding: FragmentLockerSavedsongBinding
    lateinit var songDB: SongDatabase
    private var songDatas = java.util.ArrayList<Song>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLockerSavedsongBinding.inflate(inflater, container, false)

        songDB = SongDatabase.getInstance(requireContext())!!

        songDatas.apply {
            add(Song("Butter", "방탄소년단 (BTS)", 0, 0, false, "", R.drawable.img_album_exp, false, 2))
            add(Song("Lilac", "아이유 (IU)", 0, 0, false, "", R.drawable.img_album_exp2, false, 1))
            add(Song("Next Level", "에스파 (AESPA)", 0, 0, false, "",
                R.drawable.img_album_exp3, false, 2))
            add(Song("Boy with Luv", "방탄소년단 (BTS)", 0, 0, false, "",
                R.drawable.img_album_exp4, false))
            add(Song("BBoom BBoom", "모모랜드 (MOMOLAND)", 0, 0, false, "",
                R.drawable.img_album_exp5, false))
            add(Song("Weekend", "태연 (Tae Yeon)", 0, 0, false, "", R.drawable.img_album_exp6, false))
        }
        initRecyclerview()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        initRecyclerview()
    }

    private fun initRecyclerview(){
        binding.lockerSavedSongRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

        val songRVAdapter = SavedSongRVAdapter(songDatas)

        songRVAdapter.setMyItemClickListener(object : SavedSongRVAdapter.MyItemClickListener {
            override fun onRemoveSong(songId: Int) {
                songDB.songDao().updateIsLikeById(false,songId)
            }

        })

        binding.lockerSavedSongRecyclerView.adapter = songRVAdapter

//        songRVAdapter.addSongs(songDB.songDao().getLikedSongs(true) as ArrayList<Song>)
    }
}