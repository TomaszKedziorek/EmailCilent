package com.tom.controller.persistance;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PersistenceAccess {
  
  //this is convention where we will fnd saved data
  private String VALID_ACCOUNT_LOCATION = System.getenv("APPDATA") + File.separator + "validAccounts.ser";
  private Encoder encoder = new Encoder();
  
  public List<ValidAccount> loadFromPersistence() {
    List<ValidAccount> resultList = new ArrayList<ValidAccount>();
    try {
      FileInputStream fileInputStream = new FileInputStream(VALID_ACCOUNT_LOCATION);
      ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
      List<ValidAccount> persistentList = (List<ValidAccount>) objectInputStream.readObject();
      decodePassword(persistentList);
      resultList.addAll(persistentList);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return resultList;
  }
  
  public void saveToPersistence(List<ValidAccount> validAccounts) {
    File file = null;
    FileOutputStream fileOutputStream = null;
    ObjectOutputStream objectOutputStream = null;
    try {
      file = new File(VALID_ACCOUNT_LOCATION);
      fileOutputStream = new FileOutputStream(file);
      objectOutputStream = new ObjectOutputStream(fileOutputStream);
      encodePassword(validAccounts);
      objectOutputStream.writeObject(validAccounts);
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (fileOutputStream != null) {
        try {
          fileOutputStream.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
      if (objectOutputStream != null) {
        try {
          objectOutputStream.close();
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }
  }
  
  private void decodePassword(List<ValidAccount> persistentList) {
    for (ValidAccount validAccount : persistentList) {
      String password = validAccount.getPassword();
      validAccount.setPassword(encoder.decode(password));
    }
  }
  
  private void encodePassword(List<ValidAccount> persistentList) {
    for (ValidAccount validAccount : persistentList) {
      String password = validAccount.getPassword();
      validAccount.setPassword(encoder.encode(password));
    }
  }
  
}
