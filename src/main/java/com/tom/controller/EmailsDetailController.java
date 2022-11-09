package com.tom.controller;

import com.tom.EmailManager;
import com.tom.controller.services.MessageRendererService;
import com.tom.model.EmailMessage;
import com.tom.view.ViewFactory;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.HBox;
import javafx.scene.web.WebView;

import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import java.awt.*;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class EmailsDetailController extends BaseController implements Initializable {
  private String windowTitle = "Email Client - Email details";
  
  private String locationPath = System.getProperty("user.home") + File.separator + "Downloads" + File.separator;
  @FXML
  private Label senderLabel;
  @FXML
  private Label subjectLabel;
  @FXML
  private Label attachmentLabel;
  @FXML
  private HBox hBoxAttachments;
  @FXML
  private WebView webView;
  @FXML
  private ProgressIndicator progressIndicator;
  @FXML
  private Label errorLabel;
  
  public String getLocationPath() {
    return locationPath;
  }
  
  public void setLocationPath(String locationPath) {
    this.locationPath = locationPath;
  }
  
  public EmailsDetailController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
    super(emailManager, viewFactory, fxmlName);
    
  }
  
  @Override
  public String getWindowTitle() {
    return windowTitle;
  }
  
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    EmailMessage emailMessage = emailManager.getSelectedMessage();
    subjectLabel.setText(emailMessage.getSubject());
    senderLabel.setText(emailMessage.getSender());
    loadAttachments(emailMessage);
    
    MessageRendererService messageRendererService = new MessageRendererService(webView.getEngine());
    messageRendererService.setEmailMessage(emailMessage);
    messageRendererService.restart();
    messageRendererService.setOnSucceeded(event -> {
      messageRendererService.displayMessage();
    });
    
    progressIndicator.setVisible(false);
    //Set tooltip to AttachmentLabel: where to find downloaded attachment
    setTooltip("Download to: " + getLocationPath(), attachmentLabel);
  }
  
  private void loadAttachments(EmailMessage emailMessage) {
    if (emailMessage.hasAttachment()) {
      for (MimeBodyPart mimeBodyPart : emailMessage.getAttachmentList()) {
        try {
          AttachmentButton attachmentButton = new AttachmentButton(mimeBodyPart);
          setTooltip(mimeBodyPart.getFileName(), attachmentButton);
          hBoxAttachments.setSpacing(5);
          hBoxAttachments.setAlignment(Pos.CENTER_LEFT);
          hBoxAttachments.getChildren().add(attachmentButton);
        } catch (MessagingException e) {
          e.printStackTrace();
        }
        
      }
    } else {
      attachmentLabel.setText("");
    }
  }
  
  private class AttachmentButton extends Button {
    private MimeBodyPart mimeBodyPart;
    private String downloadedFilePath;
    
    public AttachmentButton(MimeBodyPart mimeBodyPart) throws MessagingException {
      this.mimeBodyPart = mimeBodyPart;
      this.setText(mimeBodyPart.getFileName());
      this.downloadedFilePath = getLocationPath() + mimeBodyPart.getFileName();
      
      this.setOnAction(e -> downloadAttachment());
      
    }
    
    private void downloadAttachment() {
      colorBlue();
      Service service = new Service() {
        @Override
        protected Task createTask() {
          return new Task() {
            @Override
            protected Object call() throws Exception {
              mimeBodyPart.saveFile(downloadedFilePath);
              return null;
            }
          };
        }
      };
      showProgressIndicator(progressIndicator, service);
      cleanErrorLabel(errorLabel);
      service.restart();
      
      service.setOnSucceeded(e1 -> {
        hideProgressIndicator(progressIndicator);
        cleanErrorLabel(errorLabel);
        colorGreen();
        //After downloads & clicking again file should open
        this.setOnAction(e2 -> {
          File file = new File(downloadedFilePath);
          //Desktop class (required in module-info) work bad on linux & macs
          Desktop desktop = Desktop.getDesktop();
          if (file.exists()) {
            try {
              desktop.open(file);
            } catch (Exception exception) {
              exception.printStackTrace();
            }
          }
        });
      });
      service.setOnFailed(e -> {
        colorRed();
        hideProgressIndicator(progressIndicator);
      });
    }
    
    //change color of button during and after downloading
    private void colorBlue() {
      this.setStyle("-fx-background-color: blue;");
    }
    
    private void colorGreen() {
      this.setStyle("-fx-background-color: green;");
    }
    
    private void colorRed() {
      this.setStyle("-fx-background-color: red;");
    }
  }
  
}