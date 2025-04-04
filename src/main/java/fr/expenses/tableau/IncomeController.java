package fr.expenses.tableau;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class IncomeController {
    private static final Logger logger = LoggerFactory.getLogger(IncomeController.class);

    @FXML
    private TableView<Income> incomeTable;
    @FXML
    private TableColumn<Income, String> periodeColumn;
    @FXML
    private TableColumn<Income, Double> totalColumn;
    @FXML
    private TableColumn<Income, Double> salaireColumn;
    @FXML
    private TableColumn<Income, Double> aidesColumn;
    @FXML
    private TableColumn<Income, Double> autoEntrepriseColumn;
    @FXML
    private TableColumn<Income, Double> revenusPassifsColumn;
    @FXML
    private TableColumn<Income, Double> autresColumn;

    @FXML
    public void initialize() {
        logger.info("Initialisation du contrôleur de revenus");
        
        periodeColumn.setCellValueFactory(new PropertyValueFactory<>("periode"));
        totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
        salaireColumn.setCellValueFactory(new PropertyValueFactory<>("salaire"));
        aidesColumn.setCellValueFactory(new PropertyValueFactory<>("aides"));
        autoEntrepriseColumn.setCellValueFactory(new PropertyValueFactory<>("autoEntreprise"));
        revenusPassifsColumn.setCellValueFactory(new PropertyValueFactory<>("revenusPassifs"));
        autresColumn.setCellValueFactory(new PropertyValueFactory<>("autres"));

        incomeTable.setItems(getIncomes());
        
        logger.info("Tableau des revenus initialisé avec {} entrées", incomeTable.getItems().size());
    }

    private ObservableList<Income> getIncomes() {
        logger.debug("Chargement des revenus depuis la base de données");
        
        // Récupérer les revenus depuis la base de données
        List<Income> incomesFromDb = DatabaseManager.getAllIncomes();
        
        // Si la base de données est vide (première utilisation), ajouter des exemples
        if (incomesFromDb.isEmpty()) {
            logger.info("Aucun revenu trouvé dans la base de données, ajout de données d'exemple");
            
            // Données d'exemple
            ObservableList<Income> exampleData = FXCollections.observableArrayList(
                new Income("févr '24", 5000.0, 3000.0, 300.0, 500.0, 1000.0, 200.0),
                new Income("janv '24", 4300.0, 3000.0, 300.0, 500.0, 200.0, 300.0),
                new Income("déc '23", 5700.0, 3000.0, 300.0, 2150.0, 200.0, 50.0),
                new Income("nov '23", 4670.0, 3200.0, 250.0, 900.0, 200.0, 120.0),
                new Income("oct '23", 5790.0, 3600.0, 250.0, 1650.0, 200.0, 90.0),
                new Income("sept '23", 5495.0, 3200.0, 250.0, 1800.0, 200.0, 45.0),
                new Income("août '23", 5080.0, 3200.0, 250.0, 1200.0, 200.0, 230.0)
            );
            
            // Sauvegarder les exemples dans la base de données
            for (Income income : exampleData) {
                DatabaseManager.saveIncome(income);
            }
            
            // Recharger depuis la base de données pour avoir les IDs
            incomesFromDb = DatabaseManager.getAllIncomes();
        }
        
        return FXCollections.observableArrayList(incomesFromDb);
    }

    @FXML
    private void onAjouterClicked(ActionEvent event) {
        logger.info("Action: Ajout d'un nouveau revenu");
        try {
            // Chargement du fichier FXML de la fenêtre modale
            FXMLLoader loader = new FXMLLoader(getClass().getResource("add-income-view.fxml"));
            Parent root = loader.load();

            // Création de la fenêtre modale
            Stage modalStage = new Stage();
            modalStage.setTitle("Ajouter un revenu");
            modalStage.setScene(new Scene(root));
            modalStage.initModality(Modality.APPLICATION_MODAL);

            // Définition de la fenêtre principale comme owner de la fenêtre modale
            Stage primaryStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            modalStage.initOwner(primaryStage);

            // Récupération du contrôleur de la modal et passage de la liste observable et du stage
            AddIncomeController controller = loader.getController();
            controller.setData(incomeTable.getItems(), modalStage);

            modalStage.showAndWait();
            
            logger.debug("Fenêtre d'ajout de revenu fermée");
        } catch (IOException e) {
            logger.error("Erreur lors de l'ouverture de la fenêtre d'ajout de revenu", e);
        }
    }
    
    @FXML
    private void onSupprimerClicked() {
        // Récupérer l'élément sélectionné dans le tableau
        Income selectedIncome = incomeTable.getSelectionModel().getSelectedItem();
        
        // Vérifier si un élément est sélectionné
        if (selectedIncome != null) {
            logger.info("Suppression du revenu pour la période: {}", selectedIncome.getPeriode());
            
            // Supprimer de la base de données
            boolean deleted = DatabaseManager.deleteIncome(selectedIncome);
            
            if (deleted) {
                // Supprimer de la liste observable (interface)
                incomeTable.getItems().remove(selectedIncome);
                
                logger.info("Revenu supprimé avec succès");
            } else {
                logger.error("Échec de la suppression en base de données");
                showErrorAlert("Erreur", "La suppression du revenu a échoué");
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
    
    private void refreshTable() {
        logger.debug("Actualisation du tableau des revenus");
        incomeTable.setItems(FXCollections.observableArrayList(DatabaseManager.getAllIncomes()));
    }

    // Méthodes de navigation
    @FXML
    private void onTableauDepensesMenuClick() {
        logger.info("Navigation vers la page du tableau des dépenses");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent tableauView = loader.load();
            
            Scene currentScene = incomeTable.getScene();
            currentScene.setRoot(tableauView);
            
            logger.info("Page du tableau des dépenses chargée avec succès");
        } catch (IOException e) {
            logger.error("Erreur lors du chargement de la page du tableau des dépenses", e);
            showErrorAlert("Erreur de navigation", "Impossible de charger la page du tableau des dépenses");
        }
    }

    @FXML
    private void onStatsMenuClick() {
        logger.info("Navigation vers la page des statistiques");
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("stats-view.fxml"));
            Parent statsView = loader.load();
            
            Scene currentScene = incomeTable.getScene();
            currentScene.setRoot(statsView);
            
            // Obtenir le contrôleur et initialiser avec les dépenses
            StatsController statsController = loader.getController();
            statsController.initData(FXCollections.observableArrayList(DatabaseManager.getAllExpenses()));
            
            logger.info("Page des statistiques chargée avec succès");
        } catch (IOException e) {
            logger.error("Erreur lors du chargement de la page des statistiques", e);
            showErrorAlert("Erreur de navigation", "Impossible de charger la page des statistiques");
        }
    }

    @FXML
    private void onRevenusMenuClick() {
        logger.info("Navigation vers la page des revenus");
        // Déjà sur cette page, ne rien faire
    }

    @FXML
    private void onAboutMenuClick() {
        logger.info("Ouverture de la boîte de dialogue À propos");
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("À propos");
        alert.setHeaderText("Expenses Manager");
        alert.setContentText("Application de gestion des dépenses et revenus\nVersion 1.1\n© 2023-2024");
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