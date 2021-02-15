package com.leonardpark.pixel.gallery

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.leonardpark.pixel.R
import com.leonardpark.pixel.interfaces.WorkFinish
import com.leonardpark.pixel.modals.Img
import com.leonardpark.pixel.utility.PermUtil
import kotlinx.android.synthetic.main.activity_photo.*
import java.io.File

class GalleryActivity : AppCompatActivity() {

  companion object {

    private const val OPTIONS = "options"

    var IMAGE_RESULTS = "image_results"

    fun start(context: Fragment, options: GalleryOptions) {
      PermUtil.checkForGalleryPermissions(context, object : WorkFinish {
        override fun onWorkFinish(check: Boolean) {
          context.startActivityForResult(
            Intent(
              context.activity,
              GalleryActivity::class.java
            ).apply {
              putExtra(OPTIONS, options)
            }, options.requestCode
          )
        }
      })
    }

    fun start(context: Fragment, requestCode: Int) {
      start(context, GalleryOptions.init().setRequestCode(requestCode).setCount(1))
    }

    fun start(context: FragmentActivity, options: GalleryOptions) {
      PermUtil.checkForGalleryPermissions(context, object : WorkFinish {
        override fun onWorkFinish(check: Boolean) {
          context.startActivityForResult(
            Intent(
              context,
              GalleryActivity::class.java
            ).apply {
              putExtra(OPTIONS, options)
            }, options.requestCode
          )
        }
      })
    }
  }

  private var options: GalleryOptions = GalleryOptions.init()

  private var selectionList: ArrayList<Img> = ArrayList()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_photo)

    setSupportActionBar(toolbar)

    initialize()
  }

  private fun initialize() {
    try {
      options = intent.getSerializableExtra(OPTIONS) as GalleryOptions
    } catch (e: Exception) {
      e.printStackTrace()
    }

    val galleryAlbums: ArrayList<GalleryAlbums> = ArrayList()
    val albumsNames: ArrayList<String> = ArrayList()
    val photoList: ArrayList<GalleryData> = ArrayList()

    val imagesCursor = contentResolver.query(
      MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
      arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DATA,
        MediaStore.Images.Media.DATE_ADDED,
        MediaStore.Files.FileColumns.DATE_ADDED,
        MediaStore.Files.FileColumns.DATA,
        MediaStore.Files.FileColumns.MEDIA_TYPE,
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.TITLE,
        MediaStore.Files.FileColumns.PARENT,
        MediaStore.Images.Media.DATE_MODIFIED
      ),
      null,
      null,
      null
    )

    try {
      if (imagesCursor != null && imagesCursor.count > 0) {
        if (imagesCursor.moveToFirst()) {
          val idColumn = imagesCursor.getColumnIndex(MediaStore.Images.Media._ID)
          val dataColumn = imagesCursor.getColumnIndex(MediaStore.Images.Media.DATA)
          val dateAddedColumn = imagesCursor.getColumnIndex(MediaStore.Images.Media.DATE_ADDED)
          do {
            val id = imagesCursor.getString(idColumn)
            val data = imagesCursor.getString(dataColumn)
            val dateAdded = imagesCursor.getString(dateAddedColumn)

            val galleryData = GalleryData()
            galleryData.albumName = File(data).parentFile!!.name
            galleryData.photoUri = data
            galleryData.id = Integer.valueOf(id)
            galleryData.mediaType = MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE
            galleryData.dateAdded = dateAdded

            if (albumsNames.contains(galleryData.albumName)) {
              for (album in galleryAlbums) {
                if (album.name == galleryData.albumName) {
                  galleryData.albumId = album.id
                  album.albumPhotos.add(galleryData)
                  photoList.add(galleryData)
                  break
                }
              }
            } else {
              val album =
                GalleryAlbums()
              album.id = galleryData.id
              galleryData.albumId = galleryData.id
              album.name = galleryData.albumName
              album.coverUri = galleryData.photoUri
              album.albumPhotos.add(galleryData)

              photoList.add(galleryData)
              galleryAlbums.add(album)
              albumsNames.add(galleryData.albumName)
            }
          } while (imagesCursor.moveToNext())
        }
        imagesCursor.close()
      } else {
        Log.d("testmo", "error")
      }
    } catch (e: Exception) {
      Log.e("testmo", e.toString())
    } finally {
      photoList.sortWith(compareByDescending { File(it.photoUri).lastModified() })
      recyclerView.apply {
        layoutManager = GridLayoutManager(context, 4)
        adapter = GalleryAdapter(
          context,
          photoList,
          object : GalleryInterface {
            override fun onItemClickListener(add: Boolean, uri: String) {
              val img = Img("", "", uri, "", 1)
              if (add){
                selectionList.add(img)
              } else {
                selectionList.remove(img)
              }
            }
          },
          options.count
        )
      }
    }

    fab_add.setOnClickListener {
      val list = ArrayList<String>()
      for (i in selectionList) list.add(i.url)
      val resultIntent = Intent()
      resultIntent.putStringArrayListExtra(IMAGE_RESULTS, list)
      setResult(RESULT_OK, resultIntent)
      finish()
    }
  }
}