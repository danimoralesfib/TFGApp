package com.tfgrecorder

import androidx.lifecycle.*
import com.tfgrecorder.usecases.*
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

class MainActivityViewModel(
    private val coroutineContext: CoroutineContext,
    private val uploadFileUseCase: UploadFileUseCase,
    private val downloadFileUseCase: DownloadFileUseCase,
    private val startRecordingUseCase: StartRecordingUseCase,
    private val stopRecordingUseCase: StopRecordingUseCase,
    private val startPlayingUseCase: StartPlayingUseCase,
    private val stopPlayingUseCase: StopPlayingUseCase,
    private val viewPdfUseCase: ViewPdfUseCase,
    private val sharePdfUseCase: SharePdfUseCase
) : ViewModel() {

    private val status: MutableLiveData<AppStatus> = MutableLiveData(AppStatus.Idle)
    val uiModel: LiveData<UiMapper.UiModel> =
        Transformations.map(status) { uiMapper.map(it) }

    private val uiMapper: UiMapper = UiMapper()

    fun onRecordingButtonClicked() {
        if (status.value is AppStatus.Recording) {
            stopRecordingUseCase().fold({ status.postValue(it) }, {})
        } else {
            startRecordingUseCase().fold({ status.postValue(it) }, {})
        }
    }

    fun onPlayingButtonClicked() {
        if (status.value is AppStatus.Playing) {
            stopPlayingUseCase().fold({ status.postValue(it) }, {})
        } else {
            startPlayingUseCase().fold({ status.postValue(it) }, {})
        }
    }

    fun onProcessButtonClicked() = viewModelScope.launch(coroutineContext) {
        when (status.value) {
            is AppStatus.DownloadingDone -> openPdfView()
            is AppStatus.UploadingDone -> {
                status.postValue(AppStatus.Downloading)
                downloadFileUseCase().fold({ status.postValue(it) }, {})
            }
            else -> {
                status.postValue(AppStatus.Uploading)
                uploadFileUseCase().fold({ status.postValue(it) }, {})
            }
        }
    }

    private fun openPdfView() {
        viewPdfUseCase().fold({status.postValue(it)}, {})
    }

    fun onShareButtonClicked() {
        sharePdfUseCase().fold({status.postValue(it)}, {})
    }

    class MainActivityViewModelFactory(
        private val coroutineContext: CoroutineContext,
        private val uploadFileUseCase: UploadFileUseCase,
        private val downloadFileUseCase: DownloadFileUseCase,
        private val startRecordingUseCase: StartRecordingUseCase,
        private val stopRecordingUseCase: StopRecordingUseCase,
        private val startPlayingUseCase: StartPlayingUseCase,
        private val stopPlayingUseCase: StopPlayingUseCase,
        private val viewPdfUseCase: ViewPdfUseCase,
        private val sharePdfUseCase: SharePdfUseCase
    ) : ViewModelProvider.Factory {

        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
                MainActivityViewModel(
                    coroutineContext,
                    uploadFileUseCase,
                    downloadFileUseCase,
                    startRecordingUseCase,
                    stopRecordingUseCase,
                    startPlayingUseCase,
                    stopPlayingUseCase,
                    viewPdfUseCase,
                    sharePdfUseCase
                ) as T
            } else {
                throw IllegalArgumentException("ViewModel Not Found")
            }
        }
    }

    class UiMapper {

        data class UiModel(
            val recordingButtonText: String,
            val playingButtonText: String,
            val processAudioButtonText: String,
            val currentStatusText: String,
            val isRecordingButtonEnabled: Boolean,
            val isPlayingButtonEnabled: Boolean,
            val isProcessButtonEnabled: Boolean,
            val isDialogVisible: Boolean = false,
            val isDialogWithTimer: Boolean = false,
            val dialogText: String = "",
            val isShareButtonVisible: Boolean = false
        )

        fun map(appStatus: AppStatus): UiModel {
            return when (appStatus) {
                AppStatus.Idle -> provideIdleStatusUiModel()
                AppStatus.Recording -> provideRecordingStatusUiModel()
                AppStatus.Playing -> providePlayingStatusUiModel()
                AppStatus.StopRecording -> provideStopRecordingStatusUiModel()
                AppStatus.StopPlaying -> provideStopPlayingStatusUiModel()
                AppStatus.Uploading -> provideUploadingStatusUiModel()
                AppStatus.UploadingDone -> provideUploadingDoneStatusUiModel()
                AppStatus.DownloadingDone -> provideDownladingDoneStatusUiModel()
                AppStatus.Downloading -> provideDownloadingStatusUiModel()
                AppStatus.TFGError.ErrorUploading -> provideErrorUploadingStatusUiModel()
                AppStatus.TFGError.ErrorDownloading -> provideErrorDownloadingStatusUiModel()
                AppStatus.TFGError.ErrorRecording -> provideErrorRecordingStatusUiModel()
                AppStatus.TFGError.ErrorPlaying -> provideErrorPlayingStatusUiModel()
                AppStatus.TFGError.GeneralError -> provideGeneralErrorStatusUiModel()
            }
        }

        private fun provideGeneralErrorStatusUiModel() = UiModel(
            recordingButtonText = "Start Recording",
            playingButtonText = "Start Playing",
            processAudioButtonText = "Process audio",
            currentStatusText = "General error",
            isRecordingButtonEnabled = true,
            isPlayingButtonEnabled = true,
            isProcessButtonEnabled = true,
            isDialogVisible = true,
            dialogText = "General error",
            isDialogWithTimer = true
        )

        private fun provideErrorPlayingStatusUiModel() = UiModel(
            recordingButtonText = "Start Recording",
            playingButtonText = "Start Playing",
            processAudioButtonText = "Process audio",
            currentStatusText = "Error playing",
            isRecordingButtonEnabled = true,
            isPlayingButtonEnabled = true,
            isProcessButtonEnabled = true,
            isDialogVisible = true,
            dialogText = "Error while playing try again please",
            isDialogWithTimer = true
        )

        private fun provideErrorRecordingStatusUiModel() = UiModel(
            recordingButtonText = "Start Recording",
            playingButtonText = "Start Playing",
            processAudioButtonText = "Process audio",
            currentStatusText = "Error recording",
            isRecordingButtonEnabled = true,
            isPlayingButtonEnabled = true,
            isProcessButtonEnabled = true,
            isDialogVisible = true,
            dialogText = "Error while recording try again please",
            isDialogWithTimer = true
        )

        private fun provideErrorDownloadingStatusUiModel() = UiModel(
            recordingButtonText = "Start Recording",
            playingButtonText = "Start Playing",
            processAudioButtonText = "Process audio",
            currentStatusText = "Error downloading",
            isRecordingButtonEnabled = true,
            isPlayingButtonEnabled = true,
            isProcessButtonEnabled = true,
            isDialogVisible = true,
            dialogText = "Error while downloading try again please",
            isDialogWithTimer = true
        )

        private fun provideErrorUploadingStatusUiModel() = UiModel(
            recordingButtonText = "Start Recording",
            playingButtonText = "Start Playing",
            processAudioButtonText = "Process audio",
            currentStatusText = "Error uploading",
            isRecordingButtonEnabled = true,
            isPlayingButtonEnabled = true,
            isProcessButtonEnabled = true,
            isDialogVisible = true,
            dialogText = "Error while recording try again",
            isDialogWithTimer = true
        )

        private fun provideDownladingDoneStatusUiModel() = UiModel(
            recordingButtonText = "Start Recording",
            playingButtonText = "Start Playing",
            processAudioButtonText = "View score",
            currentStatusText = "Score Downloaded",
            isRecordingButtonEnabled = true,
            isPlayingButtonEnabled = true,
            isProcessButtonEnabled = true,
            isDialogVisible = false,
            isShareButtonVisible = true
        )

        private fun provideDownloadingStatusUiModel() = UiModel(
            recordingButtonText = "Start Recording",
            playingButtonText = "Start Playing",
            processAudioButtonText = "Process audio",
            currentStatusText = "Downloading",
            isRecordingButtonEnabled = false,
            isPlayingButtonEnabled = false,
            isProcessButtonEnabled = false,
            isDialogVisible = true,
            dialogText = "Downloading"
        )

        private fun provideUploadingDoneStatusUiModel() = UiModel(
            recordingButtonText = "Start Recording",
            playingButtonText = "Start Playing",
            processAudioButtonText = "Download score",
            currentStatusText = "Audio Uploaded",
            isRecordingButtonEnabled = true,
            isPlayingButtonEnabled = true,
            isProcessButtonEnabled = true
        )

        private fun provideUploadingStatusUiModel() = UiModel(
            recordingButtonText = "Start Recording",
            playingButtonText = "Start Playing",
            processAudioButtonText = "Process audio",
            currentStatusText = "Uploading audio",
            isRecordingButtonEnabled = false,
            isPlayingButtonEnabled = false,
            isProcessButtonEnabled = false,
            isDialogVisible = true,
            dialogText = "Processing audio"
        )

        private fun provideStopPlayingStatusUiModel() = UiModel(
            recordingButtonText = "Start Recording",
            playingButtonText = "Start Playing",
            processAudioButtonText = "Process audio",
            currentStatusText = "Audio Recorded",
            isRecordingButtonEnabled = true,
            isPlayingButtonEnabled = true,
            isProcessButtonEnabled = true
        )

        private fun provideStopRecordingStatusUiModel() = UiModel(
            recordingButtonText = "Start Recording",
            playingButtonText = "Start Playing",
            processAudioButtonText = "Process audio",
            currentStatusText = "Audio Recorded",
            isRecordingButtonEnabled = true,
            isPlayingButtonEnabled = true,
            isProcessButtonEnabled = true
        )

        private fun providePlayingStatusUiModel() = UiModel(
            recordingButtonText = "Start Recording",
            playingButtonText = "Stop playing",
            processAudioButtonText = "Process audio",
            currentStatusText = "Playing",
            isRecordingButtonEnabled = false,
            isPlayingButtonEnabled = true,
            isProcessButtonEnabled = false
        )

        private fun provideRecordingStatusUiModel() = UiModel(
            recordingButtonText = "Stop Recording",
            playingButtonText = "Start Playing",
            processAudioButtonText = "Process audio",
            currentStatusText = "Recording",
            isRecordingButtonEnabled = true,
            isPlayingButtonEnabled = false,
            isProcessButtonEnabled = false
        )

        private fun provideIdleStatusUiModel() = UiModel(
            recordingButtonText = "Start Recording",
            playingButtonText = "Start Playing",
            processAudioButtonText = "Process audio",
            currentStatusText = "Idle",
            isRecordingButtonEnabled = true,
            isPlayingButtonEnabled = false,
            isProcessButtonEnabled = false
        )
    }
}
