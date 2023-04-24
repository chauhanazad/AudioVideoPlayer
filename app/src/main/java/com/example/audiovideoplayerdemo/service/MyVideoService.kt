package com.example.audiovideoplayerdemo.service

import android.app.PendingIntent
import android.app.Service
import android.app.TaskStackBuilder
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import androidx.media3.common.AudioAttributes
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.*
import com.example.audiovideoplayerdemo.fragments.video.VideoPlayerFragment
import com.example.audiovideoplayerdemo.model.MediaUtils
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture


class MyVideoService: MediaLibraryService() {

    private val librarySessionCallback = CustomMediaLibrarySessionCallback()
    var player: ExoPlayer? = null
    private lateinit var mediaLibrarySession: MediaLibrarySession

    private lateinit var customCommands: List<CommandButton>

    private var customLayout = ImmutableList.of<CommandButton>()

    companion object {
        private const val SEARCH_QUERY_PREFIX_COMPAT = "androidx://media3-session/playFromSearch"
        private const val SEARCH_QUERY_PREFIX = "androidx://media3-session/setMediaUri"
        private const val CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON =
            "android.media3.session.demo.SHUFFLE_ON"
        private const val CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF =
            "android.media3.session.demo.SHUFFLE_OFF"
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaLibrarySession? {
        return mediaLibrarySession
    }

    override fun onCreate() {
        super.onCreate()
        initializeSessionAndPlayer()
    }

    override fun onDestroy() {
        player!!.release()
        mediaLibrarySession.release()
        super.onDestroy()
    }

    private fun initializeSessionAndPlayer() {
        player =
            ExoPlayer.Builder(this)
                .setAudioAttributes(AudioAttributes.DEFAULT, /* handleAudioFocus= */ true)
                .build()



        val sessionActivityPendingIntent =
            TaskStackBuilder.create(this).run {
                addNextIntent(Intent(this@MyVideoService, VideoPlayerFragment::class.java))
                addNextIntent(Intent(this@MyVideoService, VideoPlayerFragment::class.java))

                val immutableFlag = if (Build.VERSION.SDK_INT >= 23) PendingIntent.FLAG_IMMUTABLE else 0
                getPendingIntent(0, immutableFlag or PendingIntent.FLAG_UPDATE_CURRENT)
            }

        mediaLibrarySession =
            MediaLibrarySession.Builder(this, player!!, librarySessionCallback)
                .setSessionActivity(sessionActivityPendingIntent)
                .build()
        if (!customLayout.isEmpty()) {
            // Send custom layout to legacy session.
            mediaLibrarySession.setCustomLayout(customLayout)
        }
    }


    private inner class CustomMediaLibrarySessionCallback : MediaLibrarySession.Callback {

        override fun onConnect(
            session: MediaSession,
            controller: MediaSession.ControllerInfo
        ): MediaSession.ConnectionResult {
            val connectionResult = super.onConnect(session, controller)
            val availableSessionCommands = connectionResult.availableSessionCommands.buildUpon()
            customCommands.forEach { commandButton ->
                // Add custom command to available session commands.
                commandButton.sessionCommand?.let { availableSessionCommands.add(it) }
            }
            return MediaSession.ConnectionResult.accept(
                availableSessionCommands.build(),
                connectionResult.availablePlayerCommands
            )
        }

        override fun onPostConnect(session: MediaSession, controller: MediaSession.ControllerInfo) {
            if (!customLayout.isEmpty() && controller.controllerVersion != 0) {
                // Let Media3 controller (for instance the MediaNotificationProvider) know about the custom
                // layout right after it connected.
                ignoreFuture(mediaLibrarySession.setCustomLayout(controller, customLayout))
            }
        }

        override fun onCustomCommand(
            session: MediaSession,
            controller: MediaSession.ControllerInfo,
            customCommand: SessionCommand,
            args: Bundle
        ): ListenableFuture<SessionResult> {
            if (CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_ON == customCommand.customAction) {
                // Enable shuffling.
                player!!.shuffleModeEnabled = true
                // Change the custom layout to contain the `Disable shuffling` command.
                customLayout = ImmutableList.of(customCommands[1])
                // Send the updated custom layout to controllers.
                session.setCustomLayout(customLayout)
            } else if (CUSTOM_COMMAND_TOGGLE_SHUFFLE_MODE_OFF == customCommand.customAction) {
                // Disable shuffling.
                player!!.shuffleModeEnabled = false
                // Change the custom layout to contain the `Enable shuffling` command.
                customLayout = ImmutableList.of(customCommands[0])
                // Send the updated custom layout to controllers.
                session.setCustomLayout(customLayout)
            }
            return Futures.immediateFuture(SessionResult(SessionResult.RESULT_SUCCESS))
        }

        override fun onGetLibraryRoot(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            params: LibraryParams?
        ): ListenableFuture<LibraryResult<MediaItem>> {
            return Futures.immediateFuture(LibraryResult.ofItem(MediaUtils.getMediaItem(), params))
        }

        override fun onGetItem(
            session: MediaLibrarySession,
            browser: MediaSession.ControllerInfo,
            mediaId: String
        ): ListenableFuture<LibraryResult<MediaItem>> {
            val item =
                MediaUtils.getMediaItem()

            return Futures.immediateFuture(LibraryResult.ofItem(item, /* params= */ null))
        }



//        override fun onGetChildren(
//            session: MediaLibrarySession,
//            browser: MediaSession.ControllerInfo,
//            parentId: String,
//            page: Int,
//            pageSize: Int,
//            params: LibraryParams?
//        ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
//            val children =
//                MediaItemTree.getChildren(parentId)
//                    ?: return Futures.immediateFuture(
//                        LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
//                    )
//
//            return Futures.immediateFuture(LibraryResult.ofItemList(children, params))
//        }
//
//        override fun onAddMediaItems(
//            mediaSession: MediaSession,
//            controller: MediaSession.ControllerInfo,
//            mediaItems: List<MediaItem>
//        ): ListenableFuture<List<MediaItem>> {
//            val updatedMediaItems: List<MediaItem> =
//                mediaItems.map { mediaItem ->
//                    if (mediaItem.requestMetadata.searchQuery != null)
//                        getMediaItemFromSearchQuery(mediaItem.requestMetadata.searchQuery!!)
//                    else MediaItemTree.getItem(mediaItem.mediaId) ?: mediaItem
//                }
//            return Futures.immediateFuture(updatedMediaItems)
//        }

//        private fun getMediaItemFromSearchQuery(query: String): MediaItem {
//            // Only accept query with pattern "play [Title]" or "[Title]"
//            // Where [Title]: must be exactly matched
//            // If no media with exact name found, play a random media instead
//            val mediaTitle =
//                if (query.startsWith("play ", ignoreCase = true)) {
//                    query.drop(5)
//                } else {
//                    query
//                }
//
//            return MediaItemTree.getItemFromTitle(mediaTitle) ?: MediaItemTree.getRandomItem()
//        }
    }


    private fun ignoreFuture(customLayout: ListenableFuture<SessionResult>) {
        /* Do nothing. */
    }
}