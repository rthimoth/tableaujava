package fr.expenses.tableau;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class HelloApplication extends Application {
    
    private static final Logger logger = LoggerFactory.getLogger(HelloApplication.class);
    
    @Override
    public void start(Stage stage) throws IOException {
        logger.info("Démarrage de l'application ExpensesManager");
        
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("ExpensesManager - Gestion des dépenses");
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("icon.png")));
        stage.show();
        
        logger.info("Application démarrée avec succès");
    }

    public static void main(String[] args) {
        logger.info("Lancement de l'application");
        launch();
    }
} 