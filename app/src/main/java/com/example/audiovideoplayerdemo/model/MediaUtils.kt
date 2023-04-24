package com.example.audiovideoplayerdemo.model

import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata

object MediaUtils {

    var mediaItem: MediaData? = null

    fun getMediaItem(): MediaItem {
        val sourceUri = Uri.parse(mediaItem!!.source)
        val metadata =
            MediaMetadata.Builder()
                .setAlbumTitle(mediaItem!!.albumTitle)
                .setTitle(mediaItem!!.title)
                .setArtist(mediaItem!!.artist)
                .setGenre(mediaItem!!.genre)
                .setIsPlayable(true)
                .build()

        return MediaItem.Builder()
            .setMediaId(mediaItem!!.id!!)
            .setMediaMetadata(metadata)
            .setUri(sourceUri)
            .build()
    }

    fun getVideoItem(music:Music): MediaItem {
        val sourceUri = Uri.parse(music.data)
        val metadata =
            MediaMetadata.Builder()
                .setAlbumTitle(music.album)
                .setTitle(music.title)
                .setArtist(music.artist)
                .setIsPlayable(true)
                .build()

        return MediaItem.Builder()
            .setMediaId(music.id!!)
            .setMediaMetadata(metadata)
            .setUri(sourceUri)
            .build()
    }
}