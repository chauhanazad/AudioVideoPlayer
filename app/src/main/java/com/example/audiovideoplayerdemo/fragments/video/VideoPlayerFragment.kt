package com.example.audiovideoplayerdemo.fragments.video

import android.content.ComponentName
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.media3.common.*
import androidx.media3.common.C.TRACK_TYPE_TEXT
import androidx.media3.database.DefaultDatabaseProvider
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.exoplayer.util.EventLogger
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.example.audiovideoplayerdemo.R
import com.example.audiovideoplayerdemo.adapter.PlayingMediaAdapter
import com.example.audiovideoplayerdemo.databinding.FragmentVideoPlayerBinding
import com.example.audiovideoplayerdemo.model.MediaUtils
import com.example.audiovideoplayerdemo.service.MyVideoService
import com.example.audiovideoplayerdemo.service.VideoPlayerService
import com.example.audiovideoplayerdemo.viewmodel.SharedViewModel
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors


class VideoPlayerFragment : Fragment() {

    lateinit var binding: FragmentVideoPlayerBinding
    lateinit var viewModel: SharedViewModel
    var player: ExoPlayer? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentVideoPlayerBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
    }

    override fun onStart() {
        super.onStart()
        initPlayer()
    }

    fun initPlayer() {

//        Load server videos
//        val dataSourceFactory = DefaultHttpDataSource.Factory()
//        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
//            .createMediaSource(MediaItem.fromUri("https://storage.googleapis.com/exoplayer-test-media-1/mp4/dizzy-with-tx3g.mp4"))


        player = ExoPlayer.Builder(requireContext())
            .setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true)
            .build()
//        player!!.setMediaSource(mediaSource)

        viewModel.videoItem.observe(viewLifecycleOwner) {
            player!!.addMediaItem(MediaUtils.getVideoItem(it))
            setMediaMetaData(MediaUtils.getVideoItem(it).mediaMetadata)
            binding.playerView.player = player
            player!!.prepare()
            player!!.play()
        }

        player!!.addListener(object : Player.Listener {

            override fun onPlaybackStateChanged(playbackState: Int) {
                super.onPlaybackStateChanged(playbackState)
                if (Player.STATE_ENDED == playbackState) {
                    player!!.seekTo(0)
                }
            }
        })
    }

    private fun setMediaMetaData(mediaMetadata: MediaMetadata) {
        binding.videoTitle.text = mediaMetadata.title
        binding.videoArtist.text = mediaMetadata.artist


        binding.repeatSwitch.setOnClickListener {

        }
    }

    override fun onResume() {
        super.onResume()
        binding.playerView.onResume()
    }

    override fun onPause() {
        super.onPause()
        player!!.pause()
    }

    override fun onStop() {
        super.onStop()
        binding.playerView.player = null
        player!!.release()
    }
}