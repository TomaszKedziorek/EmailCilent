package com.tom.model;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;

import javax.mail.Message;
import javax.mail.internet.MimeBodyPart;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EmailMessage {
  //TableView does not support strings, but it supports object properties
  
  //It is like a string but with properties
  private SimpleStringProperty subject;
  private SimpleStringProperty sender;
  private SimpleStringProperty recipient;
  private SimpleObjectProperty<SizeInteger> size;
  private SimpleObjectProperty<Date> date;
  private boolean isRead;
  private Message message;
  private List<MimeBodyPart> attachmentList = new ArrayList<>();
  private boolean hasAttachment = false;
  
  
  public EmailMessage(String subject, String sender, String recipient, int size, Date date, boolean isRead, Message message) {
    this.subject = new SimpleStringProperty(subject);
    this.sender = new SimpleStringProperty(sender);
    this.recipient = new SimpleStringProperty(recipient);
    this.size = new SimpleObjectProperty<SizeInteger>(new SizeInteger(size));
    this.date = new SimpleObjectProperty<Date>(date);
    this.isRead = isRead;
    this.message = message;
  }
  
  public String getSubject() {
    return this.subject.get();
  }
  
  public String getSender() {
    return this.sender.get();
  }
  
  public String getRecipient() {
    return this.recipient.get();
  }
  
  public SizeInteger getSize() {
    return this.size.get();
  }
  
  public Date getDate() {
    return this.date.get();
  }
  
  public boolean isRead() {
    return isRead;
  }
  
  public void setIsRead(boolean isRead) {
    this.isRead = isRead;
  }
  
  public Message getMessage() {
    return this.message;
  }
  
  public boolean hasAttachment() {
    return hasAttachment;
  }
  
  public List<MimeBodyPart> getAttachmentList() {
    return attachmentList;
  }
  
  public void addAttachment(MimeBodyPart mimeBodyPart) {
    hasAttachment = true;
    if (!attachmentList.contains(mimeBodyPart)) {
      attachmentList.add(mimeBodyPart);
    }
  }
  
  private void clearAttachmentList() {
    getAttachmentList().clear();
  }
}
