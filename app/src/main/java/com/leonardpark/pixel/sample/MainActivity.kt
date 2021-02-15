package com.leonardpark.pixel.sample

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.leonardpark.pixel.camera.CameraActivity
import com.leonardpark.pixel.camera.CameraOptions
import com.leonardpark.pixel.gallery.GalleryActivity
import com.leonardpark.pixel.gallery.GalleryOptions
import com.leonardpark.pixel.utility.PermUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

  private val cameraRequestCodePicker = 100
  private val galleryRequestCodePicker = 200

  private lateinit var cameraOptions: CameraOptions
  private lateinit var galleryOptions: GalleryOptions

  private lateinit var adapter: Adapter
  private var returnValue = ArrayList<String>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setSupportActionBar(toolbar)

    recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    adapter = Adapter(this)
    recyclerView.adapter = adapter

    cameraOptions = CameraOptions.init()
      .setRequestCode(cameraRequestCodePicker)
      .setCount(5)
      .setFrontFacing(false)
      .setPreSelectedUrls(returnValue)
      .setExcludeVideos(false)
      .setSpanCount(4)
      .setVideoDurationLimitInSeconds(30)
      .setScreenOrientation(CameraOptions.SCREEN_ORIENTATION_PORTRAIT)
      .setPath("Pixel")

    fab_camera.setOnClickListener {
      cameraOptions.preSelectedUrls = returnValue
      CameraActivity.start(this, cameraOptions)
    }

    galleryOptions = GalleryOptions.init()
      .setRequestCode(galleryRequestCodePicker)
      .setCount(5)
      .setPreSelectedUrls(returnValue)
      .setSpanCount(4)

    fab_gallery.setOnClickListener {
      galleryOptions.preSelectedUrls = returnValue
      GalleryActivity.start(this, galleryOptions)
    }
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    when (requestCode) {
      PermUtil.CAMERA_REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          CameraActivity.start(this, cameraOptions)
        } else {
          Toast.makeText(this, "Approve permissions to open Camera", Toast.LENGTH_LONG).show()
        }
      }
      PermUtil.GALLERY_REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          GalleryActivity.start(this, galleryOptions)
        } else {
          Toast.makeText(this, "Approve permissions to open Gallery", Toast.LENGTH_LONG).show()
        }
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    when (requestCode) {
      cameraRequestCodePicker -> {
        if (resultCode == Activity.RESULT_OK) {
          val returnImage = data?.getStringArrayListExtra(CameraActivity.IMAGE_RESULTS)!!
          returnValue.addAll(returnImage)
          adapter.addImage(returnValue)
        }
      }
      galleryRequestCodePicker -> {
        if (requestCode == Activity.RESULT_OK) {
          val returnImage = data?.getStringArrayListExtra(GalleryActivity.IMAGE_RESULTS)!!
          returnValue.addAll(returnImage)
          adapter.addImage(returnValue)
        }
      }
    }
  }

  override fun onCreateOptionsMenu(menu: Menu): Boolean {
    // Inflate the menu; this adds items to the action bar if it is present.
    menuInflater.inflate(R.menu.menu_main, menu)
    return true
  }

  override fun onOptionsItemSelected(item: MenuItem): Boolean {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    return when (item.itemId) {
      R.id.action_settings -> true
      else -> super.onOptionsItemSelected(item)
    }
  }
}