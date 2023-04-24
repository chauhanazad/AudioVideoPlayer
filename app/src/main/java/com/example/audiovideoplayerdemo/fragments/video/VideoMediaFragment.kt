package com.example.audiovideoplayerdemo.fragments.video

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.MediaMetadata
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.audiovideoplayerdemo.R
import com.example.audiovideoplayerdemo.VideoPlayAdapter
import com.example.audiovideoplayerdemo.adapter.MusicAdapter
import com.example.audiovideoplayerdemo.databinding.FragmentVideoMediaBinding
import com.example.audiovideoplayerdemo.model.MediaData
import com.example.audiovideoplayerdemo.model.MediaUtils
import com.example.audiovideoplayerdemo.model.Music
import com.example.audiovideoplayerdemo.service.MyMusicService
import com.example.audiovideoplayerdemo.utils.DataUtils
import com.example.audiovideoplayerdemo.viewmodel.SharedViewModel

class VideoMediaFragment : Fragment() {

    private lateinit var binding: FragmentVideoMediaBinding
    private lateinit var viewModel: SharedViewModel
    private lateinit var adapter: MusicAdapter
    private lateinit var musicList: ArrayList<Music>
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentVideoMediaBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        viewModel.getVideoSize(requireContext())
        binding.rvVideo.layoutManager = LinearLayoutManager(requireContext())
        viewModel.size.observe(viewLifecycleOwner) {
            musicList = ArrayList(it)
            adapter = MusicAdapter(requireContext(), musicList)
                .apply {
                    onClick = {
//                        try {
//                            val serviceIntent = Intent(requireContext(), MyMusicService::class.java)
//                            serviceIntent.action = "${MyMusicService::class.java.simpleName}.ACTION_START"
//                            serviceIntent.putParcelableArrayListExtra("extraPlaylist",musicList)
//                            serviceIntent.putExtra("index",it)
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                                requireContext().startForegroundService(serviceIntent)
//                            }
//                            else
//                            {
//                                requireContext().startService(serviceIntent)
//                            }
//                        } catch (e: Exception) {
//                            e.printStackTrace()
//                        }
                        viewModel.videoItem.postValue(musicList[it])
                        findNavController().navigate(R.id.action_videoMediaFragment_to_videoPlayerFragment)

                    }
                }
            binding.rvVideo.adapter = adapter
            if (it < 10) {
                loadMusics(it)
            } else {
                loadMusics(10)
            }
        }
        binding.rvVideo.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if ((  binding.rvVideo.layoutManager as LinearLayoutManager).findLastVisibleItemPosition() == musicList.lastIndex) {
                    loadMusics(1)
                }
            }
        })
    }

    private fun loadMusics(i: Int) {
        viewModel.getVideoFromDevice(requireContext(),i)
        {
            musicList.addAll(it)
            adapter.notifyItemRangeInserted(adapter.musicList.size,it.size)
        }
    }
}