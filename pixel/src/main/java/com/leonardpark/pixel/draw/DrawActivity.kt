package com.leonardpark.pixel.draw

import android.app.Activity
import android.content.Intent
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.leonardpark.pixel.R
import com.leonardpark.pixel.interfaces.WorkFinish
import com.leonardpark.pixel.utility.PermUtil
import kotlinx.android.synthetic.main.activity_draw.*
import kotlinx.android.synthetic.main.color_palette_view.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class DrawActivity : AppCompatActivity() {

  companion object {
    private const val OPTIONS = "options"

    var IMAGE_RESULTS = "image_results"

    fun start(context: Fragment, options: DrawOptions) {
      PermUtil.checkForDrawPermissions(context, object : WorkFinish {
        override fun onWorkFinish(check: Boolean) {
          context.startActivityForResult(
            Intent(
              context.activity,
              DrawActivity::class.java
            ).apply {
              putExtra(OPTIONS, options)
            }, options.requestCode
          )
        }
      })
    }

    fun start(context: Fragment, requestCode: Int) {
      start(context, DrawOptions.init().setRequestCode(requestCode))
    }

    fun start(context: FragmentActivity, options: DrawOptions) {
      PermUtil.checkForDrawPermissions(context, object : WorkFinish {
        override fun onWorkFinish(check: Boolean) {
          context.startActivityForResult(
            Intent(
              context,
              DrawActivity::class.java
            ).apply {
              putExtra(OPTIONS, options)
            }, options.requestCode
          )
        }
      })
    }
  }

  private var options: DrawOptions = DrawOptions.init()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_draw)

    try {
      options = intent.getSerializableExtra(OPTIONS) as DrawOptions
    } catch (e: Exception) {
      e.printStackTrace()
    }

    image_close_drawing.setOnClickListener {
      finish()
    }
    fab_send_drawing.setOnClickListener {
      val bStream = ByteArrayOutputStream()
      val bitmap = draw_view.getBitmap()
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, bStream)

      val path = Environment.getExternalStoragePublicDirectory("DCIM/${options.path}/")
      val file = File(
        path,
        "DRAW_" + SimpleDateFormat("yyyyMMdd_HHmmSS", Locale.ENGLISH).format(Date()) + ".png"
      )
      path.mkdirs()
      file.createNewFile()

      val outputStream = FileOutputStream(file)
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
      outputStream.flush()
      outputStream.close()

      setResult(Activity.RESULT_OK, Intent().apply {
        putExtra(IMAGE_RESULTS, arrayListOf<String>(file.path))
      })
      finish()
    }

    setUpDrawTools()

    colorSelector()

    setPaintAlpha()

    setPaintWidth()
  }


  private fun setUpDrawTools() {
    circle_view_opacity.setCircleRadius(100f)
    image_draw_eraser.setOnClickListener {
      draw_view.toggleEraser()
      image_draw_eraser.isSelected = draw_view.isEraserOn
      toggleDrawTools(draw_tools, false)
    }
    image_draw_eraser.setOnLongClickListener {
      draw_view.clearCanvas()
      toggleDrawTools(draw_tools, false)
      true
    }
    image_draw_width.setOnClickListener {
      if (draw_tools.translationY == (56).toPx) {
        toggleDrawTools(draw_tools, true)
      } else if (draw_tools.translationY == (0).toPx && seekBar_width.visibility == View.VISIBLE) {
        toggleDrawTools(draw_tools, false)
      }
      circle_view_width.visibility = View.VISIBLE
      circle_view_opacity.visibility = View.GONE
      seekBar_width.visibility = View.VISIBLE
      seekBar_opacity.visibility = View.GONE
      draw_color_palette.visibility = View.GONE
    }
    image_draw_opacity.setOnClickListener {
      if (draw_tools.translationY == (56).toPx) {
        toggleDrawTools(draw_tools, true)
      } else if (draw_tools.translationY == (0).toPx && seekBar_opacity.visibility == View.VISIBLE) {
        toggleDrawTools(draw_tools, false)
      }
      circle_view_width.visibility = View.GONE
      circle_view_opacity.visibility = View.VISIBLE
      seekBar_width.visibility = View.GONE
      seekBar_opacity.visibility = View.VISIBLE
      draw_color_palette.visibility = View.GONE
    }
    image_draw_color.setOnClickListener {
      if (draw_tools.translationY == (56).toPx) {
        toggleDrawTools(draw_tools, true)
      } else if (draw_tools.translationY == (0).toPx && draw_color_palette.visibility == View.VISIBLE) {
        toggleDrawTools(draw_tools, false)
      }
      circle_view_width.visibility = View.GONE
      circle_view_opacity.visibility = View.GONE
      seekBar_width.visibility = View.GONE
      seekBar_opacity.visibility = View.GONE
      draw_color_palette.visibility = View.VISIBLE
    }
    image_draw_undo.setOnClickListener {
      draw_view.undo()
      toggleDrawTools(draw_tools, false)
    }
    image_draw_redo.setOnClickListener {
      draw_view.redo()
      toggleDrawTools(draw_tools, false)
    }
  }

  private fun toggleDrawTools(view: View, showView: Boolean = true) {
    if (showView) {
      view.animate().translationY((0).toPx)
    } else {
      view.animate().translationY((56).toPx)
    }
  }

  private fun colorSelector() {
    image_color_black.setOnClickListener {
      val color = ResourcesCompat.getColor(resources, R.color.color_black, null)
      draw_view.setColor(color)
      circle_view_opacity.setColor(color)
      circle_view_width.setColor(color)
      scaleColorView(image_color_black)
    }
    image_color_red.setOnClickListener {
      val color = ResourcesCompat.getColor(resources, R.color.color_red, null)
      draw_view.setColor(color)
      circle_view_opacity.setColor(color)
      circle_view_width.setColor(color)
      scaleColorView(image_color_red)
    }
    image_color_yellow.setOnClickListener {
      val color = ResourcesCompat.getColor(resources, R.color.color_yellow, null)
      draw_view.setColor(color)
      circle_view_opacity.setColor(color)
      circle_view_width.setColor(color)
      scaleColorView(image_color_yellow)
    }
    image_color_green.setOnClickListener {
      val color = ResourcesCompat.getColor(resources, R.color.color_green, null)
      draw_view.setColor(color)
      circle_view_opacity.setColor(color)
      circle_view_width.setColor(color)
      scaleColorView(image_color_green)
    }
    image_color_blue.setOnClickListener {
      val color = ResourcesCompat.getColor(resources, R.color.color_blue, null)
      draw_view.setColor(color)
      circle_view_opacity.setColor(color)
      circle_view_width.setColor(color)
      scaleColorView(image_color_blue)
    }
    image_color_pink.setOnClickListener {
      val color = ResourcesCompat.getColor(resources, R.color.color_pink, null)
      draw_view.setColor(color)
      circle_view_opacity.setColor(color)
      circle_view_width.setColor(color)
      scaleColorView(image_color_pink)
    }
    image_color_brown.setOnClickListener {
      val color = ResourcesCompat.getColor(resources, R.color.color_brown, null)
      draw_view.setColor(color)
      circle_view_opacity.setColor(color)
      circle_view_width.setColor(color)
      scaleColorView(image_color_brown)
    }
  }

  private fun scaleColorView(view: View) {
    //reset scale of all views
    image_color_black.scaleX = 1f
    image_color_black.scaleY = 1f

    image_color_red.scaleX = 1f
    image_color_red.scaleY = 1f

    image_color_yellow.scaleX = 1f
    image_color_yellow.scaleY = 1f

    image_color_green.scaleX = 1f
    image_color_green.scaleY = 1f

    image_color_blue.scaleX = 1f
    image_color_blue.scaleY = 1f

    image_color_pink.scaleX = 1f
    image_color_pink.scaleY = 1f

    image_color_brown.scaleX = 1f
    image_color_brown.scaleY = 1f

    //set scale of selected view
    view.scaleX = 1.5f
    view.scaleY = 1.5f
  }

  private fun setPaintWidth() {
    seekBar_width.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
      override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        draw_view.setStrokeWidth(progress.toFloat())
        circle_view_width.setCircleRadius(progress.toFloat())
      }

      override fun onStartTrackingTouch(seekBar: SeekBar?) {}

      override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    })
  }

  private fun setPaintAlpha() {
    seekBar_opacity.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
      override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
        draw_view.setAlpha(progress)
        circle_view_opacity.setAlpha(progress)
      }

      override fun onStartTrackingTouch(seekBar: SeekBar?) {}

      override fun onStopTrackingTouch(seekBar: SeekBar?) {}
    })
  }

  private val Int.toPx: Float
    get() = (this * Resources.getSystem().displayMetrics.density)
}