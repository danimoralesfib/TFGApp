package com.tfgrecorder

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders


private const val REQUEST_RECORD_AUDIO_PERMISSION = 200

class MainActivity : AppCompatActivity() {
    private var permissionToRecordAccepted = false
    private lateinit var viewModel: MainActivityViewModel

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        permissionToRecordAccepted = if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        } else {
            false
        }
        if (!permissionToRecordAccepted) finish()
    }


    override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)
        setContentView(R.layout.activity_main)
        viewModel = ViewModelProviders.of(
            this,
            (applicationContext as TFGApp).serviceLocator.provideViewModelFactory()
        ).get(MainActivityViewModel::class.java)
        viewModel.uiModel.observe(this, Observer { populateView(it) })

        requestRecordingPermissions()
    }

    private fun requestRecordingPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE),
                REQUEST_RECORD_AUDIO_PERMISSION
            )
        }
    }

    private fun populateView(uiModel: MainActivityViewModel.UiMapper.UiModel) {
        findViewById<TextView>(R.id.statusTextView).apply {
            text = uiModel.currentStatusText
        }
        findViewById<Button>(R.id.recordButton).apply {
            text = uiModel.recordingButtonText
            isEnabled = uiModel.isRecordingButtonEnabled
            setOnClickListener { viewModel.onRecordingButtonClicked() }
        }
        findViewById<Button>(R.id.playButton).apply {
            text = uiModel.playingButtonText
            isEnabled = uiModel.isPlayingButtonEnabled
            setOnClickListener { viewModel.onPlayingButtonClicked() }
        }
        findViewById<Button>(R.id.processButton).apply {
            text = uiModel.processAudioButtonText
            isEnabled = uiModel.isProcessButtonEnabled
            setOnClickListener { viewModel.onProcessButtonClicked() }
        }

        findViewById<Button>(R.id.shareButton).apply {
            isVisible = uiModel.isShareButtonVisible
            setOnClickListener { viewModel.onShareButtonClicked() }
        }
    }
}



