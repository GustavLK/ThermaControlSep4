package client.utility;

import client.model.ModelHandler;
import client.view.ViewHandler;
import client.viewmodel.ViewModelHandler;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainClient extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        ModelHandler modelHandler = new ModelHandler();
        modelHandler.startSimulation();

        ViewModelHandler viewModelHandler = new ViewModelHandler(modelHandler);
        ViewHandler viewHandler = new ViewHandler(viewModelHandler);
        viewHandler.start(stage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
