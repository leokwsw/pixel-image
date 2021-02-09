package com.leonardpark.pixel

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.leonardpark.pixel.interfaces.WorkFinish
import com.leonardpark.pixel.modals.Img
import com.leonardpark.pixel.utility.PermUtil
import com.leonardpark.pixel.utility.Utility
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.PictureResult
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.*
import com.otaliastudios.cameraview.size.AspectRatio
import com.otaliastudios.cameraview.size.SizeSelectors
import kotlinx.android.synthetic.main.activity_camera.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class CameraActivity : AppCompatActivity() {
  companion object {

    private const val OPTIONS = "options"

    var IMAGE_RESULTS = "image_results"


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

    fun start(context: Fragment, requestCode: Int) {
      start(context, Options.init().setRequestCode(requestCode).setCount(1))
    }

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

  // endregion

  private var statusBarHeight = 0

  private var options: Options = Options.init()

  private var selectionList: ArrayList<Img> = ArrayList()

  // region video values
  var videoCounterProgress = 0
  var maxVideoDuration = 40000
  var videoCounterHandler: Handler = Handler(Looper.getMainLooper())
  var videoCounterRunnable: Runnable = Runnable { }
  // endregion

  var flashDrawable = R.drawable.ic_flash_off
  var hdrDrawable = R.drawable.ic_hdr_on

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_camera)

    initialize()
  }

  override fun onRestart() {
    super.onRestart()
    camera_view.open()
    camera_view.mode = Mode.PICTURE
  }

  override fun onResume() {
    super.onResume()
    camera_view.open()
    camera_view.mode = Mode.PICTURE
  }

  override fun onPause() {
    camera_view.close()
    super.onPause()
  }

  override fun onDestroy() {
    super.onDestroy()
    camera_view.destroy()
  }

  private fun initialize() {
    // region get options
    try {
      options = intent.getSerializableExtra(OPTIONS) as Options
    } catch (e: Exception) {
      e.printStackTrace()
    }
    maxVideoDuration = options.videoDurationLimitInSeconds * 1000

    message_bottom.visibility = if (options.isExcludeVideos) {
      View.GONE
    } else {
      View.VISIBLE
    }
    // endregion

    // region status bar
    statusBarHeight = Utility.getStatusBarSizePort(this)

    status_bar_bg.apply {
      layoutParams.height = statusBarHeight
      translationY = (-1 * statusBarHeight).toFloat()
      requestLayout()
    }

    Utility.setupStatusBarHidden(this)
    Utility.hideStatusBar(this)

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
              R.drawable.ic_flash_auto ->
                R.drawable.ic_flash_off
              R.drawable.ic_flash_off ->
                R.drawable.ic_flash_on
              else -> R.drawable.ic_flash_auto
            }
            camera_view.flash = when (flashDrawable) {
              R.drawable.ic_flash_off -> Flash.OFF
              R.drawable.ic_flash_on -> Flash.ON
              R.drawable.ic_flash_auto -> Flash.AUTO
              else -> Flash.AUTO
            }
            flashIv.setImageResource(flashDrawable)
            flashIv.animate().translationY(0.toFloat()).setDuration(50).setListener(null).start()
          }
        })
    }
    // endregion

    // region photo Library
    photo_library.setOnClickListener {

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
        camera_view.facing = Facing.BACK
      } else {
        options.isFrontFacing = true
        camera_view.facing = Facing.FRONT
      }

    }
    // endregion

    // region Camera
    camera_view.apply {
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
            val img = Img("", "", it.absolutePath, "", 1)
            selectionList.add(img)
            returnObjects()
          }
        }

        override fun onVideoTaken(result: VideoResult) {
          Utility.vibe(this@CameraActivity, 50)
          Utility.scanPhoto(this@CameraActivity, result.file)
          camera_view.mode = Mode.PICTURE
        }

        override fun onVideoRecordingStart() {
          video_counter_layout_fl.visibility = View.VISIBLE
          videoCounterProgress = 0
          video_pbr.progress = 0
          videoCounterRunnable = object : Runnable {
            override fun run() {
              ++videoCounterProgress
              video_pbr.progress = videoCounterProgress
              var min = 0
              var sec = "$videoCounterProgress"
              if (videoCounterProgress > 59) {
                min = videoCounterProgress / 60
                sec = "${videoCounterProgress - (60 * min)}"
              }
              sec = "0".repeat(2 - sec.length) + sec
              val counter = "${min}:${sec}"
              video_counter.text = counter
              videoCounterHandler.postDelayed(this, 1000)
            }
          }
          videoCounterHandler.postDelayed(videoCounterRunnable, 1000)
          click_me.animate().scaleX(1.2f).scaleY(1.2f).setDuration(300).setInterpolator(
            AccelerateDecelerateInterpolator()
          ).start()

          arrayOf(
            this@CameraActivity.flash,
            photo_library,
            message_bottom,
            front
          ).forEach {
            it.animate().alpha(0f).setDuration(300)
              .setInterpolator(AccelerateDecelerateInterpolator()).start()
          }

        }

        override fun onVideoRecordingEnd() {
          video_counter_layout_fl.visibility = View.GONE
          videoCounterHandler.removeCallbacks(videoCounterRunnable)

          arrayOf(click_me, message_bottom).forEach {
            it.animate().scaleX(1f).scaleY(1f).setDuration(300)
              .setInterpolator(AccelerateDecelerateInterpolator()).start()
          }

          arrayOf(
            this@CameraActivity.flash,
            photo_library,
            message_bottom,
            front
          ).forEach {
            it.animate().alpha(1f).setDuration(300)
              .setInterpolator(AccelerateDecelerateInterpolator()).start()
          }

        }
      })
    }
    // endregion

    // init onclick
    onClickMethods()
  }

  private fun onClickMethods() {

    // TODO: create custom to fix warning
    click_me.setOnTouchListener { _, event ->
      if (event.action == MotionEvent.ACTION_UP) {
        click_me_video_bg.visibility = View.GONE
        click_me_photo_bg.visibility = View.VISIBLE
        click_me_video_bg.animate().scaleX(1f).scaleY(1f).setDuration(300)
          .setInterpolator(AccelerateDecelerateInterpolator()).start()
        click_me.animate().scaleX(1f).scaleY(1f).setDuration(300)
          .setInterpolator(AccelerateDecelerateInterpolator()).start()

        if (camera_view.isTakingVideo) {
          camera_view.stopVideo()
        }

      } else if (event.action == MotionEvent.ACTION_DOWN) {
        click_me_video_bg.visibility = View.VISIBLE
        click_me_photo_bg.visibility = View.GONE
        click_me_video_bg.animate().scaleX(1.2f).scaleY(1.2f).setDuration(300)
          .setInterpolator(AccelerateDecelerateInterpolator()).start()
        click_me.animate().scaleX(1.2f).scaleY(1.2f).setDuration(300)
          .setInterpolator(AccelerateDecelerateInterpolator()).start()
      }

      return@setOnTouchListener false
    }

    click_me.setOnLongClickListener(object : View.OnLongClickListener {
      override fun onLongClick(v: View?): Boolean {
        if (options.isExcludeVideos) return false

        camera_view.mode = Mode.VIDEO
        val fileName = "VID_" +
            SimpleDateFormat("yyyyMMdd_HHmmSS", Locale.ENGLISH).format(Date()) +
            ".mp4"
        val filePath = "DCIM/" + options.path

        val dir = Environment.getExternalStoragePublicDirectory(filePath)
        if (!dir.exists()) {
          dir.mkdirs()
        }

        video_pbr.max = maxVideoDuration / 1000
        video_pbr.invalidate()

        camera_view.takeVideo(File(dir, fileName), maxVideoDuration)
        return true
      }
    })

    click_me.setOnClickListener {

      camera_view.mode = Mode.PICTURE
      camera_view.takePicture()

      ObjectAnimator.ofFloat(
        camera_view,
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
      camera_view.takePicture()
    }
  }

  fun returnObjects() {
    val list = ArrayList<String>()
    for (i in selectionList) {
      list.add(i.url)
    }
    val resultIntent = Intent()
    resultIntent.putStringArrayListExtra(IMAGE_RESULTS, list)
    setResult(RESULT_OK, resultIntent)
    finish()
  }

}