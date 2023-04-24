package com.example.audiovideoplayerdemo.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.AudioAttributes
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.media.session.MediaSession
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.audiovideoplayerdemo.R
import com.example.audiovideoplayerdemo.model.Music
import com.example.audiovideoplayerdemo.utils.DataUtils
import java.io.IOException

class MyMusicService : Service() {
    private val TAG = MyMusicService::class.java.simpleName
    private val ACTION_START = "$TAG.ACTION_START"
    private val ACTION_PREV = "$TAG.ACTION_PREV"
    private val ACTION_PLAY = "$TAG.ACTION_PLAY"
    private val ACTION_NEXT = "$TAG.ACTION_NEXT"
    private val EXTRA_PLAYLIST = "extraPlaylist"

    private var mPlaylist: ArrayList<Music> = ArrayList()
    private lateinit var notificationManager: NotificationManager
    private lateinit var mediaPlayer: MediaPlayer
    companion object {
        var index = 0
    }
    private var mIsBuffering = true
    private var mIsReady = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent!!.action

        Log.d(TAG, "onStartCommand: " + intent.action)

        if (ACTION_START == action) {
            mPlaylist = DataUtils.mPlaylist
            index = intent.getIntExtra("index", 0)
            startNotification()
            startPlaying(mPlaylist.get(index))
            return START_STICKY
        }

        stopSelf()
        return START_STICKY
    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .build()
            )
        }
        val intentFilter = IntentFilter()
        intentFilter.addAction(ACTION_PREV)
        intentFilter.addAction(ACTION_PLAY)
        intentFilter.addAction(ACTION_NEXT)

        registerReceiver(mButtonReceiver, intentFilter)
    }

    private val mButtonReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val action = intent.action
            Log.d(TAG, "onReceive: " + intent.action)
            if (ACTION_PREV == action) {
                onCommandPrev()
            } else if (ACTION_PLAY == action) {
                onCommandPlayPause()
            } else if (ACTION_NEXT == action) {
                onCommandNext()
            }
        }
    }

    private fun getButtonPendingIntent(action: String): PendingIntent? {
        val intent = Intent(action)
        intent.flags = Intent.FLAG_ACTIVITY_SINGLE_TOP
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
    }
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name: CharSequence = "Music Player"
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(101.toString(), name, importance)
            notificationManager = getSystemService(
                NotificationManager::class.java
            )
            notificationManager.createNotificationChannel(channel)
        }
    }
    fun startNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var largeIcon: Bitmap? = null
            if (largeIcon == null) {
                largeIcon =
                    BitmapFactory.decodeResource(resources, R.drawable.ic_launcher_background)
            }

            val icPrev: Int = R.drawable.icon_previous
            val icPlayPause = if (mediaPlayer.isPlaying) R.drawable.icon_pause else R.drawable.ic_play
            val icNext: Int = R.drawable.icon_next

            val mediaSession = MediaSessionCompat(applicationContext, TAG)
            val mediaStyle =androidx.media.app.NotificationCompat.MediaStyle().setMediaSession(mediaSession.sessionToken)
            mediaSession.setMetadata(
                MediaMetadataCompat.Builder()
                    .putString(MediaMetadata.METADATA_KEY_TITLE, mPlaylist[index].title)
                    .putString(MediaMetadata.METADATA_KEY_ARTIST, mPlaylist[index].artist)
                    .putString(
                        MediaMetadata.METADATA_KEY_ALBUM_ART_URI, mPlaylist[index].data)
                    .putLong(MediaMetadata.METADATA_KEY_DURATION,
                        mPlaylist[index].duration?.toLong()!!)
                    .build())
            mediaSession.setPlaybackState(
                PlaybackStateCompat.Builder()
                    .setActions(PlaybackStateCompat.ACTION_SEEK_TO)
                    .setState(
                        if (isPaused)
                            PlaybackStateCompat.STATE_PAUSED else PlaybackStateCompat.STATE_PLAYING,

                        // Playback position.
                        // Used to update the elapsed time and the progress bar.
                        mediaPlayer.currentPosition.toLong(),

                        // Playback speed.
                        // Determines the rate at which the elapsed time changes.
                        1.0f
                    )
                    .build()
            )
            mediaSession.setCallback(object : MediaSessionCompat.Callback() {
                override fun onSeekTo(pos: Long) {
                    super.onSeekTo(pos)
                    mediaPlayer.seekTo(pos,MediaPlayer.SEEK_CLOSEST)
                    mediaPlayer.start()
                    startNotification()
                }
            })
