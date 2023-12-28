package com.example.flo.ui.main.home

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.example.flo.ui.main.album.AlbumRVAdapter
import com.example.flo.ui.main.BannerFragment
import com.example.flo.ui.main.BannerVPAdapter
import com.example.flo.ui.main.MainActivity
import com.example.flo.R
import com.example.flo.data.local.SongDatabase
import com.example.flo.data.entities.Album
import com.example.flo.databinding.FragmentHomeBinding
import com.example.flo.ui.main.album.AlbumFragment
import com.google.gson.Gson

class HomeFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    private var albumDatas = ArrayList<Album>()
    private var gson: Gson = Gson()

    private lateinit var songDB: SongDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

//        binding.homeAlbumImgIv1.setOnClickListener {
//            (context as MainActivity).supportFragmentManager.beginTransaction()
//                .replace(R.id.main_frm , AlbumFragment())
//                .commitAllowingStateLoss()
//        }

        // 데이터 리스트 생성 더미 데이터
//        albumDatas.apply {
//            add(Album("Butter", "방탄소년단 (BTS)", R.drawable.img_album_exp))
//            add(Album("Lilac", "아이유 (IU)", R.drawable.img_album_exp2))
//            add(Album("Next Level", "에스파 (AESPA)", R.drawable.img_album_exp3))
//            add(Album("Boy with Luv", "방탄소년단 (BTS)", R.drawable.img_album_exp4))
//            add(Album("BBoom BBoom", "모모랜드 (MOMOLAND)", R.drawable.img_album_exp5))
//            add(Album("Weekend", "태연 (Tae Yeon)", R.drawable.img_album_exp6))
//        }
        songDB = SongDatabase.getInstance(requireContext())!!
        albumDatas.addAll(songDB.albumDao().getAlbums()) // songDB에서 album list를 가져옵니다.
        Log.d("albumlist", albumDatas.toString())

        // 더미데이터랑 Adapter 연결
        val albumRVAdapter = AlbumRVAdapter(albumDatas)
        // 리사이클러뷰에 어댑터를 연결
        binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter

        var mainActivity: MainActivity = MainActivity()
        albumRVAdapter.setMyItemClickListener(object : AlbumRVAdapter.MyItemClickListener {

            override fun onItemClick(album: Album) {
                changeAlbumFragment(album)
            }

            override fun onRemoveAlbum(position: Int) {
                albumRVAdapter.removeItem(position)
            }

            override fun onPlayButtonClick(album: Album) {
                val gson = Gson()
                var albumJson = gson.toJson(album)

                val prefs: SharedPreferences =
                    context!!.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = prefs.edit()

                editor.putString("miniPlaySongInfo", albumJson)
                editor.apply()
                mainActivity.initPlayList()
//                mainActivity.initClickListener()
                mainActivity.initSong()
            }
        })

        // 레이아웃 매니저 설정
        binding.homeTodayMusicAlbumRv.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

        val bannerAdapter = BannerVPAdapter(this)
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp))
        bannerAdapter.addFragment(BannerFragment(R.drawable.img_home_viewpager_exp2))
        binding.homeBannerVp.adapter = bannerAdapter
        binding.homeBannerVp.orientation = ViewPager2.ORIENTATION_HORIZONTAL

        return binding.root
    }

    private fun changeAlbumFragment(album: Album) {
        (context as MainActivity).supportFragmentManager.beginTransaction()
            .replace(R.id.main_frm, AlbumFragment().apply {
                arguments = Bundle().apply {
                    val gson = Gson()
                    val albumJson = gson.toJson(album)
                    putString("album", albumJson)
                }
            })
            .commitAllowingStateLoss()
    }
//    override fun onGetSongLoading() {
////        binding.lookLoadingPb.visibility = View.VISIBLE
//    }
//
//    override fun onGetSongSuccess(code: Int, result: FloChartResult) {
////        binding.lookLoadingPb.visibility = View.GONE
//        // 더미데이터랑 Adapter 연결
//        val albumRVAdapter = AlbumRVAdapter2(result)
//        // 리사이클러뷰에 어댑터를 연결
//        binding.homeTodayMusicAlbumRv.adapter = albumRVAdapter
//    }
//
//    override fun onGetSongFailure(code: Int, message: String) {
//        binding.lookLoadingPb.visibility = View.GONE
//        Log.d("LOOK-FRAG/SONG-RESPONSE", message)
//    }
}