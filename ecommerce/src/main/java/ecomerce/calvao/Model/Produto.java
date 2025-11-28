
package ecommerce.calvao.Model;

/**
 *
 * @author henriquefriske
 */


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.*;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Produto {

    // Properties para uso em JavaFX (observáveis)
    private final StringProperty id = new SimpleStringProperty();
    private final StringProperty name = new SimpleStringProperty();
    private final DoubleProperty price = new SimpleDoubleProperty();
    private final DoubleProperty promotionalPrice = new SimpleDoubleProperty();
    private final BooleanProperty isPromotionActive = new SimpleBooleanProperty();
    private final IntegerProperty discountPercentage = new SimpleIntegerProperty(0);
    private final StringProperty mainImage = new SimpleStringProperty();

    // Construtor vazio para desserialização do Jackson/JSON
    public Produto() {}

    // Setters mapeados pelo Jackson (usando @JsonProperty para alinhar com a saída JSON)

    public void setId(String id) { this.id.set(id); }
    public void setName(String name) { this.name.set(name); }
    public void setPrice(double price) { this.price.set(price); }
    public void setPromotionalPrice(Double promotionalPrice) { 
        this.promotionalPrice.set(promotionalPrice != null ? promotionalPrice : 0.0);
    }
    public void setIsPromotionActive(boolean isPromotionActive) { 
        this.isPromotionActive.set(isPromotionActive); 
    }
    public void setDiscountPercentage(int discountPercentage) { 
        this.discountPercentage.set(discountPercentage); 
    }
    public void setMainImage(String mainImage) { this.mainImage.set(mainImage); }

    // Getters e Property getters para o Controller e FXML Binding
    public String getName() { return name.get(); }
    public StringProperty nameProperty() { return name; }
    public double getPrice() { return price.get(); }
    public DoubleProperty priceProperty() { return price; }
    public double getPromotionalPrice() { return promotionalPrice.get(); }
    public DoubleProperty promotionalPriceProperty() { return promotionalPrice; }
    public boolean isIsPromotionActive() { return isPromotionActive.get(); }
    public BooleanProperty isPromotionActiveProperty() { return isPromotionActive; }
    public int getDiscountPercentage() { return discountPercentage.get(); }
    public String getMainImage() { return mainImage.get(); }
}
