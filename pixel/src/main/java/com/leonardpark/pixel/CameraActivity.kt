package com.leonardpark.pixel

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
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
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.CameraView
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Audio
import com.otaliastudios.cameraview.controls.Mode
import com.otaliastudios.cameraview.size.AspectRatio
import com.otaliastudios.cameraview.size.SizeSelectors

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

  private var statusBarHeight = 0

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_camera)

    setUpStatusBarAndTabBar()

    setCamera()
    setFrontBtn()
    setFlashBtn()
    setClickBtn()
  }

  private fun setUpStatusBarAndTabBar() {
    statusBarHeight = Utility.getStatusBarSizePort(this)

    findViewById<View>(R.id.status_bar_bg).apply {
      layoutParams.height = statusBarHeight
      translationY = (-1 * statusBarHeight).toFloat()
      requestLayout()
    }

    Utility.setupStatusBarHidden(this)
    Utility.hideStatusBar(this)

    listOf("拍攝與錄影", "相簿").forEach {
      findViewById<TabLayout>(R.id.tab_layout).addTab(
        findViewById<TabLayout>(R.id.tab_layout).newTab().apply {
          text = it
        }
      )
    }
  }

  private fun setFrontBtn() {
    findViewById<AppCompatImageView>(R.id.front).setOnClickListener {
      val oa1 = ObjectAnimator.ofFloat(it, "scaleX", 1f, 0f).setDuration(150)
      val oa2 = ObjectAnimator.ofFloat(it, "scaleX", 0f, 1f).setDuration(150)

      oa1.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationEnd(animation: Animator) {
          super.onAnimationEnd(animation)
          findViewById<AppCompatImageView>(R.id.front).setImageResource(R.drawable.ic_photo_camera)
          oa2.start()
        }
      })

      oa1.start()
    }
  }

  private fun setFlashBtn() {
    var flashDrawable = R.drawable.ic_flash_off_black_24dp

    val flashIv = findViewById<FrameLayout>(R.id.flash).getChildAt(0) as ImageView
    findViewById<FrameLayout>(R.id.flash).setOnClickListener {
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
            flashIv.setImageResource(flashDrawable)
            flashIv.animate().translationY(0.toFloat()).setDuration(50).setListener(null).start()
          }
        })
    }
  }

  private fun setCamera() {
    findViewById<CameraView>(R.id.camera_view).apply {
      mode = Mode.PICTURE
      audio = Audio.OFF

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

      open()

      // add Camera Listener

      addCameraListener(object : CameraListener() {
        override fun onPictureTaken(result: PictureResult) {
          super.onPictureTaken(result)
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
  }

  private fun setClickBtn() {
    findViewById<ImageView>(R.id.clickme).setOnLongClickListener(object : View.OnLongClickListener {
      override fun onLongClick(v: View?): Boolean {
        findViewById<CameraView>(R.id.camera_view).mode = Mode.VIDEO

        return true
      }
    })

    findViewById<ImageView>(R.id.clickme).setOnClickListener {

      findViewById<CameraView>(R.id.camera_view).mode = Mode.PICTURE
      findViewById<CameraView>(R.id.camera_view).takePicture()

      ObjectAnimator.ofFloat(
        findViewById<CameraView>(R.id.camera_view),
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
    }
  }
}