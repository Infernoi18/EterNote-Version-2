package com.example.eternotev2.util

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoicePlayer @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var player: MediaPlayer? = null

    fun playFile(file: File) {
        MediaPlayer.create(context, Uri.fromFile(file)).apply {
            player = this
            start()
            setOnCompletionListener {
                stop()
            }
        }
    }

    fun stop() {
        player?.stop()
        player?.release()
        player = null
    }
}
