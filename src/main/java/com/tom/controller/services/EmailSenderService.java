package com.tom.controller.services;

import com.tom.EmailManager;
import com.tom.controller.ComposeMessageController;
import com.tom.controller.EmailSendingResult;
import com.tom.model.EmailAccount;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.File;
import javax.activation.DataSource;
import java.util.List;

public class EmailSenderService extends Service<EmailSendingResult> {
  
  private EmailAccount emailAccount;
  private EmailManager emailManager;
  private String subject;
  private String recipient;
  private String content;
  private List<File> attachments;
  
  public EmailSenderService(EmailManager emailManager, EmailAccount emailAccount, String subject, String recipient, String content, List<File> attachments) {
    this.emailManager = emailManager;
    this.emailAccount = emailAccount;
    this.subject = subject;
    this.recipient = recipient;
    this.content = content;
    this.attachments = attachments;
  }
  
  private MimeMessage createMessage(EmailAccount emailAccount) throws MessagingException {
    MimeMessage mimeMessage = new MimeMessage(emailAccount.getSession());
    mimeMessage.setFrom(emailAccount.getAddress());
    mimeMessage.addRecipients(Message.RecipientType.TO, recipient);
    mimeMessage.setSubject(subject);
    return mimeMessage;
  }
  
  private MimeMessage createReplyMessage(EmailAccount emailAccount) throws MessagingException {
    MimeMessage mimeMessage = new MimeMessage(emailAccount.getSession());
    mimeMessage = (MimeMessage) emailManager.getSelectedMessage().getMessage().reply(false);
    mimeMessage.setReplyTo(new InternetAddress[]{new InternetAddress(recipient)});
    mimeMessage.setFrom(emailAccount.getAddress());
    return mimeMessage;
  }
  
  private Multipart setMultipartContent() throws MessagingException {
    Multipart multipart = new MimeMultipart();
    BodyPart messageBodyPart = new MimeBodyPart();
    messageBodyPart.setContent(content, "TEXT/HTML;charset=UTF-8");
    multipart.addBodyPart(messageBodyPart);
    return multipart;
  }
  
  private void addAttachmentsToMultipart(Multipart multipart) throws MessagingException {
    if (attachments.size() > 0) {
      for (File file : attachments) {
        MimeBodyPart mimeBodyPart = new MimeBodyPart();
        DataSource source = new FileDataSource(file.getAbsolutePath());
        mimeBodyPart.setDataHandler(new DataHandler(source));
        mimeBodyPart.setFileName(file.getName());
        multipart.addBodyPart(mimeBodyPart);
      }
    }
  }
  
  private void sendMessage(EmailAccount emailAccount, MimeMessage mimeMessage) throws MessagingException {
    Transport transport = emailAccount.getSession().getTransport();
    transport.connect(
            emailAccount.getProperties().getProperty("outgoingHost"),
            emailAccount.getAddress(),
            emailAccount.getPassword()
    );
    transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
    transport.close();
  }
  
  @Override
  protected Task<EmailSendingResult> createTask() {
    return new Task<EmailSendingResult>() {
      @Override
      protected EmailSendingResult call() {
        try {
          MimeMessage mimeMessage = null;
          //Here we are creating the new message or reply message
          if (ComposeMessageController.getReplayMessage()) {
            mimeMessage = createReplyMessage(emailAccount);
            ComposeMessageController.setReplayMessage(false);
          } else {
            mimeMessage = createMessage(emailAccount);
          }
          //set the content with multipart
          Multipart multipart = setMultipartContent();
          mimeMessage.setContent(multipart);
          //Attachments handling
          addAttachmentsToMultipart(multipart);
          //Sending message
          sendMessage(emailAccount, mimeMessage);
          return EmailSendingResult.SUCCESS;
          
        } catch (MessagingException e) {
          e.printStackTrace();
          return EmailSendingResult.FAILED_BY_PROVIDER;
        } catch (Exception e) {
          e.printStackTrace();
          return EmailSendingResult.FAILED_BY_UNEXPECTED_ERROR;
        }
      }
    };
  }
}
