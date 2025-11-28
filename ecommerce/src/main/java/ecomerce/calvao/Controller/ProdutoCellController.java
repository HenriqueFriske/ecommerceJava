package ecommerce.calvao.Controller;

import ecommerce.calvao.Model.Produto;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.text.NumberFormat;
import java.util.Locale;

public class ProdutoCellController {

    @FXML private ImageView produtoImage;
    @FXML private Label nomeLabel;
    @FXML private Label precoOriginalLabel;
    @FXML private Label precoDescontoLabel;
    @FXML private Label descontoBadge;
    
    // NOVO: Referência ao Label de fundo
    @FXML private Label placeholderLabel; 

    private static final NumberFormat CURRENCY = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));

    public void setProduto(Produto produto) {
        nomeLabel.setText(produto.getName());
        carregarImagem(produto);
        atualizarPrecos(produto);
    }

    private void carregarImagem(Produto produto) {
        produtoImage.setImage(null);
        
        // Assume que não tem imagem inicialmente (mostra o texto)
        placeholderLabel.setVisible(true);

        String urlImagem = produto.getMainImage();

        if (urlImagem != null && !urlImagem.isBlank()) {
            urlImagem = urlImagem.trim();

            if (urlImagem.endsWith(".webp")) {
                urlImagem = urlImagem.replace(".webp", ".jpg");
            }

            // Se temos uma URL válida, ESCONDEMOS o texto de fundo
            placeholderLabel.setVisible(false);

            Image image = new Image(urlImagem, true);

            // Se der erro no carregamento, mostramos o texto de volta
            image.exceptionProperty().addListener((obs, oldEx, newEx) -> {
                if (newEx != null) {
                    System.err.println("❌ Erro imagem: " + produto.getName() + " -> " + newEx.getMessage());
                    placeholderLabel.setVisible(true); // Traz o texto de volta
                }
            });

            produtoImage.setImage(image);
        }
    }

    private void atualizarPrecos(Produto produto) {
        if (produto.isIsPromotionActive() && produto.getPromotionalPrice() > 0) {
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
            precoDescontoLabel.setText(CURRENCY.format(produto.getPrice()));
            precoOriginalLabel.setVisible(false);
            descontoBadge.setVisible(false);
        }
    }
}