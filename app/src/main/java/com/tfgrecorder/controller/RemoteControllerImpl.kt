package com.tfgrecorder.controller

import android.content.Context
import android.os.Environment
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File
import java.io.InputStream

class RemoteControllerImpl(
    private val context: Context,
    private val service: TFGService,
    private val fileName: String
) :
    RemoteController {
    override fun upload(): Result<Unit> {
        try {
            val multipartBody = provideRequest(fileName)
            val response = service.uploadFile(multipartBody)
            response.execute().also {
                return if (it.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(BackendError.UploadError)
                }
            }
        } catch (e: Exception) {
            return Result.failure(BackendError.GeneralError)
        }
    }

    override fun download(): Result<Unit> {
        try {
            val response = service.downloadFile()
            response.execute().also {
                return if (it.isSuccessful) {
                    it.body()?.byteStream()?.saveToFile("${context.getExternalFilesDir("/")?.absolutePath}/score_generated.pdf")
                    Result.success(Unit)
                } else {
                    Result.failure(BackendError.DownloadError)
                }
            }
        } catch (e: Exception) {
            return Result.failure(BackendError.GeneralError)
        }
    }

    private fun provideRequest(fileName: String): MultipartBody.Part {
        val file = File(fileName)
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file)
        return MultipartBody.Part.createFormData("file", file.name, requestFile)
    }

    private fun InputStream.saveToFile(file: String) = use { input ->
        File(file).outputStream().use { output ->
            input.copyTo(output)
        }
    }
}

interface RemoteController {
    fun upload(): Result<Unit>
    fun download(): Result<Unit>
}

interface TFGService {
    @Multipart
    @POST("upload")
    fun uploadFile(@Part file: MultipartBody.Part): Call<Unit>

    @POST("download")
    fun downloadFile(): Call<ResponseBody>
}

sealed class BackendError : Throwable() {
    object DownloadError : BackendError()
    object UploadError : BackendError()
    object GeneralError : BackendError()
}

class TFGRetrofit {
    lateinit var retrofit: Retrofit
    val isInitialized get() = ::retrofit.isInitialized
}
