package com.tom.view;

import com.tom.EmailManager;
import com.tom.controller.*;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;

//This ViewFactory pass the view events to EmailManager
public class ViewFactory {
  
  private EmailManager emailManager;
  private ArrayList<Stage> activeStages;
  private boolean mainWindowInitialized = false;
  
  public ViewFactory(EmailManager emailManager) {
    this.emailManager = emailManager;
    activeStages = new ArrayList<Stage>();
  }
  
  public boolean isMainWindowInitialized() {
    return mainWindowInitialized;
  }
  
  public void setColorTheme(ColorTheme colorTheme) {
    this.colorTheme = colorTheme;
  }
  
  public void setFontSize(FontSize fontSize) {
    this.fontSize = fontSize;
  }
  
  public ColorTheme getColorTheme() {
    return colorTheme;
  }
  
  public FontSize getFontSize() {
    return fontSize;
  }
  
  //View options handling
  private ColorTheme colorTheme = ColorTheme.DARK;
  private FontSize fontSize = FontSize.MEDIUM;
  
  private void initializeStage(BaseController baseController) {
    FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(baseController.getFxmlName()));
    fxmlLoader.setController(baseController);
    
    Parent parent;
    try {
      parent = fxmlLoader.load();
    } catch (IOException e) {
      e.printStackTrace();
      return;
    }
    Scene scene = new Scene(parent);
    Stage stage = new Stage();
    stage.setScene(scene);
    stage.show();
    stage.setTitle(baseController.getWindowTitle());
    activeStages.add(stage);
    updateStyles();
  }
  
  public void showLoginWindow() {
    //Here fxmlName has no "view/" part indicating to folder in resources because this ViewFactory also is in view folder
    BaseController controller = new LoginWindowController(emailManager, this, "LoginWindow.fxml");
    initializeStage(controller);
  }
  
  public void showMainWindow() {
    BaseController controller = new MainWindowController(emailManager, this, "MainWindow.fxml");
    initializeStage(controller);
    mainWindowInitialized = true;
  }
  
  public void showMainWindowIfNotShown() {
    if (!isMainWindowInitialized()) {
      showMainWindow();
    }
  }
  
  public void showComposeMessageWindow() {
    BaseController controller = new ComposeMessageController(emailManager, this, "ComposeMessageWindow.fxml");
    initializeStage(controller);
  }
  
  public void showEmailDetailsWindow() {
    BaseController controller = new EmailsDetailController(emailManager, this, "EmailDetailsWindow.fxml");
    initializeStage(controller);
  }
  
  public void showOptionsWindow() {
    BaseController controller = new OptionsWindowController(emailManager, this, "OptionsWindow.fxml");
    initializeStage(controller);
  }
  
  public void showLogoutWindow() {
    BaseController controller = new LogoutWindowController(emailManager, this, "LogoutWindow.fxml");
    initializeStage(controller);
  }
  
  public void closeStage(Stage stageToClose) {
    stageToClose.close();
    activeStages.remove(stageToClose);
  }
  
  public void closeAllActiveStages() {
    for (Stage activeStage : activeStages) {
      activeStage.close();
    }
    activeStages.clear();
  }
  
  //css styles are added to active scenes, but we need to have list of them
  //for this is activeStages array list, we also need to add stage in initializer to this list
  public void updateStyles() {
    for (Stage stage : activeStages) {
      Scene scene = stage.getScene();
      scene.getStylesheets().clear();
      scene.getStylesheets().add(getClass().getResource(ColorTheme.getCssPath(colorTheme)).toExternalForm());
      scene.getStylesheets().add(getClass().getResource(FontSize.getCssPath(fontSize)).toExternalForm());
    }
  }
}
