package com.leonardpark.pixel.modals;

import java.io.Serializable;

public class Img implements Serializable {
  private String headerDate;
  private String contentUrl;
  private String url;
  private Boolean isSelected;
  private String scrollerDate;
  private int mediaType = 1;
  private int position;

  public Img(String headerDate, String contentUrl, String url, String scrollerDate,
             int type) {
    this.headerDate = headerDate;
    this.contentUrl = contentUrl;
    this.url = url;
    this.isSelected = false;
    this.scrollerDate = scrollerDate;
    this.mediaType = type;
  }

  public int getMediaType() {
    return mediaType;
  }

  public void setMediaType(int mediaType) {
    this.mediaType = mediaType;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public String getHeaderDate() {
    return headerDate;
  }

  public void setHeaderDate(String headerDate) {
    this.headerDate = headerDate;
  }

  public String getContentUrl() {
    return contentUrl;
  }

  public void setContentUrl(String contentUrl) {
    this.contentUrl = contentUrl;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public Boolean getSelected() {
    return isSelected;
  }

  public void setSelected(Boolean selected) {
    isSelected = selected;
  }

  public String getScrollerDate() {
    return scrollerDate;
  }

  public void setScrollerDate(String scrollerDate) {
    this.scrollerDate = scrollerDate;
  }
}
