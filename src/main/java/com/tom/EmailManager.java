package com.tom;

import com.tom.controller.services.FetchFoldersService;
import com.tom.controller.services.FolderUpdaterService;
import com.tom.model.EmailAccount;
import com.tom.model.EmailMessage;
import com.tom.model.EmailTreeItem;
import com.tom.view.IconResolver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.mail.Flags;
import javax.mail.Folder;
import java.util.ArrayList;
import java.util.List;

public class EmailManager {
  
  //Folder handling
  private EmailTreeItem<String> foldersRoot = new EmailTreeItem<>("");
  private List<Folder> folderList = new ArrayList<Folder>();
  private FolderUpdaterService folderUpdaterService;
  private EmailMessage selectedMessage;
  private EmailTreeItem<String> selectedFolder;
  //for store email accounts & to use it in ComposeMessage
  private ObservableList<EmailAccount> emailAccounts = FXCollections.observableArrayList();
  private IconResolver iconResolver = new IconResolver();
  
  public EmailManager() {
    folderUpdaterService = new FolderUpdaterService(folderList);
    folderUpdaterService.start();
    
  }
  
  public EmailTreeItem<String> getFoldersRoot() {
    return this.foldersRoot;
  }
  
  public ObservableList<EmailAccount> getEmailAccounts() {
    return this.emailAccounts;
  }
  
  public void addEmailAccount(EmailAccount emailAccount) {
    emailAccounts.add(emailAccount);
    EmailTreeItem<String> treeItem = new EmailTreeItem<String>(emailAccount.getAddress());
    foldersRoot.getChildren().add(treeItem);
    //add icon
    treeItem.setGraphic(iconResolver.getIconForFolder(emailAccount.getAddress()));
    
    FetchFoldersService fetchFoldersService = new FetchFoldersService(emailAccount.getStore(), treeItem, folderList);
    fetchFoldersService.start();
  }
  
  public EmailMessage getSelectedMessage() {
    return selectedMessage;
  }
  
  public void setSelectedMessage(EmailMessage selectedMessage) {
    this.selectedMessage = selectedMessage;
  }
  
  public void setSelectedFolder(EmailTreeItem<String> selectedFolder) {
    this.selectedFolder = selectedFolder;
  }
  
  public void setRead() {
    try {
      selectedMessage.setIsRead(true);
      selectedMessage.getMessage().setFlag(Flags.Flag.SEEN, true);
      selectedFolder.decrementMessagesCount();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public void setUnRead() {
    try {
      selectedMessage.setIsRead(false);
      selectedMessage.getMessage().setFlag(Flags.Flag.SEEN, false);
      selectedFolder.incrementMessagesCount();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public void deleteSelectedMessage() {
    try {
      selectedMessage.getMessage().setFlag(Flags.Flag.DELETED, true);
      selectedFolder.getEmailMessages().remove(selectedMessage);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  public void deleteEmailAccounts() {
    emailAccounts.clear();
  }
}
