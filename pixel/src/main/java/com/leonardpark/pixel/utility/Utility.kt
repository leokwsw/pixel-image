package com.leonardpark.pixel.utility

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.otaliastudios.cameraview.size.Size
import java.io.File

class Utility {

  companion object {

    var HEIGHT = 0
    var WIDTH = 0

    private val pathDir: String? = null

    fun setupStatusBarHidden(appCompatActivity: AppCompatActivity) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        val w = appCompatActivity.window
        w.decorView.systemUiVisibility = (View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN)
        w.setFlags(
          WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
          WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
          w.statusBarColor = Color.TRANSPARENT
        }
      }
    }

    fun showStatusBar(appCompatActivity: AppCompatActivity) {
      synchronized(appCompatActivity) {
        appCompatActivity.window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
      }
    }

    fun hideStatusBar(appCompatActivity: AppCompatActivity) {
      synchronized(appCompatActivity) {
        appCompatActivity.window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
      }
    }

    fun getStatusBarSizePort(check: AppCompatActivity): Int {
      // getRealMetrics is only available with API 17 and +
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
        var result = 0
        val res = check.baseContext.resources
        val resourceId = res.getIdentifier("status_bar_height", "dimen", "android")
        if (resourceId > 0) {
          result = check.resources.getDimensionPixelSize(resourceId)
        }
        return result
      }
      return 0
    }

    fun vibe(c: Context, l: Long) {
      (c.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator).vibrate(l)
    }

    fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
      val width = bm.width
      val height = bm.height
      val scaleWidth = newWidth.toFloat() / width
      val scaleHeight = newHeight.toFloat() / height
      // CREATE A MATRIX FOR THE MANIPULATION
      val matrix = Matrix()
      // RESIZE THE BIT MAP
      matrix.postScale(scaleWidth, scaleHeight)

      // "RECREATE" THE NEW BITMAP
      val resizedBitmap = Bitmap.createBitmap(
        bm, 0, 0, width, height, matrix, false
      )
      return resizedBitmap.copy(Bitmap.Config.RGB_565, false)
    }

    fun scanPhoto(pixel: Context, photo: File) {
      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        val scanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE)
        val contentUri = Uri.fromFile(photo)
        scanIntent.data = contentUri
        pixel.sendBroadcast(scanIntent)
      } else {
        pixel.sendBroadcast(
          Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(photo.absolutePath))
        )
      }
    }

    fun gcd(p: Int, q: Int): Int {
      return if (q == 0) p else gcd(q, p % q)
    }

    fun ratio(a: Int, b: Int): Size? {
      val gcd = gcd(a, b)
      return if (a > b) {
        showAnswer(a / gcd, b / gcd)
        Size(a / gcd, b / gcd)
      } else {
        Size(b / gcd, a / gcd)
      }
    }

    fun showAnswer(a: Int, b: Int) {
      Log.e("show ratio", "->  $a $b")
    }
  }

}