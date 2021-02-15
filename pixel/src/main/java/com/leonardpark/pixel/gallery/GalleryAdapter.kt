package com.leonardpark.pixel.gallery

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.leonardpark.pixel.R
import kotlinx.android.synthetic.main.item_image.view.*
import java.io.File

class GalleryAdapter(
  private val context: Context,
  private val fullImageList: ArrayList<GalleryData>,
  private val listener: GalleryInterface,
  private var maxSelected: Int
) : RecyclerView.Adapter<GalleryAdapter.GalleryHolder>() {

  private val copyImageList: ArrayList<GalleryData> = ArrayList()

  init {
    Log.d("testmo", "mo : ${fullImageList.size}")
    copyImageList.addAll(fullImageList)
  }

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryHolder {
    return GalleryHolder(
      LayoutInflater.from(parent.context).inflate(
        R.layout.item_image,
        parent,
        false
      )
    )
  }

  private fun getSelectedCount(): Int = fullImageList.count { it.isSelected }

  override fun onBindViewHolder(holder: GalleryHolder, position: Int) {
    if (maxSelected != 0) {
      if (getSelectedCount() >= maxSelected) {
        copyImageList.filterNot { it.isSelected }.forEach { it.isEnabled = false }
      } else {
        copyImageList.forEach { it.isEnabled = true }
      }
    }

    //
//    val requestListener: RequestListener<Drawable> = object : RequestListener<Drawable> {
//      override fun onLoadFailed(
//        e: GlideException?,
//        model: Any?,
//        target: Target<Drawable>?,
//        isFirstResource: Boolean
//      ): Boolean {
//        holder.itemView.iv_image.alpha = 0.3f
//        holder.itemView.iv_image.isEnabled = false
//        holder.itemView.check_box.visibility = View.INVISIBLE
//        return false
//      }
//
//      override fun onResourceReady(
//        resource: Drawable?,
//        model: Any?,
//        target: Target<Drawable>?,
//        dataSource: DataSource?,
//        isFirstResource: Boolean
//      ): Boolean {
//        return false
//      }
//
//    }
//
    Glide.with(context)
      .load(Uri.fromFile(File(copyImageList[position].photoUri)))
      .apply(RequestOptions.centerCropTransform().override(100))
//      .transition(DrawableTransitionOptions.withCrossFade()).listener(requestListener)
      .into(holder.itemView.iv_image)
    //

    if (copyImageList[position].isEnabled) {
      holder.itemView.iv_image.alpha = 1.0f
      holder.itemView.iv_image.isEnabled = true
      holder.itemView.check_box.visibility = View.VISIBLE
    } else {
      holder.itemView.iv_image.alpha = 0.3f
      holder.itemView.iv_image.isEnabled = false
      holder.itemView.check_box.visibility = View.INVISIBLE
    }

    holder.itemView.check_box.setImageResource(
      if (copyImageList[position].isSelected) {
        R.drawable.ic_new_tick
      } else {
        R.drawable.ic_new_empty
      }
    )

    holder.itemView.iv_image.setOnClickListener {
      if (maxSelected != 0) {
        when {
          getSelectedCount() <= maxSelected -> {
            if (copyImageList[position].isSelected) {
              copyImageList[position].isSelected = false
              listener.onItemClickListener(false, copyImageList[position].photoUri)
              holder.itemView.check_box.setImageResource(R.drawable.ic_new_empty)
              if (getSelectedCount() == (maxSelected - 1) && !copyImageList[position].isSelected) {
                copyImageList.forEach { it.isEnabled = true }
                for ((index, item) in copyImageList.withIndex()) {
                  if (item.isEnabled && !item.isSelected) notifyItemChanged(index)
                }
              }
            } else {
              copyImageList[position].isSelected = true
              listener.onItemClickListener(true, copyImageList[position].photoUri)
              holder.itemView.check_box.setImageResource(R.drawable.ic_new_tick)
              if (getSelectedCount() == maxSelected && copyImageList[position].isSelected) {
                copyImageList.filterNot { it.isSelected }.forEach { it.isEnabled = false }
                for ((index, item) in copyImageList.withIndex()) {
                  if (!item.isEnabled) notifyItemChanged(index)
                }
              }
            }
          }
          getSelectedCount() > maxSelected -> {
            for (image in copyImageList) {
              copyImageList.filter { it.isSelected && !it.isEnabled }
                .forEach { it.isSelected = false }
            }
          }
          else -> {
            Log.d("testmo", "getSelectedCount : ${getSelectedCount()} ; maxSelected : $maxSelected")
          }
        }
      } else {
        if (copyImageList[position].isSelected) {
          copyImageList[position].isSelected = false
          holder.itemView.check_box.setImageResource(R.drawable.ic_new_empty)
          notifyItemChanged(position)
        } else {
          copyImageList[position].isSelected = true
          holder.itemView.check_box.setImageResource(R.drawable.ic_new_tick)
          notifyItemChanged(position)
        }
      }
    }

    // TODO: Dialog
  }

  override fun getItemCount(): Int = copyImageList.size

  class GalleryHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}