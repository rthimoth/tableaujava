package fr.expenses.tableau;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AddExpenseController {
    
    private static final Logger logger = LoggerFactory.getLogger(AddExpenseController.class);

    @FXML
    private TextField periodeField;
    @FXML
    private TextField totalField;
    @FXML
    private TextField logementField;
    @FXML
    private TextField nourritureField;
    @FXML
    private TextField sortiesField;
    @FXML
    private TextField voitureTransportField;
    @FXML
    private TextField voyageField;
    @FXML
    private TextField impotsField;
    @FXML
    private TextField autresField;

    // Liste observable qui alimente le TableView de la fenêtre principale
    private ObservableList<Expense> expenseList;
    // Stage de la fenêtre modale afin de pouvoir la fermer après l'ajout
    private Stage stage;

    // Méthode appelée depuis le contrôleur principal pour transmettre la liste observable et la fenêtre modale
    public void setData(ObservableList<Expense> expenseList, Stage stage) {
        this.expenseList = expenseList;
        this.stage = stage;
        logger.debug("Données transmises au contrôleur d'ajout");
    }

    @FXML
    private void onSaveExpense() {
        logger.info("Tentative d'enregistrement d'une nouvelle dépense");
        try {
            // Récupération des valeurs saisies dans les champs
            String periode = periodeField.getText();
            double total = Double.parseDouble(totalField.getText());
            double logement = Double.parseDouble(logementField.getText());
            double nourriture = Double.parseDouble(nourritureField.getText());
            double sorties = Double.parseDouble(sortiesField.getText());
            double voitureTransport = Double.parseDouble(voitureTransportField.getText());
            double voyage = Double.parseDouble(voyageField.getText());
            double impots = Double.parseDouble(impotsField.getText());
            double autres = Double.parseDouble(autresField.getText());

            // Création d'une nouvelle dépense
            Expense newExpense = new Expense(periode, total, logement, nourriture, sorties,
                                             voitureTransport, voyage, impots, autres);
            
            logger.info("Nouvelle dépense créée pour la période: {}", periode);
            
            // Sauvegarder dans la base de données
            boolean saved = DatabaseManager.saveExpense(newExpense);
            
            if (saved) {
                // Ajout de la dépense dans la liste observable, ce qui mettra à jour le TableView
                expenseList.add(newExpense);
                
                // Ferme la fenêtre modale après l'enregistrement
                stage.close();
                logger.info("Dépense sauvegardée avec succès et fenêtre fermée");
            } else {
                logger.error("Échec de la sauvegarde en base de données");
                
                // Afficher une alerte pour informer l'utilisateur
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de sauvegarde");
                alert.setHeaderText(null);
                alert.setContentText("Impossible de sauvegarder la dépense. Veuillez réessayer.");
                alert.showAndWait();
            }
        } catch (NumberFormatException e) {
            logger.error("Erreur de saisie lors de l'ajout d'une dépense", e);
            
            // Afficher une alerte pour informer l'utilisateur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de saisie");
            alert.setHeaderText(null);
            alert.setContentText("Veuillez vérifier que toutes les valeurs numériques sont correctement saisies.");
            alert.showAndWait();
        }
    }
} 