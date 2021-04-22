package com.leonardpark.pixel.gallery

import android.os.Parcelable
import android.provider.MediaStore
import kotlinx.android.parcel.Parcelize
import java.text.SimpleDateFormat
import java.util.*

@Parcelize
data class GalleryData(
  var id: Int = 0,
  var albumName: String = "",
  var photoUri: String = "",
  var albumId: Int = 0,

  var isSelected: Boolean = false,
  var isEnabled: Boolean = true,

  var dateAdded: String = "",
  var mediaType: Int = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
) : Parcelable {
  fun getDateDifference(calendar: Calendar): String? {
    val d = calendar.time
    val lastMonth = Calendar.getInstance()
    val lastWeek = Calendar.getInstance()
    val recent = Calendar.getInstance()
    lastMonth.add(Calendar.DAY_OF_MONTH, -Calendar.DAY_OF_MONTH)
    lastWeek.add(Calendar.DAY_OF_MONTH, -7)
    recent.add(Calendar.DAY_OF_MONTH, -2)
    return if (calendar.before(lastMonth)) {
      SimpleDateFormat("MMMM", Locale.getDefault()).format(d)
    } else if (calendar.after(lastMonth) && calendar.before(lastWeek)) {
      "Last Month"
    } else if (calendar.after(lastWeek) && calendar.before(recent)) {
      "Last Week"
    } else {
      "Recent"
    }
  }
}