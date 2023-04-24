package com.example.audiovideoplayerdemo.utils

import android.content.res.AssetManager
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.Util
import com.example.audiovideoplayerdemo.model.Media
import com.example.audiovideoplayerdemo.model.MediaData
import com.example.audiovideoplayerdemo.model.Music
import com.google.gson.Gson
import org.json.JSONObject

object DataUtils {

    var mPlaylist: ArrayList<Music> = ArrayList()

    fun getJsonData(assets: AssetManager): ArrayList<MediaData>
    {
        val buffer = assets.open("catalog.json").use { Util.toByteArray(it) }
        val gson = Gson()
        val data = String(buffer, Charsets.UTF_8)
        val mediaList = gson.fromJson(data,Media::class.java)
//        val jsonObject = JSONObject(data)
//        val mediaList = jsonObject.getJSONArray("media")
        return mediaList.media
    }
}