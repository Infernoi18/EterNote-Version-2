package com.example.eternotev2.util

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Log
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoiceRecorder @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var recorder: MediaRecorder? = null
    private var currentFile: File? = null

    fun startRecording(fileName: String): File? {
        currentFile = File(context.cacheDir, "$fileName.m4a")
        
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            MediaRecorder()
        }.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile(FileOutputStream(currentFile).fd)
            
            try {
                prepare()
                start()
            } catch (e: Exception) {
                Log.e("VoiceRecorder", "Recording failed", e)
                recorder?.release()
                recorder = null
                return null
            }
        }
        return currentFile
    }

    fun stopRecording() {
        try {
            recorder?.apply {
                stop()
                release()
            }
        } catch (e: Exception) {
            Log.e("VoiceRecorder", "Stop recording failed", e)
        }
        recorder = null
    }

    fun pauseRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                recorder?.pause()
            } catch (e: Exception) {
                Log.e("VoiceRecorder", "Pause recording failed", e)
            }
        }
    }

    fun resumeRecording() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            try {
                recorder?.resume()
            } catch (e: Exception) {
                Log.e("VoiceRecorder", "Resume recording failed", e)
            }
        }
    }

    fun getAmplitude(): Float {
        return recorder?.maxAmplitude?.toFloat() ?: 0f
    }
}
