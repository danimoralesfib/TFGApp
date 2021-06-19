package com.tfgrecorder.controller

import android.media.MediaPlayer
import com.github.squti.androidwaverecorder.WaveRecorder
import java.io.File

class RecorderControllerImpl(
    private val waveRecorder: WaveRecorder?,
    private val mediaPlayer: MediaPlayer?,
    private val fileName: String
) : RecorderController {


    override fun startRecording(): Result<Unit> {
        return try {
            File(fileName).apply {
                if(exists()) delete()
            }
            waveRecorder?.startRecording()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun stopRecording(): Result<Unit> {
        return try {
            waveRecorder?.stopRecording()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun startPlaying(): Result<Unit> {
        return try {
            mediaPlayer?.apply {
                reset()
                setOnPreparedListener { start() }
                setDataSource(fileName)
                prepareAsync()
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override fun stopPlaying(): Result<Unit> {
        return try {
            mediaPlayer?.also {
                if(it.isPlaying) {
                    it.stop()
                }
            }

            return Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

interface RecorderController {
    fun startRecording(): Result<Unit>
    fun stopRecording(): Result<Unit>
    fun startPlaying(): Result<Unit>
    fun stopPlaying(): Result<Unit>
}