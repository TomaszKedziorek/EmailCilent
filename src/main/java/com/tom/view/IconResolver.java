package com.tom.view;

import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class IconResolver {
  public Node getIconForFolder(String folderName) {
    String lowerCaseFolderName = folderName.toLowerCase();
    ImageView imageView = null;
    try {
      if (lowerCaseFolderName.contains("@")) {
        imageView = new ImageView(new Image(getClass().getResourceAsStream("icons/email.png")));
      } else if (lowerCaseFolderName.contains("inbox") || lowerCaseFolderName.contains("odebrane")) {
        imageView = new ImageView(new Image(getClass().getResourceAsStream("icons/inbox.png")));
      } else if (lowerCaseFolderName.contains("sent") || lowerCaseFolderName.contains("wysłane")) {
        imageView = new ImageView(new Image(getClass().getResourceAsStream("icons/sent2.png")));
      } else if (lowerCaseFolderName.contains("spam")) {
        imageView = new ImageView(new Image(getClass().getResourceAsStream("icons/spam.png")));
      } else {
        imageView = new ImageView(new Image(getClass().getResourceAsStream("icons/folder.png")));
      }
      
    } catch (Exception e) {
      e.printStackTrace();
    }
    imageView.setFitWidth(16);
    imageView.setFitHeight(16);
    return imageView;
  }
}
