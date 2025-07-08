package com.henrypeya.data.service.imageupload

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import android.util.Log

@Singleton
class CloudinaryServiceImpl @Inject constructor(
    private val context: Context
) : CloudinaryService {

    override suspend fun uploadImage(imageData: Any): String = withContext(Dispatchers.IO) {
        suspendCancellableCoroutine { continuation ->
            val dataToUpload: Uri = try {
                if (imageData is Bitmap) {
                    val filesDir = context.cacheDir
                    val tempFile = File(filesDir, "temp_upload_${System.currentTimeMillis()}.png")
                    FileOutputStream(tempFile).use { out ->
                        imageData.compress(Bitmap.CompressFormat.PNG, 100, out)
                    }
                    Uri.fromFile(tempFile)
                } else if (imageData is Uri) {
                    imageData
                } else if (imageData is String && (imageData.startsWith("http") || imageData.startsWith("content://") || imageData.startsWith("file://"))) {
                    Uri.parse(imageData)
                } else {
                    continuation.resumeWithException(IllegalArgumentException("Tipo de imagen no soportado para la subida."))
                    return@suspendCancellableCoroutine
                }
            } catch (e: Exception) {
                Log.e("CloudinaryService", "Error preparing image data: ${e.message}", e)
                continuation.resumeWithException(Exception("Error al preparar la imagen para la subida: ${e.localizedMessage}", e))
                return@suspendCancellableCoroutine
            }


            try {
                val request = MediaManager.get().upload(dataToUpload)
                    .unsigned("imagepeya")
                    .callback(object : UploadCallback {
                        override fun onStart(requestId: String) {
                            Log.d("CloudinaryService", "Upload started: $requestId")
                        }

                        override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {
                            val progress = (bytes * 100 / totalBytes).toInt()
                            Log.d("CloudinaryService", "Upload progress: $progress%")
                        }

                        override fun onSuccess(requestId: String, resultData: Map<*, *>?) {
                            val url = resultData?.get("secure_url") as? String
                            if (url != null) {
                                Log.i("CloudinaryService", "Upload successful. URL: $url")
                                continuation.resume(url)
                            } else {
                                val errorMsg = "Upload successful but secure_url is missing."
                                Log.e("CloudinaryService", errorMsg)
                                continuation.resumeWithException(IllegalStateException(errorMsg))
                            }
                            if (imageData is Bitmap) {
                                val tempFile = (dataToUpload as? Uri)?.path?.let { File(it) }
                                if (tempFile != null && tempFile.exists()) {
                                    tempFile.delete()
                                }
                            }
                        }

                        override fun onError(requestId: String, error: ErrorInfo) {
                            val errorMsg = "Upload failed: ${error.description}"
                            Log.e("CloudinaryService", errorMsg)
                            continuation.resumeWithException(Exception(errorMsg))
                            if (imageData is Bitmap) {
                                val tempFile = (dataToUpload as? Uri)?.path?.let { File(it) }
                                if (tempFile != null && tempFile.exists()) {
                                    tempFile.delete()
                                }
                            }
                        }

                        override fun onReschedule(requestId: String, error: ErrorInfo) {
                            Log.w("CloudinaryService", "Upload rescheduled: ${error.description}")
                        }
                    })

                request.dispatch()
            } catch (e: Exception) {
                Log.e("CloudinaryService", "Error during Cloudinary upload process: ${e.message}", e)
                continuation.resumeWithException(Exception("Error en el proceso de subida de Cloudinary: ${e.localizedMessage}", e))
                if (imageData is Bitmap) {
                    val tempFile = (dataToUpload as? Uri)?.path?.let { File(it) }
                    if (tempFile != null && tempFile.exists()) {
                        tempFile.delete()
                    }
                }
            }
        }
    }
}
