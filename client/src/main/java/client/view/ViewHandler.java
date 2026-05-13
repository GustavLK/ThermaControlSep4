package client.view;

import client.viewmodel.ViewModelHandler;
import client.viewmodel.ViewScene;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ViewHandler {
    private Stage primaryStage;
    private ViewModelHandler viewModelHandler;

    public ViewHandler(ViewModelHandler viewModelHandler) {
        this.viewModelHandler = viewModelHandler;
    }

    public void start(Stage stage) throws Exception {
        this.primaryStage = stage;
        primaryStage.setTitle("ThermaControl");
        primaryStage.setMinWidth(1100);
        primaryStage.setMinHeight(720);
        openView(ViewScene.LOGIN);
        primaryStage.show();
    }

    public void openView(ViewScene scene) {
        try {
            String fxmlFile = switch (scene) {
                case LOGIN -> "login.fxml";
                case DASHBOARD -> "dashboard.fxml";
                case ALARM_LOG -> "alarm_log.fxml";
                case CONFIGURATION -> "config.fxml";
            };

            URL url = getClass().getResource("/fxml/" + fxmlFile);
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            // Pass viewHandler and viewModelHandler to controller
            Object controller = loader.getController();
            if (controller instanceof BaseViewController base) {
                base.init(this, viewModelHandler);
            }

            double width = primaryStage.getScene() != null ? primaryStage.getScene().getWidth() : 1200;
            double height = primaryStage.getScene() != null ? primaryStage.getScene().getHeight() : 800;

            Scene newScene = new Scene(root, width, height);
            primaryStage.setScene(newScene);
            URL cssUrl = getClass().getResource("/style.css");
            if (cssUrl != null) {
                newScene.getStylesheets().add(cssUrl.toExternalForm());
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeView() {
        primaryStage.close();
    }

    public ViewModelHandler getViewModelHandler() {
        return viewModelHandler;
    }
}
