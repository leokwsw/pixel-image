package com.leonardpark.androidimage

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.leonardpark.pixel.Options
import com.leonardpark.pixel.CameraActivity
import com.leonardpark.pixel.utility.PermUtil
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList

class MainActivity : AppCompatActivity() {

  private val requestCodePicker = 100
  private lateinit var adapter: Adapter
  private lateinit var options: Options
  private var returnValue = ArrayList<String>()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    setSupportActionBar(toolbar)

    options = Options.init()
      .setRequestCode(requestCodePicker)
      .setCount(5)
      .setFrontFacing(false)
      .setPreSelectedUrls(returnValue)
      .setExcludeVideos(false)
      .setSpanCount(4)
      .setVideoDurationLimitInSeconds(30)
      .setScreenOrientation(Options.SCREEN_ORIENTATION_PORTRAIT)
      .setPath("Pixel")

    recyclerView.layoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
    adapter = Adapter(this)
    recyclerView.adapter = adapter

    fab.setOnClickListener {
      options.preSelectedUrls = returnValue
      startActivity()
    }
  }

  private fun startActivity() {
    CameraActivity.start(this, options)
  }

  override fun onRequestPermissionsResult(
    requestCode: Int,
    permissions: Array<out String>,
    grantResults: IntArray
  ) {
    when (requestCode) {
      PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          startActivity()
        } else {
          Toast.makeText(this, "Approve permissions to open Image Picker", Toast.LENGTH_LONG).show()
        }
      }
    }
  }

  override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    when (requestCode) {
      requestCodePicker -> {
        if (resultCode == Activity.RESULT_OK) {
          returnValue = data?.getStringArrayListExtra(CameraActivity.IMAGE_RESULTS)!!
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