package com.example.tableau;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.application.Platform;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelloController {
    private static final Logger logger = LoggerFactory.getLogger(HelloController.class);

    @FXML
    private TableView<Expense> expenseTable;
    @FXML
    private TableColumn<Expense, String> periodeColumn;
    @FXML
    private TableColumn<Expense, Double> totalColumn;
    @FXML
    private TableColumn<Expense, Double> logementColumn;
    @FXML
    private TableColumn<Expense, Double> nourritureColumn;
    @FXML
    private TableColumn<Expense, Double> sortiesColumn;
    @FXML
    private TableColumn<Expense, Double> voitureTransportColumn;
    @FXML
    private TableColumn<Expense, Double> voyageColumn;
    @FXML
    private TableColumn<Expense, Double> impotsColumn;
    @FXML
    private TableColumn<Expense, Double> autresColumn;

    @FXML
    public void initialize() {
        logger.info("Initialisation du contrôleur principal");
        periodeColumn.setCellValueFactory(new PropertyValueFactory<>("periode"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        logementColumn.setCellValueFactory(new PropertyValueFactory<>("logement"));
        nourritureColumn.setCellValueFactory(new PropertyValueFactory<>("nourriture"));
        sortiesColumn.setCellValueFactory(new PropertyValueFactory<>("sorties"));
        voitureTransportColumn.setCellValueFactory(new PropertyValueFactory<>("voitureTransport"));
        voyageColumn.setCellValueFactory(new PropertyValueFactory<>("voyage"));
        impotsColumn.setCellValueFactory(new PropertyValueFactory<>("impots"));
        autresColumn.setCellValueFactory(new PropertyValueFactory<>("autres"));

        expenseTable.setItems(getExpenses());
        DatabaseManager.initializeDatabase();
        logger.info("Contrôleur principal initialisé avec succès");
    }

    private ObservableList<Expense> getExpenses() {
        return FXCollections.observableArrayList(
            new Expense("2020-01", 1000.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0),
            new Expense("2020-02", 1000.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0)
        );
    }

    @FXML
    private void onAjouterClicked(ActionEvent event) {
        logger.info("Ouverture de la fenêtre d'ajout de dépense");
        try {
            // Chargement du fichier FXML de la fenêtre modale
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add-expense-view.fxml"));
            Parent root = loader.load();

            // Création de la fenêtre modale
            Stage modalStage = new Stage();
            modalStage.setTitle("Ajouter une dépense");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);

            // Définition de la fenêtre principale comme owner de la fenêtre modale
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            modalStage.initOwner(primaryStage);

            // Récupération du contrôleur de la modal et passage de la liste observable et du stage
            AddExpenseController controller = loader.getController();
            controller.setData(expenseTable.getItems(), modalStage);

            modalStage.showAndWait();
            logger.debug("Fenêtre d'ajout de dépense ouverte avec succès");
        } catch (IOException e) {
            logger.error("Erreur lors de l'ouverture de la fenêtre d'ajout de dépense", e);
        }
    }
    
    @FXML
    private void onSupprimerClicked() {
        // Récupérer l'élément sélectionné dans le tableau
        Expense selectedExpense = expenseTable.getSelectionModel().getSelectedItem();
        
        // Vérifier si un élément est sélectionné
        if (selectedExpense != null) {
            // Supprimer l'élément de la liste observable
            expenseTable.getItems().remove(selectedExpense);
        } else {
            // Afficher une alerte si aucun élément n'est sélectionné
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner une ligne à supprimer");
            alert.showAndWait();
        }
    }

    private void refreshTable() {
        logger.debug("Rafraîchissement de la table des dépenses");
        try {
            // Votre code existant
            logger.info("Table des dépenses mise à jour avec succès");
        } catch (Exception e) {
            logger.error("Erreur lors de la mise à jour de la table des dépenses", e);
        }
    }

    @FXML
    private void onTableauMenuClick() {
        logger.info("Navigation vers la page Tableau");
        // Déjà sur cette page, ne rien faire
    }

    @FXML
    private void onStatsMenuClick() {
        logger.info("Navigation vers la page Statistiques");
        try {
            // Charger la vue des statistiques
            FXMLLoader loader = new FXMLLoader(getClass().getResource("stats-view.fxml"));
            Parent statsView = loader.load();
            
            // Récupérer la scène actuelle
            Scene currentScene = expenseTable.getScene();
            
            // Remplacer le contenu de la scène
            currentScene.setRoot(statsView);
            
            // Obtenir le controller des statistiques et initialiser les données
            StatsController statsController = loader.getController();
            statsController.initData(getExpenses());
            
            logger.info("Page de statistiques chargée avec succès");
        } catch (IOException e) {
            logger.error("Erreur lors du chargement de la page de statistiques", e);
            showErrorAlert("Erreur de navigation", "Impossible de charger la page des statistiques");
        }
    }

    @FXML
    private void onAboutMenuClick() {
        logger.info("Ouverture de la boîte de dialogue À propos");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("À propos");
        alert.setHeaderText("Expenses Manager");
        alert.setContentText("Application de gestion des dépenses\nVersion 1.0\n© 2023");
        alert.showAndWait();
    }

    @FXML
    private void onQuitMenuClick() {
        logger.info("Fermeture de l'application");
        Platform.exit();
    }

    private void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}