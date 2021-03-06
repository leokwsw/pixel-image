package com.leonardpark.pixel.camera;

import java.io.Serializable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;

public class CameraOptions implements Serializable {
  public static final int SCREEN_ORIENTATION_UNSET = -2;
  public static final int SCREEN_ORIENTATION_UNSPECIFIED = -1;
  public static final int SCREEN_ORIENTATION_LANDSCAPE = 0;
  public static final int SCREEN_ORIENTATION_PORTRAIT = 1;
  public static final int SCREEN_ORIENTATION_USER = 2;
  public static final int SCREEN_ORIENTATION_BEHIND = 3;
  public static final int SCREEN_ORIENTATION_SENSOR = 4;
  public static final int SCREEN_ORIENTATION_NO_SENSOR = 5;
  public static final int SCREEN_ORIENTATION_SENSOR_LANDSCAPE = 6;
  public static final int SCREEN_ORIENTATION_SENSOR_PORTRAIT = 7;
  public static final int SCREEN_ORIENTATION_REVERSE_LANDSCAPE = 8;
  public static final int SCREEN_ORIENTATION_REVERSE_PORTRAIT = 9;
  public static final int SCREEN_ORIENTATION_FULL_SENSOR = 10;
  public static final int SCREEN_ORIENTATION_USER_LANDSCAPE = 11;
  public static final int SCREEN_ORIENTATION_USER_PORTRAIT = 12;
  public static final int SCREEN_ORIENTATION_FULL_USER = 13;
  public static final int SCREEN_ORIENTATION_LOCKED = 14;
  private int count = 1;
  private int requestCode = 0;
  private int spanCount = 4;
  private String path = "Pixel";
  private int height = 0, width = 0;
  private boolean frontFacing = false;
  private int videoDurationLimitInSeconds = 40;
  private boolean excludeVideos = false;
  private ArrayList<String> preSelectedUrls = new ArrayList<>();

  @ScreenOrientation
  private int screenOrientation = SCREEN_ORIENTATION_UNSPECIFIED;

  private CameraOptions() {
  }

  public static CameraOptions init() {
    return new CameraOptions();
  }

  public int getVideoDurationLimitInSeconds() {
    return videoDurationLimitInSeconds;
  }

  public CameraOptions setVideoDurationLimitInSeconds(int videoDurationLimitInSececonds) {
    this.videoDurationLimitInSeconds = videoDurationLimitInSececonds;
    return this;
  }

  public ArrayList<String> getPreSelectedUrls() {
    return preSelectedUrls;
  }

  public CameraOptions setPreSelectedUrls(ArrayList<String> preSelectedUrls) {
    check();
    this.preSelectedUrls = preSelectedUrls;
    return this;
  }

  public boolean isExcludeVideos() {
    return excludeVideos;
  }

  public CameraOptions setExcludeVideos(boolean excludeVideos) {
    this.excludeVideos = excludeVideos;
    return this;
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }

  public boolean isFrontFacing() {
    return this.frontFacing;
  }

  public CameraOptions setFrontFacing(boolean frontFacing) {
    this.frontFacing = frontFacing;
    return this;
  }

  private void check() {
    if (this == null) {
      throw new NullPointerException("call init() method to initialise Options class");
    }
  }

  public int getCount() {
    return count;
  }

  public CameraOptions setCount(int count) {
    check();
    this.count = count;
    return this;
  }

  public int getRequestCode() {
    if (this.requestCode == 0) {
      throw new NullPointerException("requestCode in Options class is null");
    }
    return requestCode;
  }

  public CameraOptions setRequestCode(int requestCode) {
    check();
    this.requestCode = requestCode;
    return this;
  }

  public String getPath() {
    return this.path;
  }

  public CameraOptions setPath(String path) {
    check();
    this.path = path;
    return this;
  }

  public int getScreenOrientation() {
    return screenOrientation;
  }

  public CameraOptions setScreenOrientation(@ScreenOrientation int screenOrientation) {
    check();
    this.screenOrientation = screenOrientation;
    return this;
  }

  public int getSpanCount() {
    return spanCount;
  }

  public CameraOptions setSpanCount(int spanCount) {
    check();
    this.spanCount = spanCount;
    if (spanCount < 1 && spanCount > 5) {
      throw new IllegalArgumentException("span count can not be set below 0 or more than 5");
    }
    return this;
  }

  @Retention(RetentionPolicy.SOURCE)
  public @interface ScreenOrientation {
  }


}
