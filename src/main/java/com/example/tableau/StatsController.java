package com.example.tableau;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class StatsController {
    private static final Logger logger = LoggerFactory.getLogger(StatsController.class);

    @FXML
    private PieChart pieChartDepenses;
    
    @FXML
    private BarChart<String, Number> barChartEvolution;

    private ObservableList<Expense> expenses;

    public void initData(ObservableList<Expense> expenses) {
        logger.info("Initialisation des données pour les statistiques");
        this.expenses = expenses;
        updateCharts();
    }

    @FXML
    public void initialize() {
        logger.info("Initialisation du contrôleur des statistiques");
    }

    private void updateCharts() {
        logger.debug("Mise à jour des graphiques");
        
        // Mettre à jour le graphique en camembert
        updatePieChart();
        
        // Mettre à jour le graphique en barres
        updateBarChart();
    }

    private void updatePieChart() {
        // Calculer la somme de toutes les catégories pour la dernière période
        if (expenses == null || expenses.isEmpty()) {
            logger.warn("Aucune dépense disponible pour le graphique en camembert");
            return;
        }

        Expense latestExpense = expenses.get(expenses.size() - 1);
        
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList(
            new PieChart.Data("Logement", latestExpense.getLogement()),
            new PieChart.Data("Nourriture", latestExpense.getNourriture()),
            new PieChart.Data("Sorties", latestExpense.getSorties()),
            new PieChart.Data("Voiture/Transport", latestExpense.getVoitureTransport()),
            new PieChart.Data("Voyage", latestExpense.getVoyage()),
            new PieChart.Data("Impôts", latestExpense.getImpots()),
            new PieChart.Data("Autres", latestExpense.getAutres())
        );
        
        pieChartDepenses.setData(pieChartData);
        pieChartDepenses.setTitle("Répartition des dépenses - " + latestExpense.getPeriode());
        
        logger.info("Graphique en camembert mis à jour");
    }

    private void updateBarChart() {
        if (expenses == null || expenses.isEmpty()) {
            logger.warn("Aucune dépense disponible pour le graphique en barres");
            return;
        }

        // Série pour les totaux
        XYChart.Series<String, Number> totalSeries = new XYChart.Series<>();
        totalSeries.setName("Total");
        
        for (Expense expense : expenses) {
            totalSeries.getData().add(new XYChart.Data<>(expense.getPeriode(), expense.getTotal()));
        }
        
        barChartEvolution.getData().clear();
        barChartEvolution.getData().add(totalSeries);
        
        logger.info("Graphique en barres mis à jour");
    }
    
    @FXML
    private void onTableauMenuClick() {
        logger.info("Navigation vers la page Tableau");
        try {
            // Charger la vue du tableau
            FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
            Parent tableauView = loader.load();
            
            // Récupérer la scène actuelle
            Scene currentScene = pieChartDepenses.getScene();
            
            // Remplacer le contenu de la scène
            currentScene.setRoot(tableauView);
            
            logger.info("Page du tableau chargée avec succès");
        } catch (IOException e) {
            logger.error("Erreur lors du chargement de la page du tableau", e);
            showErrorAlert("Erreur de navigation", "Impossible de charger la page du tableau");
        }
    }

    @FXML
    private void onStatsMenuClick() {
        logger.info("Navigation vers la page Statistiques");
        // Déjà sur cette page, ne rien faire
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