package com.example.audiovideoplayerdemo.model

import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaMetadata
import androidx.media3.common.MediaMetadata.FolderType
import androidx.media3.common.MediaMetadata.PictureType
import androidx.media3.common.Rating

data class Media(
    val media: ArrayList<MediaData>,
)

data class MediaData(
    var id: String? = null,
    var title: String? = null,

    val artist: String? = null,

    val albumTitle: String? = null,

    val albumArtist: String? = null,

    val displayTitle: String? = null,
    val genre: String? = null,

    val subtitle: String? = null,

    val description: String? = null,

    val userRating: Rating? = null,
    val overallRating: Rating? = null,
    val artworkDataType: @PictureType Int? = null,
    val artworkUri: Uri? = null,
    val trackNumber: Int? =
        null,

    val totalTrackCount: Int? =
        null,

    val folderType: @FolderType Int? =
        null,

    val isPlayable: Boolean? =
        null,

    val recordingYear: Int? =
        null,

    val recordingMonth: Int? =
        null,
    val recordingDay: Int? =
        null,
    val releaseYear: Int? =
        null,
    val source: String? = null,
)