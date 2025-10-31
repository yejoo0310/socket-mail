package socketmail;

import socketmail.controller.MainController;
import socketmail.util.ConfigManager;
import socketmail.view.MainView;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ConfigManager configManager = new ConfigManager();
            MainView mainView = new MainView();
            new MainController(mainView, configManager);
            mainView.setVisible(true);
        });
    }
}
