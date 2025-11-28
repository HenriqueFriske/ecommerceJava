package ecommerce.calvao.App;

import ecommerce.calvao.Controller.ProdutoDetalheController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApplication extends Application {

    private static final String LOGIN_VIEW = "/ecommerce/calvao/View/LoginView.fxml";
    private static final String CATALOGO_VIEW = "/ecommerce/calvao/View/CatalogoView.fxml";
    private static final String DETALHE_VIEW = "/ecommerce/calvao/View/ProdutoDetalheView.fxml";

    @Override
    public void start(Stage primaryStage) throws IOException {
        // Inicia direto no Catálogo para facilitar seus testes
        showCatalogoScene(primaryStage);
    }

    public static void showLoginScene(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(LOGIN_VIEW));
            Scene scene = new Scene(fxmlLoader.load());
            scene.getStylesheets().add(MainApplication.class.getResource("/styles.css").toExternalForm());

            stage.setTitle("Login - Calvão de Cria");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    // ESTE É O MÉTODO QUE ESTAVA FALTANDO
    public static void showCatalogoScene(Stage stage) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(CATALOGO_VIEW));
            BorderPane root = fxmlLoader.load();
            Scene scene = new Scene(root, 1200, 800);
            scene.getStylesheets().add(MainApplication.class.getResource("/styles.css").toExternalForm());
            
            stage.setTitle("Calvão de Cria - Catálogo de Produtos");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Método para abrir os detalhes
    public static void showDetalheScene(Stage stage, String productId) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainApplication.class.getResource(DETALHE_VIEW));
            Scene scene = new Scene(fxmlLoader.load(), 1200, 800);
            scene.getStylesheets().add(MainApplication.class.getResource("/styles.css").toExternalForm());
            
            // Passa o ID para o controlador da nova tela
            ProdutoDetalheController controller = fxmlLoader.getController();
            controller.setProductId(productId);
            
            stage.setTitle("Detalhes do Produto");
            stage.setScene(scene);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}