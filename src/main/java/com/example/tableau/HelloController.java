package com.example.tableau;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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
        expenseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

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
}