package com.leonardpark.pixel.utility

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.leonardpark.pixel.interfaces.WorkFinish
import java.util.*

class PermUtil {
  companion object {
    const val CAMERA_REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 9921
    const val GALLERY_REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 9922
    const val DRAW_REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 9933

    @RequiresApi(api = Build.VERSION_CODES.M)
    private fun addPermission(
      permissionsList: MutableList<String>,
      permission: String,
      ac: Activity
    ): Boolean {
      if (ac.checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
        permissionsList.add(permission)
        // Check for Rationale Option
        return ac.shouldShowRequestPermissionRationale(permission)
      }
      return true
    }

    fun checkForCameraWritePermissions(activity: FragmentActivity, workFinish: WorkFinish) {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        workFinish.onWorkFinish(true)
      } else {
        val permissionsNeeded: MutableList<String> = ArrayList()
        val permissionsList: MutableList<String> = ArrayList()

        if (!addPermission(
            permissionsList,
            Manifest.permission.CAMERA,
            activity
          )
        ) permissionsNeeded.add("CAMERA")

        if (!addPermission(
            permissionsList,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            activity
          )
        ) permissionsNeeded.add("WRITE_EXTERNAL_STORAGE")

        if (permissionsList.size > 0) {
          activity.requestPermissions(
            permissionsList.toTypedArray(),
            CAMERA_REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
          )
        } else {
          workFinish.onWorkFinish(true)
        }
      }
    }

    fun checkForCameraWritePermissions(fragment: Fragment, workFinish: WorkFinish) {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        workFinish.onWorkFinish(true)
      } else {
        val permissionsNeeded: MutableList<String> = ArrayList()
        val permissionsList: MutableList<String> = ArrayList()

        if (!addPermission(
            permissionsList,
            Manifest.permission.CAMERA,
            fragment.requireActivity()
          )
        ) permissionsNeeded.add("CAMERA")

        if (!addPermission(
            permissionsList,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            fragment.requireActivity()
          )
        ) permissionsNeeded.add("WRITE_EXTERNAL_STORAGE")

        if (permissionsList.size > 0) {
          fragment.requestPermissions(
            permissionsList.toTypedArray(),
            CAMERA_REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
          )
        } else {
          workFinish.onWorkFinish(true)
        }
      }
    }

    fun checkForGalleryPermissions(activity: FragmentActivity, workFinish: WorkFinish) {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        workFinish.onWorkFinish(true)
      } else {
        val permissionsNeeded: MutableList<String> = ArrayList()
        val permissionsList: MutableList<String> = ArrayList()

        if (!addPermission(
            permissionsList,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            activity
          )
        ) permissionsNeeded.add("WRITE_EXTERNAL_STORAGE")

        if (permissionsList.size > 0) {
          activity.requestPermissions(
            permissionsList.toTypedArray(),
            GALLERY_REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
          )
        } else {
          workFinish.onWorkFinish(true)
        }
      }
    }

    fun checkForGalleryPermissions(fragment: Fragment, workFinish: WorkFinish) {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        workFinish.onWorkFinish(true)
      } else {
        val permissionsNeeded: MutableList<String> = ArrayList()
        val permissionsList: MutableList<String> = ArrayList()

        if (!addPermission(
            permissionsList,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            fragment.requireActivity()
          )
        ) permissionsNeeded.add("WRITE_EXTERNAL_STORAGE")

        if (permissionsList.size > 0) {
          fragment.requestPermissions(
            permissionsList.toTypedArray(),
            GALLERY_REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
          )
        } else {
          workFinish.onWorkFinish(true)
        }
      }
    }

    fun checkForDrawPermissions(activity: FragmentActivity, workFinish: WorkFinish) {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        workFinish.onWorkFinish(true)
      } else {
        val permissionsNeeded: MutableList<String> = ArrayList()
        val permissionsList: MutableList<String> = ArrayList()

        if (!addPermission(
            permissionsList,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            activity
          )
        ) permissionsNeeded.add("WRITE_EXTERNAL_STORAGE")

        if (permissionsList.size > 0) {
          activity.requestPermissions(
            permissionsList.toTypedArray(),
            DRAW_REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
          )
        } else {
          workFinish.onWorkFinish(true)
        }
      }
    }

    fun checkForDrawPermissions(fragment: Fragment, workFinish: WorkFinish) {
      if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
        workFinish.onWorkFinish(true)
      } else {
        val permissionsNeeded: MutableList<String> = ArrayList()
        val permissionsList: MutableList<String> = ArrayList()

        if (!addPermission(
            permissionsList,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            fragment.requireActivity()
          )
        ) permissionsNeeded.add("WRITE_EXTERNAL_STORAGE")

        if (permissionsList.size > 0) {
          fragment.requestPermissions(
            permissionsList.toTypedArray(),
            DRAW_REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS
          )
        } else {
          workFinish.onWorkFinish(true)
        }
      }
    }
  }
}