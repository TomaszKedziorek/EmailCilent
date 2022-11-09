package com.tom.controller;

import com.tom.EmailManager;
import com.tom.controller.services.EmailSenderService;
import com.tom.model.EmailAccount;
import com.tom.model.EmailMessage;
import com.tom.view.ViewFactory;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.web.HTMLEditor;
import javafx.stage.FileChooser;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ComposeMessageController extends BaseController implements Initializable {
  private String windowTitle = "Email Client - Compose message";
  
  @FXML
  private Label errorLabel;
  @FXML
  private TextField recipientTextField;
  @FXML
  private TextField subjectTextField;
  @FXML
  private HTMLEditor htmlEditor;
  @FXML
  private ChoiceBox<EmailAccount> emailAccountChoice;
  @FXML
  private HBox hBoxAttachments;
  @FXML
  private ProgressIndicator progressIndicator;
  @FXML
  private Group groupForHbox;
  private List<File> attachments = new ArrayList<File>();
  public static boolean replayMessage = false;
  
  public ComposeMessageController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
    super(emailManager, viewFactory, fxmlName);
  }
  
  @FXML
  void sendButtonAction() {
    EmailSenderService emailSenderService = new EmailSenderService(
            emailManager,
            emailAccountChoice.getValue(),
            subjectTextField.getText(),
            recipientTextField.getText(),
            htmlEditor.getHtmlText(),
            attachments
    );
    if (recipientFieldIsValid()) {
      //Show progress Indicator & bind with service
      showProgressIndicator(progressIndicator, emailSenderService);
      cleanErrorLabel(errorLabel);
      emailSenderService.start();
    }
    emailSenderService.setOnSucceeded(e -> {
      EmailSendingResult emailSendingResult = emailSenderService.getValue();
      hideProgressIndicator(progressIndicator);
      switch (emailSendingResult) {
        case SUCCESS:
          viewFactory.showMainWindowIfNotShown();
          closeStage(errorLabel);
          break;
        case FAILED_BY_PROVIDER:
          errorLabel.setText("Provider error!");
          break;
        case FAILED_BY_UNEXPECTED_ERROR:
          errorLabel.setText("Unexpected error!");
          break;
      }
    });
  }
  
  @Override
  public String getWindowTitle() {
    return windowTitle;
  }
  
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    emailAccountChoice.setItems(emailManager.getEmailAccounts());
    emailAccountChoice.setValue(emailManager.getEmailAccounts().get(0));
    progressIndicator.setVisible(false);
    setUpHboxForAttachments();
    if (replayMessage) {
      setReplyMessage();
    }
  }
  
  @FXML
  void attachButtonAction() {
    FileChooser fileChooser = new FileChooser();
    File selectedFile = fileChooser.showOpenDialog(null);
    if (selectedFile != null) {
      attachments.add(selectedFile);
    }
    Label attachmentLabel = new Label(selectedFile.getName());
    Separator separator = new Separator(Orientation.VERTICAL);
    separator.setPrefHeight(20);
    hBoxAttachments.getChildren().addAll(attachmentLabel, separator);
    
    setTooltip(selectedFile.getAbsolutePath(), attachmentLabel);
  }
  
  private void setUpHboxForAttachments() {
    hBoxAttachments.setSpacing(5);
    hBoxAttachments.setAlignment(Pos.CENTER_LEFT);
  }
  
  private boolean recipientFieldIsValid() {
    if (recipientTextField.getText().isEmpty()) {
      errorLabel.setText("Pleas fill the recipient email address.");
      return false;
    }
    return true;
  }
  
  public void setReplyMessage() {
    EmailMessage messageToReply = emailManager.getSelectedMessage();
    recipientTextField.setText(messageToReply.getSender());
    subjectTextField.setText("Re:" + messageToReply.getSubject());
  }
  
  public static void setReplayMessage(boolean replayMessage) {
    ComposeMessageController.replayMessage = replayMessage;
  }
  
  public static boolean getReplayMessage() {
    return ComposeMessageController.replayMessage;
  }
  
}
