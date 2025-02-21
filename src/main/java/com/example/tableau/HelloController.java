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
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

public class HelloController {

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
    }

    private ObservableList<Expense> getExpenses() {
        return FXCollections.observableArrayList(
            new Expense("2020-01", 1000.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0),
            new Expense("2020-02", 1000.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0),
            new Expense("2020-03", 1000.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0, 100.0)
        );
    }

    @FXML
    private void onAjouterClicked(ActionEvent event) {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}