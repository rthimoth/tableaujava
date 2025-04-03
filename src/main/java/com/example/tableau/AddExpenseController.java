package com.example.tableau;

import javafx.fxml.FXML;
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
    }

    @FXML
    private void onSaveExpense() {
        logger.info("Tentative d'enregistrement d'une nouvelle dépense");
        try {
            // Validation des champs
            if (periodeField.getText().isEmpty()) {
                logger.warn("Tentative d'enregistrement avec un champ période vide");
                // Votre code de gestion d'erreur
                return;
            }

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
            // Ajout de la dépense dans la liste observable, ce qui mettra à jour le TableView
            expenseList.add(newExpense);

            logger.info("Nouvelle dépense enregistrée avec succès pour la période: {}", periode);
            
            // Ferme la fenêtre modale après l'enregistrement
            stage.close();
        } catch (NumberFormatException e) {
            logger.error("Erreur de saisie : " + e.getMessage());
            // Ici, vous pouvez ajouter une alerte afin d'informer l'utilisateur en cas d'erreur de saisie
        } catch (Exception e) {
            logger.error("Erreur lors de l'enregistrement de la dépense", e);
        }
    }

    @FXML
    protected void onCancelButtonClick() {
        logger.debug("Annulation de l'ajout de dépense");
        Stage stage = (Stage) periodeField.getScene().getWindow();
        stage.close();
    }
}