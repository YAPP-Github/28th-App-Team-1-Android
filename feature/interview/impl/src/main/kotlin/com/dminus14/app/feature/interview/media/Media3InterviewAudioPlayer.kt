package com.dminus14.app.feature.interview.media

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.PlaybackException
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.ByteArrayDataSource
import androidx.media3.datasource.okhttp.OkHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.OkHttpClient
import javax.inject.Inject

@OptIn(UnstableApi::class)
class Media3InterviewAudioPlayer
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        @InterviewQuestionAudioClient private val okHttpClient: OkHttpClient,
    ) : InterviewAudioPlayer {
        private var player: ExoPlayer? = null
        private var memoryPayload: ByteArray? = null

        override fun play(
            request: InterviewAudioPlaybackRequest,
            listener: InterviewAudioPlayer.Listener,
        ) {
            stop()
            val newPlayer = ExoPlayer.Builder(context).build()
            player = newPlayer
            newPlayer.addListener(
                object : Player.Listener {
                    override fun onPlaybackStateChanged(playbackState: Int) {
                        when (playbackState) {
                            Player.STATE_READY -> {
                                listener.onStarted()
                            }

                            Player.STATE_ENDED -> {
                                listener.onCompleted()
                                stop()
                            }
                        }
                    }

                    override fun onPlayerError(error: PlaybackException) {
                        listener.onFailure(error)
                        stop()
                    }
                },
            )
            val mediaSource =
                when (request) {
                    is InterviewAudioPlaybackRequest.AuthenticatedQuestionStream -> {
                        ProgressiveMediaSource
                            .Factory(OkHttpDataSource.Factory(okHttpClient))
                            .createMediaSource(MediaItem.fromUri(request.url))
                    }

                    is InterviewAudioPlaybackRequest.Base64WrapUpMp3 -> {
                        val bytes = Base64.decode(request.payload, Base64.DEFAULT)
                        memoryPayload = bytes
                        ProgressiveMediaSource
                            .Factory { ByteArrayDataSource(bytes) }
                            .createMediaSource(MediaItem.fromUri(Uri.parse("memory://wrap-up.mp3")))
                    }
                }
            newPlayer.setMediaSource(mediaSource)
            newPlayer.prepare()
            newPlayer.playWhenReady = true
        }

        override fun stop() {
            player?.release()
            player = null
            memoryPayload?.fill(0)
            memoryPayload = null
        }
    }
