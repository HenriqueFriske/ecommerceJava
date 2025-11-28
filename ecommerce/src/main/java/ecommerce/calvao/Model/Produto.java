package ecommerce.calvao.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Produto {

    // Campos mapeados do JSON do Backend
    private String id;
    private String name;
    private double price;
    private Double promotionalPrice;
    private boolean isPromotionActive;
    private int discountPercentage;
    private String mainImage;
    private String description; // Campo novo para a tela de detalhes

    public Produto() {}

    // --- GETTERS (Necessários para o Controller acessar os dados) ---

    public String getId() { return id; }
    public String getName() { return name; }
    public double getPrice() { return price; }
    public Double getPromotionalPrice() { return promotionalPrice; }
    public boolean isIsPromotionActive() { return isPromotionActive; }
    public int getDiscountPercentage() { return discountPercentage; }
    public String getMainImage() { return mainImage; }
    public String getDescription() { return description; }

    // --- SETTERS (Necessários para o Jackson preencher os dados) ---

    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setPrice(double price) { this.price = price; }
    public void setPromotionalPrice(Double promotionalPrice) { 
        this.promotionalPrice = promotionalPrice != null ? promotionalPrice : 0.0;
    }
    // O backend pode enviar como "isPromotionActive" ou outro nome, o Jackson tenta casar
    public void setIsPromotionActive(boolean isPromotionActive) { this.isPromotionActive = isPromotionActive; }
    public void setDiscountPercentage(int discountPercentage) { this.discountPercentage = discountPercentage; }
    public void setMainImage(String mainImage) { this.mainImage = mainImage; }
    public void setDescription(String description) { this.description = description; }
}