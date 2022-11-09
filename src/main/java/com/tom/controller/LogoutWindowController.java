package com.tom.controller;

import com.tom.EmailManager;
import com.tom.controller.services.LogoutService;
import com.tom.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;

import java.net.URL;
import java.util.ResourceBundle;

public class LogoutWindowController extends BaseController implements Initializable {
  
  private String windowTitle = "Email Client - Logout";
  @FXML
  private Label errorLabel;
  @FXML
  private ProgressIndicator progressIndicator;
  
  public LogoutWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
    super(emailManager, viewFactory, fxmlName);
  }
  
  @FXML
  void logoutButtonAction() {
    LogoutService logoutService = new LogoutService(emailManager);
    logoutService.restart();
    cleanErrorLabel(errorLabel);
    showProgressIndicator(progressIndicator, logoutService);
    logoutService.setOnSucceeded(event -> {
              hideProgressIndicator(progressIndicator);
              viewFactory.closeAllActiveStages();
            }
    );
  }
  
  @FXML
  void noLogoutButtonAction() {
    closeStage(this.errorLabel);
  }
  
  @Override
  public String getWindowTitle() {
    return windowTitle;
  }
  
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    hideProgressIndicator(progressIndicator);
  }
}
