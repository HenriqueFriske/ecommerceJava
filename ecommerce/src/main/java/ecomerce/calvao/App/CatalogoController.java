package ecommerce.calvao.App;

import ecommerce.calvao.Controller.ProdutoCellController;
import ecommerce.calvao.Model.Produto;
import ecommerce.calvao.Service.ProductAPI;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CatalogoController {

    @FXML private FlowPane produtosFlowPane;
    @FXML private Label contadorProdutosLabel;
    @FXML private ToggleGroup precoToggleGroup;
    @FXML private VBox filtroVBox;
    
    @FXML private TextField searchField;
    @FXML private RadioButton allPricesRadio; 

    private final ProductAPI productAPI = new ProductAPI();

    // Configuração dos intervalos de preço
    private final Map<String, Double[]> priceFilters = Map.of(
        "0-40", new Double[]{0.0, 40.0},
        "40-100", new Double[]{40.0, 100.0},
        "100-200", new Double[]{100.0, 200.0},
        "200-500", new Double[]{200.0, 500.0},
        "500-MAX", new Double[]{500.0, null}, // Null no max significa "Infinito"
        "ALL", new Double[]{null, null}
    );

    @FXML
    public void initialize() {
        initializeFilterListeners();
        
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            carregarProdutos();
        });
        
        allPricesRadio.setSelected(true);
    }

    private void initializeFilterListeners() {
        filtroVBox.getChildren().forEach(node -> {
            if (node instanceof VBox) {
                ((VBox) node).getChildren().stream()
                    .filter(child -> child instanceof RadioButton)
                    .map(child -> (RadioButton) child)
                    .forEach(rb -> {
                        rb.setToggleGroup(precoToggleGroup);
                        rb.selectedProperty().addListener((obs, oldVal, newVal) -> {
                            if (newVal) carregarProdutos(); 
                        });
                    });
            }
        });
    }

    private void carregarProdutos() {
        produtosFlowPane.getChildren().clear();
        contadorProdutosLabel.setText("Filtrando produtos...");

        // 1. Captura os limites de preço selecionados
        Double minPriceTemp = null;
        Double maxPriceTemp = null;
        RadioButton selectedRadio = (RadioButton) precoToggleGroup.getSelectedToggle();
        
        if (selectedRadio != null) {
            String key = (String) selectedRadio.getUserData();
            if (priceFilters.containsKey(key)) {
                Double[] limits = priceFilters.get(key);
                minPriceTemp = limits[0];
                maxPriceTemp = limits[1];
            }
        }
        
        // Variáveis finais para usar dentro do Lambda
        final Double minPrice = minPriceTemp;
        final Double maxPrice = maxPriceTemp;
        
        String searchTerm = searchField.getText();
        
        // 2. Chama a API pedindo TUDO (passa null nos preços para o backend não filtrar errado)
        productAPI.fetchProducts(null, null, searchTerm)
                .thenApply(produtos -> {
                    // 3. FILTRAGEM INTELIGENTE NO JAVA (CLIENTE)
                    // Aqui aplicamos a lógica: Preço Promocional x Preço Original
                    return produtos.stream().filter(p -> {
                        // Calcula qual o preço real que o cliente vai pagar
                        double precoEfetivo = p.isIsPromotionActive() && p.getPromotionalPrice() > 0 
                                ? p.getPromotionalPrice() 
                                : p.getPrice();
                        
                        // Verifica se está dentro do intervalo selecionado
                        boolean maiorQueMinimo = (minPrice == null) || (precoEfetivo >= minPrice);
                        boolean menorQueMaximo = (maxPrice == null) || (precoEfetivo <= maxPrice);
                        
                        return maiorQueMinimo && menorQueMaximo;
                    }).collect(Collectors.toList());
                })
                .thenAccept(produtosFiltrados -> {
                    // 4. Atualiza a tela com a lista já filtrada corretamente
                    Platform.runLater(() -> {
                        String textoContador = produtosFiltrados.isEmpty() 
                                ? "Nenhum produto encontrado" 
                                : produtosFiltrados.size() + " produtos encontrados";
                        contadorProdutosLabel.setText(textoContador);
                        
                        produtosFiltrados.forEach(this::adicionarProdutoCell);
                    });
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> {
                        String msg = (e.getCause() != null) ? e.getCause().getMessage() : e.getMessage();
                        contadorProdutosLabel.setText("Erro: " + msg);
                        e.printStackTrace();
                    });
                    return null; 
                });
    }

    private void adicionarProdutoCell(Produto produto) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/ecommerce/calvao/View/ProdutoCell.fxml"));
            VBox produtoCell = fxmlLoader.load();
            ProdutoCellController controller = fxmlLoader.getController();
            controller.setProduto(produto);
            produtosFlowPane.getChildren().add(produtoCell);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRedefinirBusca() {
        searchField.clear();
        allPricesRadio.setSelected(true);
    }
}