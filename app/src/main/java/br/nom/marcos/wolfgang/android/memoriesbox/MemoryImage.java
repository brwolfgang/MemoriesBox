package br.nom.marcos.wolfgang.android.memoriesbox;

public class MemoryImage {

  private Long imageID;
  private Long memoryID;
  private String imagePath;


  public MemoryImage() {
  }

  public MemoryImage(Long imageID, Long memoryID, String imagePath) {
    this.imageID = imageID;
    this.memoryID = memoryID;
    this.imagePath = imagePath;
  }

  public String getImagePath() {
    return imagePath;
  }

  public void setImagePath(String imagePath) {
    this.imagePath = imagePath;
  }

  public Long getImageID() {
    return imageID;
  }

  public void setImageID(Long imageID) {
    this.imageID = imageID;
  }

  public Long getMemoryID() {
    return memoryID;
  }

  public void setMemoryID(Long memoryID) {
    this.memoryID = memoryID;
  }
}
