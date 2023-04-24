package com.example.audiovideoplayerdemo.model

import android.os.Parcel
import android.os.Parcelable

data class Music (
    val id: String?= null,
    val duration: String? = null,
    val is_ringtone:String?=null,
    val album: String?= null,
    val album_artist: String? = null,
    val artist: String?=null,
    val author: String?=null,
    val bucket_display_name : String? = null,
    val _display_name: String?=null,
    val title: String? = null,
    val data: String? = null,
    val isPlayed:Boolean = false
    ) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
        ) {
    }

    override fun describeContents(): Int {
        TODO("Not yet implemented")
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(duration)
        parcel.writeString(is_ringtone)
        parcel.writeString(album)
        parcel.writeString(album_artist)
        parcel.writeString(artist)
        parcel.writeString(author)
        parcel.writeString(bucket_display_name)
        parcel.writeString(_display_name)
        parcel.writeString(title)
        parcel.writeString(data)
    }

    companion object CREATOR : Parcelable.Creator<Music> {
        override fun createFromParcel(parcel: Parcel): Music {
            return Music(parcel)
        }

        override fun newArray(size: Int): Array<Music?> {
            return arrayOfNulls(size)
        }
    }
}