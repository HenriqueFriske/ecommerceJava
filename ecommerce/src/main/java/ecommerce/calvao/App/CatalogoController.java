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
import java.net.URL;
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

    private final Map<String, Double[]> priceFilters = Map.of(
        "0-40", new Double[]{0.0, 40.0},
        "40-100", new Double[]{40.0, 100.0},
        "100-200", new Double[]{100.0, 200.0},
        "200-500", new Double[]{200.0, 500.0},
        "500-MAX", new Double[]{500.0, null},
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
                            if (newVal) {
                                carregarProdutos(); 
                            }
                        });
                    });
            }
        });
    }

    private void carregarProdutos() {
        // Marcador visual de que a busca começou
        contadorProdutosLabel.setText("Buscando...");

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
        
        final Double minPrice = minPriceTemp;
        final Double maxPrice = maxPriceTemp;
        String searchTerm = searchField.getText();
        
        productAPI.fetchProducts(null, null, searchTerm)
                .thenApply(produtos -> {
                    // Filtragem no Java (Cliente)
                    return produtos.stream().filter(p -> {
                        double precoEfetivo = p.isIsPromotionActive() && p.getPromotionalPrice() > 0 
                                ? p.getPromotionalPrice() 
                                : p.getPrice();
                        
                        boolean maiorQueMinimo = (minPrice == null) || (precoEfetivo >= minPrice);
                        boolean menorQueMaximo = (maxPrice == null) || (precoEfetivo <= maxPrice);
                        
                        return maiorQueMinimo && menorQueMaximo;
                    }).collect(Collectors.toList());
                })
                .thenAccept(produtosFiltrados -> {
                    Platform.runLater(() -> {
                        // LIMPEZA DA TELA AGORA ACONTECE AQUI, NA HORA DE DESENHAR
                        produtosFlowPane.getChildren().clear();
                        
                        String texto = produtosFiltrados.isEmpty() 
                                ? "Nenhum produto encontrado" 
                                : produtosFiltrados.size() + " produtos encontrados";
                        contadorProdutosLabel.setText(texto);
                        
                        for (Produto p : produtosFiltrados) {
                            adicionarProdutoCell(p);
                        }
                    });
                })
                .exceptionally(e -> {
                    Platform.runLater(() -> {
                        System.err.println("❌ Erro na busca de produtos:");
                        e.printStackTrace();
                        contadorProdutosLabel.setText("Erro ao carregar.");
                    });
                    return null; 
                });
    }

    private void adicionarProdutoCell(Produto produto) {
        try {
            URL fxmlUrl = getClass().getResource("/ecommerce/calvao/View/ProdutoCell.fxml");
            if (fxmlUrl == null) {
                System.err.println("❌ FXML NÃO ENCONTRADO: /ecommerce/calvao/View/ProdutoCell.fxml");
                return;
            }

            FXMLLoader fxmlLoader = new FXMLLoader(fxmlUrl);
            VBox produtoCell = fxmlLoader.load();
            
            ProdutoCellController controller = fxmlLoader.getController();
            controller.setProduto(produto);
            
            produtosFlowPane.getChildren().add(produtoCell);
            
        } catch (Exception e) {
            System.err.println("❌ Erro ao criar card para: " + produto.getName());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleRedefinirBusca() {
        searchField.clear();
        allPricesRadio.setSelected(true);
    }
}