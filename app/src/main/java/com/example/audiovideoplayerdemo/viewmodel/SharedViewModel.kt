package com.example.audiovideoplayerdemo.viewmodel

import android.annotation.SuppressLint
import android.content.Context
import android.provider.MediaStore
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.audiovideoplayerdemo.model.MediaData
import com.example.audiovideoplayerdemo.model.Music
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class SharedViewModel : ViewModel() {

    //    private val compositeDisposable = CompositeDisposable()
    private var startingRow = 0
    var rowsToLoad = 0
    private var allLoaded = false
    var size: MutableLiveData<Int> = MutableLiveData()
    private lateinit var musicList: Flow<List<Music>>

    val mediaItem : MutableLiveData<MediaData> = MutableLiveData()
    val videoItem : MutableLiveData<Music> = MutableLiveData()
    /**
     * This call again and again fetchphotos till last record
     */
    fun getMusicFromDevice(context: Context, pageSize: Int, list: (List<Music>) -> Unit) {
        musicList = getMusic(context, pageSize)
        viewModelScope.launch {
            musicList.collect {
                list(it)
            }
        }
    }

    /**
     * Get number of music in device using cursor
     */
    fun getMusicSize(context: Context) {
        val cursor = context.contentResolver.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
            null, null
        )
        val rows = cursor!!.count
        cursor.close()
        size.postValue(rows)
    }

    private fun getMusic(context: Context, rowsPerLoad: Int): Flow<List<Music>> {
        val musicList: ArrayList<Music> = ArrayList()
        val cursor = context.contentResolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null)
        if (cursor != null && !allLoaded) {
            val totalRows = cursor.count
            allLoaded = rowsToLoad == totalRows
            if (rowsToLoad < rowsPerLoad) {
                rowsToLoad = rowsPerLoad
            }
            for (i in startingRow until rowsToLoad) {
                cursor.moveToPosition(i)
                val dataColumnIndex =
                    cursor.getColumnIndex(MediaStore.Audio.Media.DATA) //get column index
                val data = cursor.getString(dataColumnIndex)
                Log.d("dara", data)
                val titleColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
                val title = cursor.getString(titleColumnIndex)

                val albumColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)
                val album = cursor.getString(albumColumnIndex)

                val durationColumnIndex = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION)
                val duration = cursor.getLong(durationColumnIndex)
                val music =
                    Music(title = title, data = data, album = album, duration = duration.toString())
                musicList.add(music)
            }
            startingRow = rowsToLoad

            if (rowsPerLoad > totalRows || rowsToLoad >= totalRows)
                rowsToLoad = totalRows
            else {
                if (totalRows - rowsToLoad <= rowsPerLoad)
                    rowsToLoad = totalRows
                else
                    rowsToLoad += rowsPerLoad
            }
            cursor.close()
        }
        val music: Flow<ArrayList<Music>> = flow {
            emit(musicList)
        }
        return music
    }


    fun getVideoFromDevice(context: Context, pageSize: Int, list: (List<Music>)->Unit)
    {
        musicList = getVideo(context, pageSize)
        viewModelScope.launch {
            musicList.collect {
                list(it)
            }
        }
    }

    fun getVideoSize(context: Context)
    {
        val cursor = context.contentResolver.query(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI, null, null,
            null, null
        )
        val rows = cursor!!.count
        cursor.close()
        size.postValue(rows)
    }

    private fun getVideo(context: Context, rowsPerLoad: Int): Flow<List<Music>> {
        val musicList: ArrayList<Music> = ArrayList()
        val cursor = context.contentResolver.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
            null,
            null,
            null,
            null)
        if (cursor != null && !allLoaded) {
            val totalRows = cursor.count
            allLoaded = rowsToLoad == totalRows
            if (rowsToLoad < rowsPerLoad) {
                rowsToLoad = rowsPerLoad
            }
            for (i in startingRow until rowsToLoad) {
                cursor.moveToPosition(i)
                val idColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media._ID)
                val id = cursor.getString(idColumnIndex)
                val dataColumnIndex =
                    cursor.getColumnIndex(MediaStore.Video.Media.DATA) //get column index
                val data = cursor.getString(dataColumnIndex)
                Log.d("dara", data)
                val titleColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.TITLE)
                val title = cursor.getString(titleColumnIndex)

                val albumColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.ALBUM)
                val album = cursor.getString(albumColumnIndex)

                val durationColumnIndex = cursor.getColumnIndex(MediaStore.Video.Media.DURATION)
                val duration = cursor.getLong(durationColumnIndex)
                val music =
                    Music(id = id, title = title, data = data, album = album, duration = duration.toString())
                musicList.add(music)
            }
            startingRow = rowsToLoad

            if (rowsPerLoad > totalRows || rowsToLoad >= totalRows)
                rowsToLoad = totalRows
            else {
                if (totalRows - rowsToLoad <= rowsPerLoad)
                    rowsToLoad = totalRows
                else
                    rowsToLoad += rowsPerLoad
            }
            cursor.close()
        }
        val music: Flow<ArrayList<Music>> = flow {
            emit(musicList)
        }
        return music
    }
}