package ecommerce.calvao.Controller;

import ecommerce.calvao.App.MainApplication;
import ecommerce.calvao.Model.Produto;
import ecommerce.calvao.Service.ProductAPI;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.Locale;

public class ProdutoDetalheController {

    @FXML private Label nomeLabel;
    @FXML private Label precoLabel;
    @FXML private Text descricaoText;
    @FXML private ImageView produtoImage;
    @FXML private Label placeholderLabel;

    private final ProductAPI productAPI = new ProductAPI();
    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    /**
     * Chamado pela MainApplication para carregar os dados.
     */
    public void setProductId(String productId) {
        // Busca os dados completos no backend
        productAPI.fetchProductById(productId)
                .thenAccept(produto -> {
                    Platform.runLater(() -> preencherTela(produto));
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> {
                        nomeLabel.setText("Erro ao carregar produto.");
                        e.printStackTrace();
                    });
                    return null;
                });
    }

    private void preencherTela(Produto produto) {
        if (produto == null) return;

        nomeLabel.setText(produto.getName());
        
        // Descrição do objeto Produto (se não tiver descrição, coloca um texto padrão)
        // Você precisa garantir que a classe Produto.java tenha o campo 'description' e seu Getter.
        // Se ainda não tiver, adicione na classe Model/Produto.java: private String description;
        // e o getter correspondente.
        // Assumindo que você vai adicionar ou o Jackson mapeia para um campo genérico:
        
        // *NOTA*: Verifique se o seu Model/Produto.java tem o campo description.
        // Se não tiver, o Jackson vai ignorar. Vamos assumir que você adicionará.
        // Por enquanto, uso um placeholder se não houver getter.
         String desc = "Descrição detalhada do produto " + produto.getName() + ". " 
                 + "Tratamento inovador que proporciona maciez e brilho instantâneos.";
         // desc = produto.getDescription(); // Descomente quando adicionar ao Model
         
        descricaoText.setText(desc);

        // Preço (Lógica de Promoção)
        double precoFinal = produto.isIsPromotionActive() && produto.getPromotionalPrice() > 0 
                ? produto.getPromotionalPrice() 
                : produto.getPrice();
        precoLabel.setText(CURRENCY.format(precoFinal));

        // Imagem (Mesma lógica do CellController)
        carregarImagem(produto.getMainImage());
    }

    private void carregarImagem(String urlImagem) {
        produtoImage.setImage(null);
        placeholderLabel.setVisible(true);

        if (urlImagem != null && !urlImagem.isBlank()) {
            if (urlImagem.endsWith(".webp")) {
                urlImagem = urlImagem.replace(".webp", ".jpg");
            }
            
            Image image = new Image(urlImagem, true);
            image.exceptionProperty().addListener((obs, o, n) -> {
                if (n != null) placeholderLabel.setVisible(true);
            });
            image.progressProperty().addListener((obs, o, n) -> {
                if (n.doubleValue() == 1.0) placeholderLabel.setVisible(false);
            });
            
            produtoImage.setImage(image);
        }
    }

    @FXML
    private void handleVoltar() {
        // Volta para o catálogo
        Stage stage = (Stage) nomeLabel.getScene().getWindow();
        MainApplication.showCatalogoScene(stage);
    }
}