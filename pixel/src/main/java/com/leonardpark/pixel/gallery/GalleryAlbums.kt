package com.leonardpark.pixel.gallery

data class GalleryAlbums(
  var id: Int = 0,
  var name: String = "",
  var coverUri: String = "",
  var albumPhotos: ArrayList<GalleryData> = ArrayList()
)