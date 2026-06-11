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
    private var onCompletionListener: (() -> Unit)? = null

    fun playFile(file: File, onCompletion: (() -> Unit)? = null) {
        stop()
        this.onCompletionListener = onCompletion
        
        MediaPlayer().apply {
            setDataSource(context, Uri.fromFile(file))
            prepare()
            player = this
            start()
            setOnCompletionListener {
                stop()
                onCompletionListener?.invoke()
            }
        }
    }

    fun stop() {
        player?.apply {
            if (isPlaying) stop()
            release()
        }
        player = null
    }
}
