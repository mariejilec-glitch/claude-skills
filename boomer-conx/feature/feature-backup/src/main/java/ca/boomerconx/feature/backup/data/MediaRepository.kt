package ca.boomerconx.feature.backup.data

import android.content.ContentResolver
import android.net.Uri
import android.provider.MediaStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class MediaItem(
    val uri: Uri,
    val displayName: String,
    val size: Long,
    val dateAdded: Long
)

class MediaRepository @Inject constructor(
    private val contentResolver: ContentResolver
) {
    suspend fun getPhotoPaths(): List<MediaItem> = withContext(Dispatchers.IO) {
        val photos = mutableListOf<MediaItem>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE,
            MediaStore.Images.Media.DATE_ADDED
        )

        contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection, null, null,
            "${MediaStore.Images.Media.DATE_ADDED} DESC"
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
            val sizeColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
            val dateColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATE_ADDED)

            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id.toString())
                photos.add(
                    MediaItem(
                        uri = uri,
                        displayName = cursor.getString(nameColumn),
                        size = cursor.getLong(sizeColumn),
                        dateAdded = cursor.getLong(dateColumn)
                    )
                )
            }
        }
        photos
    }
}
