package br.nom.marcos.wolfgang.android.memoriesbox;

/**
 * Created by Wolfgang Marcos on 17/03/2015.
 */
public class Memory {
  private long id;
  private String title;
  private String content;
  private String creation_date;

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

  public String getCreation_date() {
    return creation_date;
  }

  public void setCreation_date(String creation_date) {
    this.creation_date = creation_date;
  }
}
