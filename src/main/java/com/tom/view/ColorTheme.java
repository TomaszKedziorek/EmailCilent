package com.tom.view;

public enum ColorTheme {
  LIGHT,
  DARK,
  DEFAULT;
  
  public static String getCssPath(ColorTheme colorTheme) {
    switch (colorTheme) {
      case LIGHT:
        return "css/themeLight.css";
      case DARK:
        return "css/themeDark.css";
      case DEFAULT:
        return "css/themeDefault.css";
      default:
        return null;
    }
  }
}
