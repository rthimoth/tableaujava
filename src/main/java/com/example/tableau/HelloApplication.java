package com.example.tableau;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class HelloApplication extends Application {
    private static final Logger logger = LoggerFactory.getLogger(HelloApplication.class);

    @Override
    public void start(Stage stage) throws IOException {
        logger.info("Démarrage de l'application ExpensesManager");
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("hello-view.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 800, 600);
            stage.setTitle("Expenses Manager");
            stage.setScene(scene);
            stage.show();
            logger.info("Interface utilisateur chargée avec succès");
        } catch (IOException e) {
            logger.error("Erreur lors du chargement de l'interface utilisateur", e);
            throw e;
        }
    }

    public static void main(String[] args) {
        logger.info("Initialisation de l'application");
        DatabaseManager.initializeDatabase();
        launch();
    }
}