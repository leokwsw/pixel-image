package com.leonardpark.pixel

import android.os.Build

class System {
  companion object {
    val isQ = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
  }
}