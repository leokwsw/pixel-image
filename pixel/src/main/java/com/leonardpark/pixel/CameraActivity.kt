package com.leonardpark.pixel

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.google.android.material.tabs.TabLayout
import com.leonardpark.pixel.interfaces.WorkFinish
import com.leonardpark.pixel.utility.PermUtil
import com.leonardpark.pixel.utility.Utility
import com.otaliastudios.cameraview.*
import com.otaliastudios.cameraview.controls.Audio
import com.otaliastudios.cameraview.controls.Facing
import com.otaliastudios.cameraview.controls.Flash
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.size.AspectRatio
import com.otaliastudios.cameraview.size.SizeSelectors
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraActivity : AppCompatActivity() {
  companion object {

    private const val OPTIONS = "options"

    @JvmStatic
    fun start(context: Fragment, options: Options) {
      PermUtil.checkForCameraWritePermissions(context, object : WorkFinish {
        override fun onWorkFinish(check: Boolean) {
          context.startActivityForResult(
            Intent(
              context.activity,
              CameraActivity::class.java
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
              CameraActivity::class.java
            ).apply {
              putExtra(OPTIONS, options)
            }, options.requestCode
          )
        }
      })
    }
  }

  val cameraView: CameraView
    get() = findViewById(R.id.camera_view)

  val statusBarBg: View
    get() = findViewById(R.id.status_bar_bg)

  val tabLayout: TabLayout
    get() = findViewById(R.id.tab_layout)

  val front: AppCompatImageView
    get() = findViewById(R.id.front)

  val flash: FrameLayout
    get() = findViewById(R.id.flash)

  val clickMe: ImageView
    get() = findViewById(R.id.clickme)

  private var statusBarHeight = 0

  private var options: Options = Options.init()

  var flashDrawable = R.drawable.ic_flash_off_black_24dp

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_camera)

    initialize()
  }

  override fun onRestart() {
    super.onRestart()
    cameraView.open()
    cameraView.mode = Mode.PICTURE
  }

  override fun onResume() {
    super.onResume()
    cameraView.open()
    cameraView.mode = Mode.PICTURE
  }

  override fun onPause() {
    cameraView.close()
    super.onPause()
  }

  override fun onDestroy() {
    super.onDestroy()
    cameraView.destroy()
  }

  private fun initialize() {
    // region get options
    try {
      options = intent.getSerializableExtra(OPTIONS) as Options
    } catch (e: Exception) {
      e.printStackTrace()
    }
    // endregion

    // region status bar
    statusBarHeight = Utility.getStatusBarSizePort(this)

    statusBarBg.apply {
      layoutParams.height = statusBarHeight
      translationY = (-1 * statusBarHeight).toFloat()
      requestLayout()
    }

    Utility.setupStatusBarHidden(this)
    Utility.hideStatusBar(this)

    listOf("拍攝與錄影", "相簿").forEach {
      tabLayout.addTab(
        tabLayout.newTab().apply {
          text = it
        }
      )
    }
    // endregion status bar

    // region Flash
    val flashIv = flash.getChildAt(0) as ImageView
    flash.setOnClickListener {
      flashIv.animate()
        .translationY(it.height.toFloat())
        .setDuration(100)
        .setListener(object : AnimatorListenerAdapter() {
          override fun onAnimationEnd(animation: Animator) {
            super.onAnimationEnd(animation)
            flashIv.translationY = -(it.height / 2).toFloat()
            flashDrawable = when (flashDrawable) {
              R.drawable.ic_flash_auto_black_24dp ->
                R.drawable.ic_flash_off_black_24dp
              R.drawable.ic_flash_off_black_24dp ->
                R.drawable.ic_flash_on_black_24dp
              else -> R.drawable.ic_flash_auto_black_24dp
            }
            cameraView.flash = when (flashDrawable) {
              R.drawable.ic_flash_off_black_24dp -> Flash.OFF
              R.drawable.ic_flash_on_black_24dp -> Flash.ON
              R.drawable.ic_flash_auto_black_24dp -> Flash.AUTO
              else -> Flash.AUTO
            }
            flashIv.setImageResource(flashDrawable)
            flashIv.animate().translationY(0.toFloat()).setDuration(50).setListener(null).start()
          }
        })
    }
    // endregion

    // region Front
    front.setOnClickListener {
      val oa1 = ObjectAnimator.ofFloat(it, "scaleX", 1f, 0f).setDuration(150)
      val oa2 = ObjectAnimator.ofFloat(it, "scaleX", 0f, 1f).setDuration(150)

      oa1.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          front.setImageResource(R.drawable.ic_photo_camera)
          oa2.start()
        }
      })

      oa1.start()

      if (options.isFrontFacing) {
        options.isFrontFacing = false
        cameraView.facing = Facing.BACK
      } else {
        options.isFrontFacing = true
        cameraView.facing = Facing.FRONT
      }

    }
    // endregion

    // region Camera
    cameraView.apply {
      mode = Mode.PICTURE
      if (options.isExcludeVideos) audio = Audio.OFF

      val width = SizeSelectors.minWidth(Utility.WIDTH)
      val height = SizeSelectors.minHeight(Utility.HEIGHT)

      val dimensions =
        SizeSelectors.and(width, height) // Matches sizes bigger than width X height

      val ratio = SizeSelectors.aspectRatio(AspectRatio.of(1, 2), 0f) // Matches 1:2 sizes.

      val ratio3 = SizeSelectors.aspectRatio(AspectRatio.of(2, 3), 0f) // Matches 2:3 sizes.

      val ratio2 = SizeSelectors.aspectRatio(AspectRatio.of(9, 18), 0f) // Matches 9:16 sizes.

      val result = SizeSelectors.or(
        SizeSelectors.and(ratio, dimensions),
        SizeSelectors.and(ratio2, dimensions),
        SizeSelectors.and(ratio3, dimensions)
      )

      setPictureSize(result)

      setVideoSize(result)

      setLifecycleOwner(this@CameraActivity)

      facing = if (options.isFrontFacing) {
        Facing.FRONT
      } else {
        Facing.BACK
      }

      open()

      // add Camera Listener

      addCameraListener(object : CameraListener() {
        override fun onPictureTaken(result: PictureResult) {
          val fileName = "IMG_" +
              SimpleDateFormat("yyyyMMdd_HHmmSS", Locale.ENGLISH).format(Date()) +
              ".jpg"
          val filePath = "DCIM/" + options.path

          if (System.isQ) {
            val resolver = this@CameraActivity.contentResolver

            val uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
              ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                put(MediaStore.MediaColumns.RELATIVE_PATH, filePath)
              }
            )

            result.toBitmap {
              val fos = resolver.openOutputStream(uri!!)
              if (it != null && fos != null) {
                it.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                fos.flush()
                fos.close()
              }
            }
          } else {
            // TODO: WIP to testing
            val dir = Environment.getExternalStoragePublicDirectory(filePath)
            if (!dir.exists()) {
              dir.mkdirs()
            }

            result.toFile(File(dir, fileName)) {
              Utility.vibe(this@CameraActivity, 50)
              Utility.scanPhoto(this@CameraActivity, it!!)
            }
          }
        }

        override fun onVideoTaken(result: VideoResult) {
          super.onVideoTaken(result)
        }

        override fun onVideoRecordingStart() {
          super.onVideoRecordingStart()
        }

        override fun onVideoRecordingEnd() {
          super.onVideoRecordingEnd()
        }
      })
    }
    // endregion

    // init onclick
    onClickMethods()
  }

  private fun onClickMethods() {
    clickMe.setOnLongClickListener(object : View.OnLongClickListener {
      override fun onLongClick(v: View?): Boolean {
        cameraView.mode = Mode.VIDEO

        return true
      }
    })

    clickMe.setOnClickListener {

      cameraView.mode = Mode.PICTURE
      cameraView.takePicture()

      ObjectAnimator.ofFloat(
        cameraView,
        "alpha",
        1f,
        0.5f,
        0.5f,
        1f
      ).apply {
        startDelay = 50L
        duration = 50L
        start()
      }
      cameraView.takePicture()
    }
  }

}