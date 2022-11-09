module com.tom {
  requires javafx.controls;
  requires javafx.fxml;
  requires javafx.web;
  requires java.mail;//name is different than in pom artifactId, but in MANIFEST.MF in META-INF ther is Automatic-Module-Name: java.mail
  requires java.activation;//same thing
  requires java.desktop;
  
  opens com.tom;
  opens com.tom.view;
  opens com.tom.controller;
  opens com.tom.model;
  exports com.tom;
}
