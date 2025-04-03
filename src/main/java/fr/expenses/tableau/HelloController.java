package fr.expenses.tableau;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import javafx.application.Platform;
import java.util.List;

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
        logger.info("Initialisation du contrôleur");
        
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
        
        logger.info("Tableau initialisé avec {} dépenses", expenseTable.getItems().size());
    }

    private ObservableList<Expense> getExpenses() {
        logger.debug("Chargement des dépenses depuis la base de données");
        
        // Récupérer les dépenses depuis la base de données
        List<Expense> expensesFromDb = DatabaseManager.getAllExpenses();
        
        // Si la base de données est vide (première utilisation), ajouter des exemples
        if (expensesFromDb.isEmpty()) {
            logger.info("Aucune dépense trouvée dans la base de données, ajout de données d'exemple");
            
            // Données d'exemple
            ObservableList<Expense> exampleData = FXCollections.observableArrayList(
                new Expense("août '23", 850.0, 350.0, 200.0, 150.0, 50.0, 50.0, 20.0, 30.0),
                new Expense("sept '23", 1950.0, 350.0, 250.0, 200.0, 100.0, 950.0, 50.0, 50.0),
                new Expense("oct '23", 900.0, 380.0, 200.0, 100.0, 70.0, 50.0, 50.0, 50.0),
                new Expense("nov '23", 800.0, 350.0, 150.0, 80.0, 50.0, 100.0, 50.0, 20.0),
                new Expense("déc '23", 1050.0, 350.0, 250.0, 200.0, 50.0, 100.0, 50.0, 50.0),
                new Expense("janv '24", 900.0, 350.0, 200.0, 150.0, 50.0, 50.0, 50.0, 50.0),
                new Expense("févr '24", 1030.0, 350.0, 200.0, 150.0, 100.0, 100.0, 80.0, 50.0)
            );
            
            // Sauvegarder les exemples dans la base de données
            for (Expense expense : exampleData) {
                DatabaseManager.saveExpense(expense);
            }
            
            // Recharger depuis la base de données pour avoir les IDs
            expensesFromDb = DatabaseManager.getAllExpenses();
        }
        
        return FXCollections.observableArrayList(expensesFromDb);
    }

    private void refreshTable() {
        logger.debug("Actualisation du tableau des dépenses");
        expenseTable.setItems(getExpenses());
    }

    @FXML
    private void onAjouterClicked(ActionEvent event) {
        logger.info("Action: Ajout d'une nouvelle dépense");
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
            logger.debug("Fenêtre d'ajout fermée");
        } catch (IOException e) {
            logger.error("Erreur lors de l'ouverture de la fenêtre d'ajout", e);
        }
    }
    
    @FXML
    private void onSupprimerClicked() {
        // Récupérer l'élément sélectionné dans le tableau
        Expense selectedExpense = expenseTable.getSelectionModel().getSelectedItem();
        
        // Vérifier si un élément est sélectionné
        if (selectedExpense != null) {
            logger.info("Suppression de la dépense pour la période: {}", selectedExpense.getPeriode());
            
            // Supprimer de la base de données
            boolean deleted = DatabaseManager.deleteExpense(selectedExpense);
            
            if (deleted) {
                // Supprimer de la liste observable (interface)
                expenseTable.getItems().remove(selectedExpense);
                logger.info("Dépense supprimée avec succès");
            } else {
                logger.error("Échec de la suppression en base de données");
                showErrorAlert("Erreur", "La suppression de la dépense a échoué");
            }
        } else {
            logger.warn("Tentative de suppression sans sélection");
            // Afficher une alerte si aucun élément n'est sélectionné
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Information");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez sélectionner une ligne à supprimer");
            alert.showAndWait();
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
            
            // Utilisation des données actuelles du tableau qui sont synchronisées avec la base de données
            statsController.initData(expenseTable.getItems());
            
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