package com.leonardpark.pixel

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.appcompat.widget.LinearLayoutCompat
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

  // region UI

  private val statusBarBg: View
    get() = findViewById(R.id.status_bar_bg)

  val camera: CameraView
    get() = findViewById(R.id.camera_view)

  // region Video Counter
  val videoCounterLayoutFl: FrameLayout
    get() = findViewById(R.id.video_counter_layout_fl)

  val videoCounterProgressBar: ProgressBar
    get() = findViewById(R.id.video_pbr)

  val videoCounter: AppCompatTextView
    get() = findViewById(R.id.video_counter)
  // endregion

  // region control
  private val flash: FrameLayout
    get() = findViewById(R.id.flash)

  private val clickMeBg: AppCompatImageView
    get() = findViewById(R.id.clickmebg)

  private val clickMe: AppCompatImageView
    get() = findViewById(R.id.clickme)

  val front: AppCompatImageView
    get() = findViewById(R.id.front)
  // endregion

  val messageBottom: AppCompatTextView
    get() = findViewById(R.id.message_bottom)

  private val tabLayout: TabLayout
    get() = findViewById(R.id.tab_layout)

  // endregion

  private var statusBarHeight = 0

  private var options: Options = Options.init()

  // region video values
  var videoCounterProgress = 0
  var maxVideoDuration = 40000
  var videoCounterHandler: Handler = Handler()
  var videoCounterRunnable: Runnable = Runnable { }
  // endregion

  var flashDrawable = R.drawable.ic_flash_off_black_24dp

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_camera)

    initialize()
  }

  override fun onRestart() {
    super.onRestart()
    camera.open()
    camera.mode = Mode.PICTURE
  }

  override fun onResume() {
    super.onResume()
    camera.open()
    camera.mode = Mode.PICTURE
  }

  override fun onPause() {
    camera.close()
    super.onPause()
  }

  override fun onDestroy() {
    super.onDestroy()
    camera.destroy()
  }

  private fun initialize() {
    // region get options
    try {
      options = intent.getSerializableExtra(OPTIONS) as Options
    } catch (e: Exception) {
      e.printStackTrace()
    }
    maxVideoDuration = options.videoDurationLimitInSeconds * 1000
    messageBottom.text = if (options.isExcludeVideos) {
      "Drag images up for gallery"
    } else {
      "Hold for video, tap for photo"
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
            camera.flash = when (flashDrawable) {
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
        camera.facing = Facing.BACK
      } else {
        options.isFrontFacing = true
        camera.facing = Facing.FRONT
      }

    }
    // endregion

    // region Camera
    camera.apply {
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

          val dir = Environment.getExternalStoragePublicDirectory(filePath)
          if (!dir.exists()) {
            dir.mkdirs()
          }

          result.toFile(File(dir, fileName)) {
            Utility.vibe(this@CameraActivity, 50)
            Utility.scanPhoto(this@CameraActivity, it!!)
          }
        }

        override fun onVideoTaken(result: VideoResult) {
          Utility.vibe(this@CameraActivity, 50)
          Utility.scanPhoto(this@CameraActivity, result.file)
          camera.mode = Mode.PICTURE
        }

        override fun onVideoRecordingStart() {
          videoCounterLayoutFl.visibility = View.VISIBLE
          videoCounterProgress = 0
          videoCounterProgressBar.progress = 0
          videoCounterRunnable = object : Runnable {
            override fun run() {
              ++videoCounterProgress
              videoCounterProgressBar.progress = videoCounterProgress
              var min = 0
              var sec = "$videoCounterProgress"
              if (videoCounterProgress > 59) {
                min = videoCounterProgress / 60
                sec = "${videoCounterProgress - (60 * min)}"
              }
              sec = "0".repeat(2 - sec.length) + sec
              var counter = "${min}:${sec}"
              videoCounter.text = counter
              videoCounterHandler.postDelayed(this, 1000)
            }
          }
          videoCounterHandler.postDelayed(videoCounterRunnable, 1000)
          clickMe.animate().scaleX(1.2f).scaleY(1.2f).setDuration(300).setInterpolator(
            AccelerateDecelerateInterpolator()
          ).start()
          this@CameraActivity.flash.animate().alpha(0f).setDuration(300)
            .setInterpolator(AccelerateDecelerateInterpolator()).start()
          messageBottom.animate().alpha(0f).setDuration(300)
            .setInterpolator(AccelerateDecelerateInterpolator()).start()
          front.animate().alpha(0f).setDuration(300)
            .setInterpolator(AccelerateDecelerateInterpolator()).start()
        }

        override fun onVideoRecordingEnd() {
          videoCounterLayoutFl.visibility = View.GONE
          videoCounterHandler.removeCallbacks(videoCounterRunnable)
          clickMe.animate().scaleX(1f).scaleY(1f).setDuration(300)
            .setInterpolator(AccelerateDecelerateInterpolator()).start()
          messageBottom.animate().scaleX(1f).scaleY(1f).setDuration(300)
            .setInterpolator(AccelerateDecelerateInterpolator()).start()
          this@CameraActivity.flash.animate().alpha(1f).setDuration(300)
            .setInterpolator(AccelerateDecelerateInterpolator()).start()
          front.animate().alpha(1f).setDuration(300)
            .setInterpolator(AccelerateDecelerateInterpolator()).start()
        }
      })
    }
    // endregion

    // init onclick
    onClickMethods()
  }

  private fun onClickMethods() {

    // TODO: create custom to fix warning
    clickMe.setOnTouchListener { _, event ->
      if (event.action == MotionEvent.ACTION_UP) {
        clickMeBg.visibility = View.GONE
        clickMeBg.animate().scaleX(1f).scaleY(1f).setDuration(300)
          .setInterpolator(AccelerateDecelerateInterpolator()).start()
        clickMe.animate().scaleX(1f).scaleY(1f).setDuration(300)
          .setInterpolator(AccelerateDecelerateInterpolator()).start()

        if (camera.isTakingVideo) {
          camera.stopVideo()
        }

      } else if (event.action == MotionEvent.ACTION_DOWN) {
        clickMeBg.visibility = View.VISIBLE
        clickMeBg.animate().scaleX(1.2f).scaleY(1.2f).setDuration(300)
          .setInterpolator(AccelerateDecelerateInterpolator()).start()
        clickMe.animate().scaleX(1.2f).scaleY(1.2f).setDuration(300)
          .setInterpolator(AccelerateDecelerateInterpolator()).start()
      }

      return@setOnTouchListener false
    }

    clickMe.setOnLongClickListener(object : View.OnLongClickListener {
      override fun onLongClick(v: View?): Boolean {
        if (options.isExcludeVideos) return false

        camera.mode = Mode.VIDEO
        val fileName = "VID_" +
            SimpleDateFormat("yyyyMMdd_HHmmSS", Locale.ENGLISH).format(Date()) +
            ".mp4"
        val filePath = "DCIM/" + options.path

        val dir = Environment.getExternalStoragePublicDirectory(filePath)
        if (!dir.exists()) {
          dir.mkdirs()
        }

        videoCounterProgressBar.max = maxVideoDuration / 1000
        videoCounterProgressBar.invalidate()

        camera.takeVideo(File(dir, fileName), maxVideoDuration)
        return true
      }
    })

    clickMe.setOnClickListener {

      camera.mode = Mode.PICTURE
      camera.takePicture()

      ObjectAnimator.ofFloat(
        camera,
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
      camera.takePicture()
    }
  }

}