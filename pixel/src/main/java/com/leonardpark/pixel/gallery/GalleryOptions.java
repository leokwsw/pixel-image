package com.leonardpark.pixel.gallery;

import java.io.Serializable;
import java.util.ArrayList;

public class GalleryOptions implements Serializable {
  private int count = 1;
  private int requestCode = 0;
  private int spanCount = 4;
  private String path = "Pixel";
  private int height = 0, width = 0;
  private boolean excludeVideos = false;

  private ArrayList<String> preSelectedUrls = new ArrayList<>();

  private GalleryOptions() {
  }

  public static GalleryOptions init() {
    return new GalleryOptions();
  }

  public ArrayList<String> getPreSelectedUrls() {
    return preSelectedUrls;
  }

  public GalleryOptions setPreSelectedUrls(ArrayList<String> preSelectedUrls) {
    check();
    this.preSelectedUrls = preSelectedUrls;
    return this;
  }

  public boolean isExcludeVideos() {
    return excludeVideos;
  }

  public GalleryOptions setExcludeVideos(boolean excludeVideos) {
    this.excludeVideos = excludeVideos;
    return this;
  }

  public int getHeight() {
    return height;
  }

  public int getWidth() {
    return width;
  }

  private void check() {
    if (this == null) {
      throw new NullPointerException("call init() method to initialise Options class");
    }
  }

  public int getCount() {
    return count;
  }

  public GalleryOptions setCount(int count) {
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

  public GalleryOptions setRequestCode(int requestCode) {
    check();
    this.requestCode = requestCode;
    return this;
  }

  public String getPath() {
    return this.path;
  }

  public GalleryOptions setPath(String path) {
    check();
    this.path = path;
    return this;
  }

  public int getSpanCount() {
    return spanCount;
  }

  public GalleryOptions setSpanCount(int spanCount) {
    check();
    this.spanCount = spanCount;
    if (spanCount < 1 && spanCount > 5) {
      throw new IllegalArgumentException("span count can not be set below 0 or more than 5");
    }
    return this;
  }


}
