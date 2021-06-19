package com.tfgrecorder.usecases

import com.tfgrecorder.controller.FileController
import com.tfgrecorder.controller.RecorderController
import com.tfgrecorder.controller.RemoteController

class UploadFileUseCase(private val remoteController: RemoteController) :
        () -> Result<AppStatus> {
    override fun invoke(): Result<AppStatus> {
        return if (remoteController.upload().isSuccess) {
            Result.success(AppStatus.UploadingDone)
        } else {
            Result.success(AppStatus.TFGError.ErrorUploading)
        }
    }
}

class DownloadFileUseCase(private val remoteController: RemoteController) :
        () -> Result<AppStatus> {
    override fun invoke(): Result<AppStatus> {
        return if (remoteController.download().isSuccess) {
            Result.success(AppStatus.DownloadingDone)
        } else {
            Result.success(AppStatus.TFGError.ErrorDownloading)
        }
    }
}

class StartRecordingUseCase(private val recorderController: RecorderController) :
        () -> Result<AppStatus> {
    override fun invoke(): Result<AppStatus> {
        return if (recorderController.startRecording().isSuccess) {
            Result.success(AppStatus.Recording)
        } else {
            Result.success(AppStatus.TFGError.ErrorRecording)
        }
    }
}

class StopRecordingUseCase(private val recorderController: RecorderController) :
        () -> Result<AppStatus> {
    override fun invoke(): Result<AppStatus> {
        return if(recorderController.stopRecording().isSuccess) {
            Result.success(AppStatus.StopRecording)
        } else {
            Result.success(AppStatus.TFGError.ErrorRecording)
        }
    }
}

class StartPlayingUseCase(private val recorderController: RecorderController) :
        () -> Result<AppStatus> {
    override fun invoke(): Result<AppStatus> {
        return if (recorderController.startPlaying().isSuccess) {
            Result.success(AppStatus.Playing)
        } else {
            Result.success(AppStatus.TFGError.ErrorPlaying)
        }
    }
}

class StopPlayingUseCase(private val recorderController: RecorderController) :
        () -> Result<AppStatus> {
    override fun invoke(): Result<AppStatus> {
        return if(recorderController.stopPlaying().isSuccess) {
            Result.success(AppStatus.StopPlaying)
        } else {
            Result.success(AppStatus.TFGError.ErrorPlaying)
        }
    }
}

class ViewPdfUseCase(private val fileController: FileController): () -> Result<AppStatus> {
    override fun invoke(): Result<AppStatus> {
        return if(fileController.viewPdf().isSuccess) {
            Result.success(AppStatus.DownloadingDone)
        } else {
            Result.success(AppStatus.TFGError.GeneralError)
        }
    }
}

class SharePdfUseCase(private val fileController: FileController): () -> Result<AppStatus> {
    override fun invoke(): Result<AppStatus> {
        return if(fileController.sharePdf().isSuccess) {
            Result.success(AppStatus.DownloadingDone)
        } else {
            Result.success(AppStatus.TFGError.GeneralError)
        }
    }
}

sealed class AppStatus {
    object Idle : AppStatus()
    object Recording : AppStatus()
    object Playing : AppStatus()
    object StopRecording : AppStatus()
    object StopPlaying : AppStatus()
    object Uploading : AppStatus()
    object Downloading: AppStatus()
    object UploadingDone : AppStatus()
    object DownloadingDone : AppStatus()
    sealed class TFGError: AppStatus() {
        object ErrorUploading : TFGError()
        object ErrorDownloading : TFGError()
        object ErrorRecording : TFGError()
        object ErrorPlaying : TFGError()
        object GeneralError : TFGError()
    }
}