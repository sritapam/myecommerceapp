package com.henrypeya.data.service.imageupload

import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Fake implementation of CloudinaryService for demonstration and testing.
 * It simulates an image upload by returning a fixed placeholder URL after a delay.
 */
@Singleton
class FakeCloudinaryService @Inject constructor() : CloudinaryService {

    private val FIXED_IMAGE_URL = "https://placehold.co/200x200/0000FF/FFFFFF?text=PROFILE"

    override suspend fun uploadImage(imageData: Any): String {
        delay(1500)
        println("FakeCloudinaryService: Simulating upload of image data: $imageData. Returning fixed URL.")
        return FIXED_IMAGE_URL //TODO : Refactorizar al tener el dato real
    }
}
