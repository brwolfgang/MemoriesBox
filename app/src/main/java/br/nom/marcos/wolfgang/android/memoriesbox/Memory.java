package br.nom.marcos.wolfgang.android.memoriesbox;

/**
 * Created by Wolfgang Marcos on 17/03/2015.
 */
public class Memory {
  private long id;
  private String title;
  private String content;
  private String date;
  private String time;
  private String[] imageList;

  public Memory() {
  }

  public Memory(long id, String title, String content, String date, String time, String[] imageList) {
    this.id = id;
    this.title = title;
    this.content = content;
    this.date = date;
    this.time = time;
    this.imageList = imageList;
  }

  public Memory(String title, String content, String date, String time, String[] imageList) {
    this.title = title;
    this.content = content;
    this.date = date;
    this.time = time;
    this.imageList = imageList;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getTime() {
    return time;
  }

  public void setTime(String time) {
    this.time = time;
  }

  public String[] getImageList() {
    return imageList;
  }

  public void setImageList(String[] imageList) {
    this.imageList = imageList;
  }
}
