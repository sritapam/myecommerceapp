package com.henrypeya.data.service.imageupload

/**
 * Interface for an image upload service (Cloudinary).
 * This abstraction allows swapping out the underlying service easily.
 */
interface CloudinaryService {
    suspend fun uploadImage(imageData: Any): String
}
