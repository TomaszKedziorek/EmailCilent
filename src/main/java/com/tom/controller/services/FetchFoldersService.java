package com.tom.controller.services;

import com.tom.model.EmailTreeItem;
import com.tom.view.IconResolver;
import javafx.concurrent.Service;
import javafx.concurrent.Task;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Store;
import javax.mail.event.MessageCountEvent;
import javax.mail.event.MessageCountListener;
import java.util.List;

public class FetchFoldersService extends Service<Void> {
  
  private Store store;
  private EmailTreeItem<String> foldersRoot;
  private List<Folder> folderList;
  private IconResolver iconResolver = new IconResolver();
  
  public FetchFoldersService(Store store, EmailTreeItem<String> foldersRoot, List<Folder> folderList) {
    this.store = store;
    this.foldersRoot = foldersRoot;
    this.folderList = folderList;
  }
  
  @Override
  protected Task<Void> createTask() {
    return new Task<Void>() {
      @Override
      protected Void call() throws Exception {
        fetchFolders();
        return null;
      }
    };
  }
  
  private void fetchFolders() throws MessagingException {
    Folder[] folders = store.getDefaultFolder().list();
    handleFolders(folders, foldersRoot);
  }
  
  private void handleFolders(Folder[] folders, EmailTreeItem<String> foldersRoot) throws MessagingException {
    for (Folder folder : folders) {
      folderList.add(folder);
      EmailTreeItem<String> emailTreeItem = new EmailTreeItem<String>(folder.getName());
      //add icon
      emailTreeItem.setGraphic(iconResolver.getIconForFolder(folder.getName()));
      
      foldersRoot.getChildren().add(emailTreeItem);
      foldersRoot.setExpanded(true);
      //Fetch messages form folder
      fetchMessagesFromFolder(folder, emailTreeItem);
      addMessageListenerToFolder(folder, emailTreeItem);
      //Now we fetch all folders
      if (folder.getType() == Folder.HOLDS_FOLDERS) {
        Folder[] subFolders = folder.list();
        handleFolders(subFolders, emailTreeItem);
      }
    }
  }
  
  private void fetchMessagesFromFolder(Folder folder, EmailTreeItem<String> emailTreeItem) {
    Service fetchMessagesService = new Service() {
      @Override
      protected Task createTask() {
        return new Task() {
          @Override
          protected Object call() throws Exception {
            if (folder.getType() != Folder.HOLDS_FOLDERS) {
              folder.open(Folder.READ_WRITE);
              int numbersOfMessages = folder.getMessageCount();
              for (int i = numbersOfMessages; i >= 1; i--) {
                emailTreeItem.addEmail(folder.getMessage(i));
              }
            }
            return null;
          }
        };
      }
    };
    fetchMessagesService.start();
  }
  
  private void addMessageListenerToFolder(Folder folder, EmailTreeItem<String> emailTreeItem) {
    folder.addMessageCountListener(new MessageCountListener() {
      @Override
      public void messagesAdded(MessageCountEvent e) {
        //here maile are in array, but message is in the folder already, but at the end
        for (int i = 0; i < e.getMessages().length; i++) {
          try {
            Message message = folder.getMessage(folder.getMessageCount() - i);
            emailTreeItem.addEmailToTop(message);
          } catch (MessagingException ex) {
            ex.printStackTrace();
          }
        }
        
      }
      
      @Override
      public void messagesRemoved(MessageCountEvent e) {
        System.out.println("remove--" + e);
        
      }
    });
  }
}
