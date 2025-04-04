package fr.expenses.tableau;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddIncomeController {
    
    private static final Logger logger = LoggerFactory.getLogger(AddIncomeController.class);

    @FXML
    private TextField periodeField;
    @FXML
    private TextField totalField;
    @FXML
    private TextField salaireField;
    @FXML
    private TextField aidesField;
    @FXML
    private TextField autoEntrepriseField;
    @FXML
    private TextField revenusPassifsField;
    @FXML
    private TextField autresField;

    // Liste observable qui alimente le TableView de la fenêtre principale
    private ObservableList<Income> incomeList;
    // Stage de la fenêtre modale afin de pouvoir la fermer après l'ajout
    private Stage stage;

    // Méthode appelée depuis le contrôleur principal pour transmettre la liste observable et la fenêtre modale
    public void setData(ObservableList<Income> incomeList, Stage stage) {
        this.incomeList = incomeList;
        this.stage = stage;
        logger.debug("Données transmises au contrôleur d'ajout de revenus");
    }

    @FXML
    private void onSaveIncome() {
        logger.info("Tentative d'enregistrement d'un nouveau revenu");
        try {
            // Récupération des valeurs saisies dans les champs
            String periode = periodeField.getText();
            double salaire = getDoubleValue(salaireField, 0);
            double aides = getDoubleValue(aidesField, 0);
            double autoEntreprise = getDoubleValue(autoEntrepriseField, 0);
            double revenusPassifs = getDoubleValue(revenusPassifsField, 0);
            double autres = getDoubleValue(autresField, 0);
            
            // Calcul automatique du total si non spécifié
            double total;
            if (totalField.getText().isEmpty()) {
                total = salaire + aides + autoEntreprise + revenusPassifs + autres;
                logger.debug("Total calculé automatiquement: {}", total);
            } else {
                total = Double.parseDouble(totalField.getText());
            }

            // Création d'un nouveau revenu
            Income newIncome = new Income(periode, total, salaire, aides, autoEntreprise, revenusPassifs, autres);
            
            logger.info("Nouveau revenu créé pour la période: {}", periode);
            
            // Sauvegarder dans la base de données
            boolean saved = DatabaseManager.saveIncome(newIncome);
            
            if (saved) {
                // Ajout du revenu dans la liste observable, ce qui mettra à jour le TableView
                incomeList.add(newIncome);
                
                // Ferme la fenêtre modale après l'enregistrement
                stage.close();
                logger.info("Revenu sauvegardé avec succès et fenêtre fermée");
            } else {
                logger.error("Échec de la sauvegarde en base de données");
                
                // Afficher une alerte pour informer l'utilisateur
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de sauvegarde");
                alert.setHeaderText(null);
                alert.setContentText("Impossible de sauvegarder le revenu. Veuillez réessayer.");
                alert.showAndWait();
            }
        } catch (NumberFormatException e) {
            logger.error("Erreur de saisie lors de l'ajout d'un revenu", e);
            
            // Afficher une alerte pour informer l'utilisateur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez vérifier que toutes les valeurs numériques sont correctement saisies.");
            alert.showAndWait();
        }
    }
    
    @FXML
    private void onCancelButtonClick() {
        logger.debug("Annulation de l'ajout d'un revenu");
        stage.close();
    }
    
    // Méthode utilitaire pour convertir un TextField en double, avec valeur par défaut si vide
    private double getDoubleValue(TextField field, double defaultValue) {
        String text = field.getText();
        if (text == null || text.isEmpty()) {
            return defaultValue;
        }
        return Double.parseDouble(text);
    }
} 