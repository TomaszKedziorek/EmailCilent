package com.tom.controller;

import com.tom.EmailManager;
import com.tom.view.ColorTheme;
import com.tom.view.FontSize;
import com.tom.view.ViewFactory;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Slider;
import javafx.util.StringConverter;

import java.net.URL;
import java.util.ResourceBundle;

public class OptionsWindowController extends BaseController implements Initializable {
  
  private String windowTitle = "Email Client - Options";
  @FXML
  private Slider fontSizePicker;
  @FXML
  private ChoiceBox<ColorTheme> themePicker;
  
  public OptionsWindowController(EmailManager emailManager, ViewFactory viewFactory, String fxmlName) {
    super(emailManager, viewFactory, fxmlName);
  }
  
  @Override
  public String getWindowTitle() {
    return windowTitle;
  }
  
  @FXML
  void applyButtonAction() {
    viewFactory.setColorTheme(themePicker.getValue());
    viewFactory.setFontSize(FontSize.values()[(int) fontSizePicker.getValue()]);
    viewFactory.updateStyles();
  }
  
  @FXML
  void cancelButtonAction() {
    closeStage(fontSizePicker);
  }
  
  @Override
  //This method from Initialize interface run just after we call constructor of our class
  public void initialize(URL url, ResourceBundle resourceBundle) {
    setUpThemePicker();
    setUpFontSizePicker();
  }
  
  private void setUpFontSizePicker() {
    fontSizePicker.setMin(0);
    fontSizePicker.setMax(FontSize.values().length - 1);
    fontSizePicker.setValue(viewFactory.getFontSize().ordinal());
    fontSizePicker.setMinorTickCount(0);
    fontSizePicker.setMajorTickUnit(1);
    fontSizePicker.setBlockIncrement(1);
    fontSizePicker.setSnapToTicks(true);
    fontSizePicker.setShowTickMarks(true);
    fontSizePicker.setShowTickLabels(true);
    fontSizePicker.setLabelFormatter(new StringConverter<Double>() {
      @Override
      public String toString(Double aDouble) {
        int i = aDouble.intValue();
        return FontSize.values()[i].toString();
      }
      
      @Override
      public Double fromString(String s) {
        return null;
      }
    });
    fontSizePicker.valueProperty().addListener((obs, oldVal, newVal) -> {
      fontSizePicker.setValue(newVal.intValue());
    });
  }
  
  private void setUpThemePicker() {
    //here we must pass ObservableList
    //indicate in ThemePicker field what it contains
    themePicker.setItems(FXCollections.observableArrayList(ColorTheme.values()));
    themePicker.setValue(viewFactory.getColorTheme());
  }
}
