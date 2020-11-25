package com.leonardpark.pixel

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.leonardpark.pixel.interfaces.WorkFinish
import com.leonardpark.pixel.utility.PermUtil

class PhotoActivity : AppCompatActivity() {

  companion object {

    private const val OPTIONS = "options"

    @JvmStatic
    fun start(context: Fragment, options: Options) {
      PermUtil.checkForCameraWritePermissions(context, object : WorkFinish {
        override fun onWorkFinish(check: Boolean) {
          context.startActivityForResult(
            Intent(
              context.activity,
              PhotoActivity::class.java
            ).apply {
              putExtra(OPTIONS, options)
            }, options.requestCode
          )
        }
      })
    }

    @JvmStatic
    fun start(context: Fragment, requestCode: Int) {
      start(context, Options.init().setRequestCode(requestCode).setCount(1))
    }

    @JvmStatic
    fun start(context: FragmentActivity, options: Options) {
      PermUtil.checkForCameraWritePermissions(context, object : WorkFinish {
        override fun onWorkFinish(check: Boolean) {
          context.startActivityForResult(
            Intent(
              context,
              PhotoActivity::class.java
            ).apply {
              putExtra(OPTIONS, options)
            }, options.requestCode
          )
        }
      })
    }
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_photo)
  }
}