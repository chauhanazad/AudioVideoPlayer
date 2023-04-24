package com.example.audiovideoplayerdemo.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService

class VideoPlayerService : MediaLibraryService() {

    private val mediaLibrarySessionCallBack = MediaLibraryCallBack()
    var player: ExoPlayer? = null
    var session: MediaLibrarySession? = null
    init {
        val dataSourceFactory = DefaultHttpDataSource.Factory()
        val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
            .createMediaSource(MediaItem.fromUri("https://storage.googleapis.com/exoplayer-test-media-1/mp4/dizzy-with-tx3g.mp4"))
        player = ExoPlayer.Builder(this)
            .setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true)
            .build()
        player!!.setMediaSource(mediaSource)

        session = MediaLibrarySession.Builder(this, player!!,mediaLibrarySessionCallBack ).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return session
    }

    inner class MediaLibraryCallBack: MediaLibrarySession.Callback{

    }
}