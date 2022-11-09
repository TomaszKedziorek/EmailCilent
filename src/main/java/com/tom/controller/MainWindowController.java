package com.tom.controller;

import com.tom.EmailManager;
import com.tom.controller.services.MessageRendererService;
import com.tom.model.EmailMessage;
import com.tom.model.EmailTreeItem;
import com.tom.model.SizeInteger;
import com.tom.view.ViewFactory;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.web.WebView;
import javafx.util.Callback;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class MainWindowController extends BaseController implements Initializable {
  
  private String windowTitle = "Email Client - Main";
  @FXML
  private WebView emailWebView;
  @FXML
  private TableView<EmailMessage> emailsTableView;
  @FXML
  private TreeView<String> emailsTreeView;
  @FXML
  private TableColumn<EmailMessage, String> recipientColumn;
  @FXML
  private TableColumn<EmailMessage, String> senderColumn;
  @FXML
  private TableColumn<EmailMessage, String> subjectColumn;
  @FXML
  private TableColumn<EmailMessage, SizeInteger> sizeColumn;
  @FXML
  private TableColumn<EmailMessage, Date> dateColumn;
  private MessageRendererService messageRendererService;
  private MenuItem markUnreadMenuItem = new MenuItem("mark as unread");
  private MenuItem markDeleteMenuItem = new MenuItem("delete message");
  private MenuItem markDetailsMenuItem = new MenuItem("show details");
  @FXML
  private BorderPane borderPane;
  @FXML
  private Button detailsBtn;
  @FXML
  private Button replayBtn;
  @FXML
  private Button unreadBtn;
  @FXML
  private ButtonBar buttonBar;
  @FXML
  private Label attachmentsLabel;
  
  public MainWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
    super(emailManager, viewFactory, fxmlName);
  }
  
  @Override
  public String getWindowTitle() {
    return windowTitle;
  }
  
  @FXML
  void optionsAction() {
    viewFactory.showOptionsWindow();
  }
  
  @FXML
  void addAccountAction() {
    viewFactory.showLoginWindow();
  }
  
  @FXML
  void composeMessageAction() {
    viewFactory.showComposeMessageWindow();
  }
  
  @FXML
  void logoutAction() {
    viewFactory.showLogoutWindow();
  }
  
  @Override
  public void initialize(URL location, ResourceBundle resources) {
    setUpEmailsTreeView();
    setUpEmailsTableView();
    setUpFolderSelection();
    setUpBoldRows();
    setUpMessageRendererService();
    setUpMessageSelection();
    setUpContextMenus();
    setUpFolderSelectedOnStart();
    setUpButtonBarButtonOrder();
    borderPane.setVisible(false);
  }
  
  private void setUpEmailsTreeView() {
    emailsTreeView.setRoot((TreeItem<String>) emailManager.getFoldersRoot());
    emailsTreeView.setShowRoot(false);
  }
  
  //Each column must have own cele value factory
  private void setUpEmailsTableView() {
    senderColumn.setCellValueFactory(new PropertyValueFactory<EmailMessage, String>("sender"));
    subjectColumn.setCellValueFactory(new PropertyValueFactory<EmailMessage, String>("subject"));
    recipientColumn.setCellValueFactory(new PropertyValueFactory<EmailMessage, String>("recipient"));
    sizeColumn.setCellValueFactory(new PropertyValueFactory<EmailMessage, SizeInteger>("size"));
    dateColumn.setCellValueFactory(new PropertyValueFactory<EmailMessage, Date>("date"));
    
    //Add context menu to each in table view
    emailsTableView.setContextMenu(new ContextMenu(markDetailsMenuItem, markUnreadMenuItem, markDeleteMenuItem));
  }
  
  private EmailTreeItem<String> getTreeViewItem(EmailTreeItem<String> item, String value) {
    if (item != null && item.getValue().toLowerCase().contains(value.toLowerCase()))
      return item;
    for (TreeItem<String> child : item.getChildren()) {
      EmailTreeItem<String> childTreeItem = getTreeViewItem((EmailTreeItem<String>) child, value);
      if (childTreeItem != null)
        return childTreeItem;
    }
    return null;
  }
  
  private void setUpFolderSelectedOnStart() {
    EmailTreeItem<String> item = getTreeViewItem(emailManager.getFoldersRoot(), "INBOX");
    if (item != null) emailsTreeView.getSelectionModel().select(item);
    else {
      emailsTreeView.getSelectionModel().select(1);
      item = (EmailTreeItem<String>) emailsTreeView.getSelectionModel().getSelectedItem();
    }
    emailManager.setSelectedFolder(item);
    emailsTableView.setItems(item.getEmailMessages());
  }
  
  private void setUpFolderSelection() {
    emailsTreeView.setOnMouseClicked(event -> {
      EmailTreeItem<String> item = (EmailTreeItem<String>) emailsTreeView.getSelectionModel().getSelectedItem();
      if (item != null) {
        emailManager.setSelectedFolder(item);
        emailsTableView.setItems(item.getEmailMessages());
      }
    });
  }
  
  private final PseudoClass unReadMessage = PseudoClass.getPseudoClass("unread-color");
  
  private void setUpBoldRows() {
    emailsTableView.setRowFactory(new Callback<TableView<EmailMessage>, TableRow<EmailMessage>>() {
      @Override
      public TableRow<EmailMessage> call(TableView<EmailMessage> param) {
        return new TableRow<EmailMessage>() {
          @Override
          protected void updateItem(EmailMessage item, boolean is_empty) {
            super.updateItem(item, is_empty);
            if (item != null) {
              if (item.isRead()) {
                pseudoClassStateChanged(unReadMessage, false);
              } else {
                pseudoClassStateChanged(unReadMessage, true);
              }
            }
          }
          
        };
        
      }
    });
  }
  
  private void setUpMessageRendererService() {
    messageRendererService = new MessageRendererService(emailWebView.getEngine());
  }
  
  private void setUpMessageSelection() {
    emailsTableView.setOnMouseClicked(event -> {
      EmailMessage emailMessage = emailsTableView.getSelectionModel().getSelectedItem();
      borderPane.setVisible(true);
      if (emailMessage != null) {
        emailManager.setSelectedMessage(emailMessage);
        if (!emailMessage.isRead()) {
          emailManager.setRead();
        }
        messageRendererService.setEmailMessage(emailMessage);
        messageRendererService.restart();//we use restart because start may be used only once
        messageRendererService.setOnSucceeded(event1 -> {
          messageRendererService.displayMessage();
          showAttachmentsLabel(emailManager.getSelectedMessage());
        });
      }
    });
  }
  
  private void setUpContextMenus() {
    markUnreadMenuItem.setOnAction(event -> {
      emailManager.setUnRead();
    });
    markDeleteMenuItem.setOnAction(event -> {
      emailManager.deleteSelectedMessage();
      //clear emailWebView
      emailWebView.getEngine().loadContent("");
      borderPane.setVisible(false);
    });
    markDetailsMenuItem.setOnAction(event -> {
      viewFactory.showEmailDetailsWindow();
    });
  }
  
  private void setUpButtonBarButtonOrder() {
    buttonBar.setButtonOrder(ButtonBar.BUTTON_ORDER_WINDOWS);
    buttonBar.setButtonMinWidth(100);
    ButtonBar.setButtonData(detailsBtn, ButtonBar.ButtonData.YES);
    ButtonBar.setButtonData(replayBtn, ButtonBar.ButtonData.NO);
    ButtonBar.setButtonData(unreadBtn, ButtonBar.ButtonData.OK_DONE);
    ButtonBar.setButtonData(attachmentsLabel, ButtonBar.ButtonData.OTHER);
  }
  
  private void showAttachmentsLabel(EmailMessage emailMessage) {
    attachmentsLabel.setMinWidth(120);
    if (emailMessage.hasAttachment()) {
      attachmentsLabel.setText("attachments: " + emailMessage.getAttachmentList().size());
    } else {
      attachmentsLabel.setText("");
    }
  }
  
  @FXML
  private void replayAction() {
    ComposeMessageController.setReplayMessage(true);
    viewFactory.showComposeMessageWindow();
  }
  
  @FXML
  private void setUnreadAction() {
    emailManager.setUnRead();
  }
  
  @FXML
  private void showDetailsAction() {
    viewFactory.showEmailDetailsWindow();
  }
}
