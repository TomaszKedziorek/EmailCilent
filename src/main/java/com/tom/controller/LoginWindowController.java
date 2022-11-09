package com.tom.controller;

import com.tom.EmailManager;
import com.tom.controller.services.LoginService;
import com.tom.model.EmailAccount;
import com.tom.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginWindowController extends BaseController implements Initializable {
  private String windowTitle = "Email Client - Login";
  @FXML
  private TextField emailAddressField;
  @FXML
  private Label errorLabel;
  @FXML
  private PasswordField passwordField;
  @FXML
  private ProgressIndicator progressIndicator;
  
  public LoginWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
    super(emailManager, viewFactory, fxmlName);
  }
  
  @Override
  public String getWindowTitle() {
    return windowTitle;
  }
  
  @FXML
  void loginButtonAction() {
    System.out.println("Login");
    if (fieldsAreValid()) {
      EmailAccount emailAccount = new EmailAccount(emailAddressField.getText(), passwordField.getText());
      LoginService loginService = new LoginService(emailAccount, emailManager);
      //set progress indicator
      showProgressIndicator(progressIndicator, loginService);
      cleanErrorLabel(errorLabel);
      loginService.start();
      loginService.setOnSucceeded(event -> {
        EmailLoginResult emailLoginResult = loginService.getValue();
        hideProgressIndicator(progressIndicator);
        switch (emailLoginResult) {
          case SUCCESS:
            System.out.println("Success!");
            viewFactory.showMainWindowIfNotShown();
            closeStage(errorLabel);
            return;
          case FAILED_BY_CREDENTIALS:
            errorLabel.setText("Invalid credentials!");
            return;
          case FAILED_BY_NETWORK:
            errorLabel.setText("Problem with network!");
            return;
          case FAILED_BY_UNEXPECTED_ERROR:
            errorLabel.setText("Unexpected error!!");
            return;
          default:
            return;
        }
      });
      loginService.setOnFailed(event -> {
        hideProgressIndicator(progressIndicator);
      });
    }
  }
  
  private boolean fieldsAreValid() {
    if (emailAddressField.getText().isEmpty()) {
      errorLabel.setText("Pleas fill email address.");
      return false;
    }
    if (passwordField.getText().isEmpty()) {
      errorLabel.setText("Pleas fill password.");
      return false;
    }
    return true;
  }
  
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    progressIndicator.setVisible(false);
  }
}