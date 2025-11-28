package ecommerce.calvao.Service; 

import ecommerce.calvao.Model.Produto; 
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class ProductAPI {

    private static final String BASE_URL = "http://localhost:3000/api/v1/products";
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Busca produtos no backend.
     */
    public CompletableFuture<List<Produto>> fetchProducts(Double minPrice, Double maxPrice, String searchTerm) {
        
        StringBuilder uriBuilder = new StringBuilder(BASE_URL).append("?limit=20&page=1"); 
       
        if (minPrice != null) {
            uriBuilder.append("&minPrice=").append(minPrice); 
        }
        if (maxPrice != null) {
            uriBuilder.append("&maxPrice=").append(maxPrice); 
        }
        
        if (searchTerm != null && !searchTerm.trim().isEmpty()) {
            try {
                String encodedSearch = java.net.URLEncoder.encode(searchTerm.trim(), java.nio.charset.StandardCharsets.UTF_8.toString());
                uriBuilder.append("&search=").append(encodedSearch); 
            } catch (UnsupportedEncodingException e) {
                // Ignora erro de encoding
            }
        }

        uriBuilder.append("&sortBy=price&order=asc"); 

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(uriBuilder.toString()))
                .header("Accept", "application/json")
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseProducts);
    }

    /**
     * Busca um único produto pelo ID.
     */
    public CompletableFuture<Produto> fetchProductById(String productId) {
        String url = BASE_URL + "/" + productId;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Accept", "application/json")
                .GET()
                .build();

        return httpClient.sendAsync(request, HttpResponse.BodyHandlers.ofString())
                .thenApply(HttpResponse::body)
                .thenApply(this::parseSingleProduct);
    }

    private List<Produto> parseProducts(String responseBody) {
        List<Produto> produtos = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode dataNode = root.get("data");

            if (dataNode != null && dataNode.isArray()) {
                for (JsonNode productNode : dataNode) {
                    Produto p = objectMapper.treeToValue(productNode, Produto.class);
                    produtos.add(p);
                }
            }
        } catch (Exception e) {
            System.err.println("Erro ao parsear JSON: " + e.getMessage());
            e.printStackTrace();
        }
        return produtos;
    }

    private Produto parseSingleProduct(String responseBody) {
        try {
            JsonNode root = objectMapper.readTree(responseBody);
            JsonNode dataNode = root.get("data");
            
            if (dataNode != null) {
                return objectMapper.treeToValue(dataNode, Produto.class);
            }
        } catch (Exception e) {
            System.err.println("Erro ao parsear produto único: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
    }
}