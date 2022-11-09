package com.tom.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

public class EmailTreeItem<String> extends TreeItem<String> {
  
  private String name;
  //Variable for holding messages
  private ObservableList<EmailMessage> emailMessages;
  //to show how many unread messages are in folder
  private int unreadMessageCount;
  
  public EmailTreeItem(String name) {
    super(name);
    this.name = name;
    this.emailMessages = FXCollections.observableArrayList();
  }
  
  public void addEmail(Message message) throws MessagingException {
    EmailMessage emailMessage = fetchMessage(message);
    emailMessages.add(emailMessage);
  }
  public void addEmailToTop(Message message) throws MessagingException {
    EmailMessage emailMessage = fetchMessage(message);
    emailMessages.add(0,emailMessage);
  }

  private EmailMessage fetchMessage(Message message) throws MessagingException {
    boolean messageIsRead = message.getFlags().contains(Flags.Flag.SEEN);
    EmailMessage emailMessage = new EmailMessage(
            message.getSubject(),
            message.getFrom()[0].toString(),
            message.getRecipients(MimeMessage.RecipientType.TO)[0].toString(),
            message.getSize(),
            message.getSentDate(),
            messageIsRead,
            message
    );
    if(!messageIsRead){
      incrementMessagesCount();
    }
    return emailMessage;
  }
  public void incrementMessagesCount(){
    unreadMessageCount++;
    updateName();
  }
  public void decrementMessagesCount(){
    unreadMessageCount--;
    updateName();
  }
  private void updateName() {
    if (unreadMessageCount > 0) {
      this.setValue((String) (name + "  (" + unreadMessageCount + ")"));
    }else{
      this.setValue(name);
    }
  }
  
  public ObservableList<EmailMessage> getEmailMessages() {
    return emailMessages;
  }
  
}
