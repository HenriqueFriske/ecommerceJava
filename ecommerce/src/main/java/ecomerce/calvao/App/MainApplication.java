package ecommerce.calvao.App;



import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        try {
            // Carrega o FXML principal (CatalogoView.fxml)
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ecommerce/calvao/View/CatalogoView.fxml"));
            BorderPane root = fxmlLoader.load();
            
            Scene scene = new Scene(root, 1200, 800);
            
            // Carrega o CSS global (note o caminho absoluto)
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            
            primaryStage.setTitle("Calvão de Cria - Catálogo de Produtos");
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}