package com.tom.controller.services;

import com.tom.model.EmailMessage;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.web.WebEngine;

import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import java.io.IOException;

public class MessageRendererService extends Service {
  
  private EmailMessage emailMessage;
  private WebEngine webEngine;
  private StringBuffer stringBuffer;//It holds the content rendered by web engine
  
  public MessageRendererService(WebEngine webEngine) {
    this.webEngine = webEngine;
    this.stringBuffer = new StringBuffer();
    
  }
  
  public void setEmailMessage(EmailMessage emailMessage) {
    this.emailMessage = emailMessage;
  }
  
  public void displayMessage() {
    webEngine.loadContent(stringBuffer.toString());
  }
  
  @Override
  protected Task createTask() {
    return new Task() {
      @Override
      protected Object call() throws Exception {
        try {
          loadMessage();
        } catch (Exception e) {
          e.printStackTrace();
        }
        return null;
      }
    };
  }
  
  private void loadMessage() throws MessagingException, IOException {
    stringBuffer.setLength(0);//to clear stringBuffer
    Message message = emailMessage.getMessage();
    String contentType = message.getContentType();
    
    if (isTextHtml(contentType) || isTextPlain(contentType)) {
      stringBuffer.append(message.getContent().toString());
      
    } else if (isMultiTypes(contentType)) {
      Multipart multipart = (Multipart) message.getContent();//this return array of parts
      loadMultipart(multipart, stringBuffer);
    }
  }
  
  private void loadMultipart(Multipart multipart, StringBuffer stringBuffer) throws MessagingException, IOException {
    for (int i = multipart.getCount() - 1; i >= 0; i--) {
      BodyPart bodyPart = multipart.getBodyPart(i);
      String contentType = bodyPart.getContentType();
      if (isTextHtml(contentType) || isTextPlain(contentType)) {
        stringBuffer.append(bodyPart.getContent().toString());
        //break;
      } else if (isMultiTypes(contentType)) {
        Multipart multipart2 = (Multipart) bodyPart.getContent();
        loadMultipart(multipart2, stringBuffer);
      } else if (!isTextPlain(contentType)) {
        //here attachments
        //must be added to EmailMessage
        MimeBodyPart mimeBodyPart = (MimeBodyPart) bodyPart;
        emailMessage.addAttachment(mimeBodyPart);
      }
    }
  }
  
  private boolean isTextPlain(String contentType) {
    if (contentType.contains("TEXT/PLAIN")) {
      return true;
    } else return false;
  }
  
  private boolean isTextHtml(String contentType) {
    if (contentType.contains("TEXT/HTML")) {
      return true;
    } else return false;
  }
  
  private boolean isMultiTypes(String contentType) {
    if (contentType.contains("multipart") || contentType.contains("multipart/MIXED")) return true;
    else return false;
  }
}
