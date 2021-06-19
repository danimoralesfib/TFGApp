package com.tfgrecorder.di

import android.content.Context
import android.media.MediaPlayer
import com.github.squti.androidwaverecorder.WaveRecorder
import com.tfgrecorder.MainActivityViewModel
import com.tfgrecorder.controller.*
import com.tfgrecorder.usecases.*
import kotlinx.coroutines.Dispatchers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class TFGAppServiceLocator(private val context: Context) {

    private val tfgRetrofit: TFGRetrofit = TFGRetrofit()
    private var waveRecorder: WaveRecorder? = null
    private var mediaPlayer: MediaPlayer? = null

    private fun provideRetrofit(): Retrofit {
        if (!tfgRetrofit.isInitialized) {
            tfgRetrofit.retrofit = Retrofit.Builder()
                .baseUrl("http://danimocab.pythonanywhere.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }
        return tfgRetrofit.retrofit
    }

    private fun provideTFGService(): TFGService {
        return provideRetrofit().create(TFGService::class.java)
    }

    private fun provideRemoteController(): RemoteController {
        return RemoteControllerImpl(context, provideTFGService(), provideFileName())
    }

    private fun provideFileName(): String {
        return "${context.filesDir?.absolutePath}/audiorecordjoto.wav"
    }

    private fun provideWaveRecorder(): WaveRecorder? {
        if(waveRecorder == null) {
            waveRecorder = WaveRecorder(provideFileName())
        }
        return waveRecorder
    }

    private fun provideMediaPlayer(): MediaPlayer? {
        if(mediaPlayer == null) {
            mediaPlayer = MediaPlayer()
        }
        return mediaPlayer
    }

    private fun provideRecorderController(): RecorderController {
        return RecorderControllerImpl(provideWaveRecorder(), provideMediaPlayer(), provideFileName())
    }

    private fun provideFileController(): FileController {
        return FileControllerImpl(context)
    }

    private fun provideUploadFileUseCase(): UploadFileUseCase {
        return UploadFileUseCase(provideRemoteController())
    }

    private fun provideDownloadFileUseCase(): DownloadFileUseCase {
        return DownloadFileUseCase(provideRemoteController())
    }

    private fun provideStartRecordingUseCase() = StartRecordingUseCase(provideRecorderController())

    private fun provideStopRecordingUseCase() = StopRecordingUseCase(provideRecorderController())

    private fun provideStartPlayingUseCase() = StartPlayingUseCase(provideRecorderController())

    private fun provideStopPlayingUseCase() = StopPlayingUseCase(provideRecorderController())

    private fun provideViewPdfUseCase() = ViewPdfUseCase(provideFileController())

    private fun provideSharePdfUseCase() = SharePdfUseCase(provideFileController())

    private fun provideCoroutineContext() = Dispatchers.IO

    fun provideViewModelFactory() = MainActivityViewModel.MainActivityViewModelFactory(
        provideCoroutineContext(),
        provideUploadFileUseCase(),
        provideDownloadFileUseCase(),
        provideStartRecordingUseCase(),
        provideStopRecordingUseCase(),
        provideStartPlayingUseCase(),
        provideStopPlayingUseCase(),
        provideViewPdfUseCase(),
        provideSharePdfUseCase()
    )
}