package com.leonardpark.pixel.sample

import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.ThumbnailUtils
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.image.view.*
import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.util.*

class Adapter(private val context: Context, private val callBack: AdapterInterface) :
  RecyclerView.Adapter<Adapter.Holder>() {

  private val list = ArrayList<String>()

  fun addImage(list: List<String>) {
    this.list.clear()
    this.list.addAll(list)
    notifyDataSetChanged()
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder =
    Holder(
      LayoutInflater
        .from(parent.context)
        .inflate(
          R.layout.image,
          parent,
          false
        )
    )

  override fun onBindViewHolder(holder: Holder, position: Int) {
    val view = holder.itemView
    val f = File(list[position])
    val len = f.absolutePath.length
    val extension = f.absolutePath.subSequence(len - 3, len)

    val bitmap = if (extension == "mp4" || extension == "mkv") {
      view.play.visibility = View.VISIBLE
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        ThumbnailUtils.createVideoThumbnail(f, Size(500, 500), null)
      } else {
        ThumbnailUtils.createVideoThumbnail(f.absolutePath, MediaStore.Video.Thumbnails.MINI_KIND)
      }
    } else {
      view.play.visibility = View.GONE
      BitmapFactory.decodeFile(f.absolutePath)
    }

    val roundedBitmapDrawable = RoundedBitmapDrawableFactory.create(context.resources, bitmap)

    val roundPx = bitmap!!.width.toFloat() * 0.06f

    roundedBitmapDrawable.cornerRadius = roundPx

    view.iv.setImageDrawable(roundedBitmapDrawable)

    view.iv.setOnClickListener {
      if (extension == "mp4" || extension == "mkv") {

      } else {
        callBack.onImageClicked(f)
      }
    }
  }

  override fun getItemCount() = list.size

  class Holder(itemView: View) : RecyclerView.ViewHolder(itemView)
}