package com.tom.controller;

import com.tom.EmailManager;
import com.tom.view.ViewFactory;
import javafx.concurrent.Service;
import javafx.scene.Node;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tooltip;
import javafx.stage.Stage;

public abstract class BaseController {
  
  protected EmailManager emailManager;
  protected ViewFactory viewFactory;
  private String fxmlName;
  
  public BaseController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
    this.emailManager = emailManager;
    this.viewFactory = viewFactory;
    this.fxmlName = fxmlName;
  }
  
  public String getFxmlName() {
    return fxmlName;
  }
  
  public abstract String getWindowTitle();
  
  public void showProgressIndicator(ProgressIndicator progressIndicator, Service serviceForProgressIndicator) {
    progressIndicator.setMaxSize(40, 40);
    progressIndicator.setVisible(true);
    progressIndicator.progressProperty().bind(serviceForProgressIndicator.progressProperty());
  }
  
  public void hideProgressIndicator(ProgressIndicator progressIndicator) {
    progressIndicator.setVisible(false);
    progressIndicator.progressProperty().unbind();
  }
  
  public void cleanErrorLabel(Label errorLabel) {
    errorLabel.setText("");
  }
  
  public void closeStage(Control controlElement) {
    Stage stage = (Stage) controlElement.getScene().getWindow();
    viewFactory.closeStage(stage);
  }
  
  public void setTooltip(String tooltipText, Node nodeForTooltip) {
    Tooltip tooltipAttachmentLabel = new Tooltip(tooltipText);
    Tooltip.install(nodeForTooltip, tooltipAttachmentLabel);
  }
}
