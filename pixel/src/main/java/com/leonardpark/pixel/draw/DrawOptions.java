package com.leonardpark.pixel.draw;

import java.io.Serializable;

public class DrawOptions implements Serializable {
  private int requestCode = 0;
  private String path = "Pixel";
  private String background = "";

  private DrawOptions() {
  }

  public static DrawOptions init() {
    return new DrawOptions();
  }

  private void check() {
    if (this == null) {
      throw new NullPointerException("call init() method to initialise Options class");
    }
  }

  public int getRequestCode() {
    if (this.requestCode == 0) {
      throw new NullPointerException("requestCode in Options class is null");
    }
    return requestCode;
  }

  public DrawOptions setRequestCode(int requestCode) {
    check();
    this.requestCode = requestCode;
    return this;
  }

  public String getPath() {
    return this.path;
  }

  public DrawOptions setPath(String path) {
    check();
    this.path = path;
    return this;
  }

  public String getBackground() {
    return background;
  }

  public DrawOptions setBackground(String background) {
    check();
    this.background = background;
    return this;
  }
}
