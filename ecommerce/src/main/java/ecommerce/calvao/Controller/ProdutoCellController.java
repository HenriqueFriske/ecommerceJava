package ecommerce.calvao.Controller;

import ecommerce.calvao.App.MainApplication;
import ecommerce.calvao.Model.Produto;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.text.NumberFormat;
import java.util.Locale;

public class ProdutoCellController {

    @FXML private ImageView produtoImage;
    @FXML private Label nomeLabel;
    @FXML private Label precoOriginalLabel;
    @FXML private Label precoDescontoLabel;
    @FXML private Label descontoBadge;
    
    // Label que fica "atrás" da imagem ("Sem Imagem")
    @FXML private Label placeholderLabel; 

    // Variável para guardar o ID do produto deste card
    private String currentProductId;

    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    /**
     * Método principal chamado pelo CatalogoController para preencher os dados.
     */
    public void setProduto(Produto produto) {
        // 1. Guarda o ID para usar no clique
        this.currentProductId = produto.getId(); // Certifique-se que Produto.java tem getId()

        // 2. Define o nome
        nomeLabel.setText(produto.getName());
        
        // 3. Carrega a imagem com tratamento de erro e formato
        carregarImagem(produto);

        // 4. Calcula e exibe os preços
        atualizarPrecos(produto);
    }

    /**
     * Ação de clique no card (definida no FXML no VBox principal: onMouseClicked="#handleCardClick")
     */
    @FXML 
    private void handleCardClick() {
        if (currentProductId != null) {
            Stage stage = (Stage) nomeLabel.getScene().getWindow();
            MainApplication.showDetalheScene(stage, currentProductId);
        }
    }

    private void carregarImagem(Produto produto) {
        // Limpa imagem anterior
        produtoImage.setImage(null);
        
        // Mostra o texto "Sem Imagem" inicialmente
        placeholderLabel.setVisible(true);

        String urlImagem = produto.getMainImage();

        if (urlImagem != null && !urlImagem.isBlank()) {
            urlImagem = urlImagem.trim();

            // Correção para Cloudinary: Troca .webp por .jpg pois JavaFX não suporta WebP nativamente
            if (urlImagem.endsWith(".webp")) {
                urlImagem = urlImagem.replace(".webp", ".jpg");
            }

            // Esconde o placeholder pois temos uma URL válida
            placeholderLabel.setVisible(false);

            // Carrega em background (true)
            Image image = new Image(urlImagem, true);

            // Listener: Se der erro no carregamento, mostra o placeholder de volta
            image.exceptionProperty().addListener((obs, oldEx, newEx) -> {
                if (newEx != null) {
                    System.err.println("❌ Erro imagem: " + produto.getName() + " -> " + newEx.getMessage());
                    placeholderLabel.setVisible(true); 
                }
            });

            produtoImage.setImage(image);
        }
    }

    private void atualizarPrecos(Produto produto) {
        if (produto.isIsPromotionActive() && produto.getPromotionalPrice() > 0) {
            // PROMOÇÃO ATIVA
            precoOriginalLabel.setText(CURRENCY.format(produto.getPrice()));
            precoOriginalLabel.setVisible(true);
            
            precoDescontoLabel.setText(CURRENCY.format(produto.getPromotionalPrice()));
            
            int desconto = produto.getDiscountPercentage();
            if (desconto == 0) {
                 desconto = (int) Math.round((1 - (produto.getPromotionalPrice() / produto.getPrice())) * 100);
            }
            descontoBadge.setText(desconto + "% OFF");
            descontoBadge.setVisible(true);
        } else {
            // PREÇO NORMAL
            precoDescontoLabel.setText(CURRENCY.format(produto.getPrice()));
            
            precoOriginalLabel.setVisible(false);
            descontoBadge.setVisible(false);
        }
    }
}