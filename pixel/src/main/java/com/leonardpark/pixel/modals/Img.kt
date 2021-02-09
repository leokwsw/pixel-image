package com.leonardpark.pixel.modals

class Img(
  var headerDate: String,
  var contentUrl: String,
  var url: String,
  var scrollerDate: String,
  var mediaType: Int = 1
) {
  var isSelected: Boolean = false
  var position: Int = 0
}