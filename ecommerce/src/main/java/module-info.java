module ecommerce.calvao {
    // 1. Módulos do JavaFX (necessários para a interface)
    requires javafx.controls;
    requires javafx.fxml;
    
    // 2. Módulo HTTP (necessário para o AuthAPI e ProductAPI)
    requires java.net.http;
    
    // 3. Módulos do Jackson (necessários para JSON)
    requires com.fasterxml.jackson.databind;
    // O annotation é um módulo separado, mas é necessário explicitamente para o FXML + Jackson
    requires com.fasterxml.jackson.annotation;
    
    // --- PERMISSÕES DE ACESSO (REFLECTION) ---
    
    // 4. Permite que o Jackson acesse os modelos para preenchimento de dados (MUITO IMPORTANTE!)
    opens ecommerce.calvao.Model to com.fasterxml.jackson.databind;
    
    // 5. Permite que o FXML acesse seus Controllers
    opens ecommerce.calvao.Controller to javafx.fxml;
    
    // 6. Permite que o MainApplication e outros componentes sejam acessíveis
    opens ecommerce.calvao.App to javafx.fxml;

    // Exporta o pacote principal onde está o MainApplication
    exports ecommerce.calvao.App;
}