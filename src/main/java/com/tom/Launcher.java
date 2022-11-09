package com.tom;

import com.tom.controller.EmailLoginResult;
import com.tom.controller.persistance.PersistenceAccess;
import com.tom.controller.persistance.ValidAccount;
import com.tom.controller.services.LoginService;
import com.tom.model.EmailAccount;
import com.tom.view.ViewFactory;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

public class Launcher extends Application {
  public static void main(String[] args) {
    launch(args);
  }
  
  private PersistenceAccess persistenceAccess = new PersistenceAccess();
  private EmailManager emailManager = new EmailManager();
  
  @Override
  public void start(Stage stage) throws Exception {
    ViewFactory viewFactory = new ViewFactory(emailManager);
    checkPersistence(viewFactory);
  }
  
  private void checkPersistence(ViewFactory viewFactory) {
    List<ValidAccount> validAccountList = persistenceAccess.loadFromPersistence();
    if (validAccountList.size() > 0) {
      for (ValidAccount validAccount : validAccountList) {
        EmailAccount emailAccount = new EmailAccount(validAccount.getAddress(), validAccount.getPassword());
        LoginService loginService = new LoginService(emailAccount, emailManager);
        loginService.start();
        //-------------------------------------------
        loginService.setOnSucceeded(event -> {
          EmailLoginResult emailLoginResult = loginService.getValue();
          if (emailLoginResult.equals(EmailLoginResult.SUCCESS)) {
            System.out.println("Success!");
            viewFactory.showMainWindowIfNotShown();
          } else {
            System.out.println("Failure!");
            viewFactory.showLoginWindow();
          }
        });
        //-----------------------------------------
      }
    } else {
      viewFactory.showLoginWindow();
    }
  }
  
  @Override
  public void stop() throws Exception {
    List<ValidAccount> validAccountList = new ArrayList<ValidAccount>();
    for (EmailAccount emailAccount : emailManager.getEmailAccounts()) {
      validAccountList.add(new ValidAccount(emailAccount.getAddress(), emailAccount.getPassword()));
    }
    persistenceAccess.saveToPersistence(validAccountList);
  }
  
}
