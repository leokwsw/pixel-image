package com.leonardpark.pixel.utility

import android.provider.MediaStore

class Constants {
  companion object {
    const val sScrollbarAnimDuration = 500

    @kotlin.jvm.JvmField
    var PROJECTION = arrayOf(
      MediaStore.Images.Media.DATA,
      MediaStore.Images.Media._ID,  //MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
      //MediaStore.Images.Media.BUCKET_ID,
      // MediaStore.Images.Media.DATE_TAKEN,
      MediaStore.Images.Media.DATE_ADDED,
      MediaStore.Images.Media.DATE_MODIFIED
    )

    @kotlin.jvm.JvmField
    var URI = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    @kotlin.jvm.JvmField
    var ORDERBY = MediaStore.Images.Media.DATE_MODIFIED + " DESC"

    @kotlin.jvm.JvmField
    var IMAGE_VIDEO_PROJECTION = arrayOf(
      MediaStore.Files.FileColumns.DATA,
      MediaStore.Files.FileColumns._ID,
      MediaStore.Files.FileColumns.PARENT,
      MediaStore.Files.FileColumns.DISPLAY_NAME,
      MediaStore.Files.FileColumns.DATE_ADDED,
      MediaStore.Files.FileColumns.DATE_MODIFIED,
      MediaStore.Files.FileColumns.MEDIA_TYPE,
      MediaStore.Files.FileColumns.MIME_TYPE,
      MediaStore.Files.FileColumns.TITLE
    )

    @kotlin.jvm.JvmField
    var IMAGE_VIDEO_SELECTION = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
        + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
        + " OR "
        + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
        + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)

    @kotlin.jvm.JvmField
    var IMAGE_SELECTION = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
        + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE)

    @kotlin.jvm.JvmField
    var IMAGE_VIDEO_URI = MediaStore.Files.getContentUri("external")

    @kotlin.jvm.JvmField
    var IMAGE_VIDEO_ORDERBY = MediaStore.Images.Media.DATE_MODIFIED + " DESC"
  }
}