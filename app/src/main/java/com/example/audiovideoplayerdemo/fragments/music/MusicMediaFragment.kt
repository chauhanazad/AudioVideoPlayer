package com.example.audiovideoplayerdemo.fragments.music

import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.audiovideoplayerdemo.adapter.MusicAdapter
import com.example.audiovideoplayerdemo.databinding.FragmentMusicMediaBinding
import com.example.audiovideoplayerdemo.model.Music
import com.example.audiovideoplayerdemo.service.MyMusicService
import com.example.audiovideoplayerdemo.utils.DataUtils
import com.example.audiovideoplayerdemo.viewmodel.SharedViewModel

class MusicMediaFragment : Fragment() {

    private lateinit var binding: FragmentMusicMediaBinding
    private lateinit var viewModel: SharedViewModel
    private lateinit var adapter: MusicAdapter
    private lateinit var musicList: ArrayList<Music>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentMusicMediaBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(this)[SharedViewModel::class.java]
        viewModel.getMusicSize(requireContext())
        binding.rvMusic.layoutManager = LinearLayoutManager(requireContext())
        viewModel.size.observe(viewLifecycleOwner) {
            musicList = ArrayList(it)
            adapter = MusicAdapter(requireContext(), musicList)
                .apply {
                    onClick = {
                        try {
                            val serviceIntent = Intent(requireContext(),MyMusicService::class.java)
                            serviceIntent.action = "${MyMusicService::class.java.simpleName}.ACTION_START"
                            DataUtils.mPlaylist = musicList
                            serviceIntent.putExtra("index",it)
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                requireContext().startForegroundService(serviceIntent)
                            }
                            else
                            {
                                requireContext().startService(serviceIntent)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            binding.rvMusic.adapter = adapter
            if (it < 10) {
                loadMusics(it)
            } else {
                loadMusics(10)
            }
        }
        binding.rvMusic.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if ((  binding.rvMusic.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() == musicList.lastIndex) {
                    loadMusics(1)
                }
            }
        })
    }


    private fun loadMusics(i: Int) {
        viewModel.getMusicFromDevice(requireContext(),i)
        {
            musicList.addAll(it)
            adapter.notifyItemRangeInserted(adapter.musicList.size,it.size)
        }
    }
}