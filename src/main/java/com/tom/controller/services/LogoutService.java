package com.tom.controller.services;

import com.tom.EmailManager;
import com.tom.model.EmailAccount;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javax.mail.MessagingException;

public class LogoutService extends Service<Boolean> {
  
  private EmailManager emailManager;
  
  public LogoutService(EmailManager emailManager) {
    this.emailManager = emailManager;
  }
  
  private void deleteAccounts() throws MessagingException {
    for (EmailAccount account : emailManager.getEmailAccounts()) {
      account.getStore().close();
    }
    emailManager.deleteEmailAccounts();
  }
  
  @Override
  protected Task<Boolean> createTask() {
    return new Task<Boolean>() {
      @Override
      protected Boolean call() throws Exception {
        deleteAccounts();
        return null;
      }
    };
  }
}