//            val token = mediaSession.sessionToken
            createNotificationChannel()
            val notificationBuilder = NotificationCompat.Builder(this, 101.toString())
                .setShowWhen(false)
                .setOngoing(true)
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentTitle(mPlaylist[index].title)
                .setLargeIcon(largeIcon)
                .addAction(icPrev, "Previous", getButtonPendingIntent(ACTION_PREV))
                .addAction(icPlayPause, "Play", getButtonPendingIntent(ACTION_PLAY))
                .addAction(icNext, "Next", getButtonPendingIntent(ACTION_NEXT))
                .setTicker(mPlaylist[index].title)
                .setStyle(mediaStyle)
                .build()
            startForeground(1, notificationBuilder)
        } else {
            startForeground(1, Notification())
        }
    }


    private fun startPlaying(music: Music) {
        try {
            mIsReady = false
            mIsBuffering = true
            val myAudioUri: Uri = Uri.parse(music.data)
            mediaPlayer.reset()
            mediaPlayer.setOnPreparedListener(mMediaPrepared)
            mediaPlayer.setOnCompletionListener(mMediaCompleted)
            mediaPlayer.setOnInfoListener(mMediaInfo)
            mediaPlayer.setOnErrorListener(mMediaError)
            mediaPlayer.setDataSource(baseContext, myAudioUri)
            mediaPlayer.prepareAsync()

        } catch (e: IOException) {
            Log.e(TAG, "startPlaying", e)
            onCommandQuit()
        }
    }

    /**
     * Handle on Quit music event
     */
    private fun onCommandQuit() {
        mIsReady = false
        stopForeground(true)
        stopSelf()
    }

    var isPaused = false
    private fun onCommandPlayPause() {
        try {
            if (mediaPlayer.isPlaying) {
                isPaused = true
                onCommandPause()
            } else {
                isPaused = false
                onCommandPlay()
            }
        } catch (e: IllegalStateException) {
            Log.e(TAG, "onCommandPlayPause", e)
            onCommandQuit()
        }
    }

    /**
     * Handle on start music event
     */
    private fun onCommandPlay() {
        try {
            mediaPlayer.start()
            startNotification()
        } catch (e: IllegalStateException) {
            Log.e(TAG, "onCommandPlay", e)
            onCommandQuit()
        }
    }

    /**
     * Handle on Pause music event
     */
    private fun onCommandPause() {
        try {
            mediaPlayer.pause()
            startNotification()
        } catch (e: IllegalStateException) {
            Log.e(TAG, "onCommandPause", e)
            onCommandQuit()
        }
    }

    /**
     * Handle on Previous music event
     */
    private fun onCommandPrev() {
        index -= 1
        if (index >= 0) {
            startPlaying(mPlaylist[index])
            startNotification()
        }
    }

    /**
     * Handle on Next music event
     */
    private fun onCommandNext() {
        index += 1
        if (index < mPlaylist.size) {
            startPlaying(mPlaylist[index])
            startNotification()
        } else {
            if (notificationManager != null) {
                notificationManager.cancelAll()
            }
        }
    }

    /**
     * Handle event Mediaplyer onPrepared
     */
    private val mMediaPrepared = MediaPlayer.OnPreparedListener {
        Log.d(TAG, "MediaPlayer.onPrepared")
        mIsReady = true
        mIsBuffering = false
        onCommandPlay()
    }

    /**
     * Handle media player music complete event
     */
    private val mMediaCompleted = MediaPlayer.OnCompletionListener {
        Log.d(TAG, "MediaPlayer.onCompletion")
        if ((mPlaylist.size).minus(1) != index) {
            onCommandNext()
        } else {
            onCommandQuit()
        }
    }

    /**
     * Handle current media info
     */
    private val mMediaInfo =
        MediaPlayer.OnInfoListener { mp, what, extra ->
            Log.d(TAG, "MediaPlayer.onInfo: $what, $extra")
            when (what) {
                MediaPlayer.MEDIA_INFO_BUFFERING_START -> {
                    mIsBuffering = true
                }
                MediaPlayer.MEDIA_INFO_BUFFERING_END -> {
                    mIsBuffering = false
                }
            }
            true
        }

    /**
     * Handle Error of mediaplayer
     */
    private val mMediaError =
        MediaPlayer.OnErrorListener { mp, what, extra ->
            Log.e(TAG, "MediaPlayer.onError: $what, $extra")
            onCommandQuit()
            true
        }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer.release()
        unregisterReceiver(mButtonReceiver)
    }
}