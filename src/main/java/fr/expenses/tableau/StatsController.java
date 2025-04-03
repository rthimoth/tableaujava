package fr.expenses.tableau;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatsController {
    private static final Logger logger = LoggerFactory.getLogger(StatsController.class);

    @FXML
    private PieChart pieChartDepenses;
    
    @FXML
    private LineChart<String, Number> lineChartEvolution;
    
    @FXML
    private ComboBox<String> periodeComboBox;
    
    @FXML
    private Label pieChartTitleLabel;

    private ObservableList<Expense> expenses;
    private Map<String, Color> categoryColors;
    private boolean showAllData = false;

    public void initData(ObservableList<Expense> expenses) {
        logger.info("Initialisation des données pour les statistiques");
        this.expenses = expenses;
        
        // Initialiser les couleurs des catégories
        initCategoryColors();
        
        // Vérifier si nous avons des données
        if (expenses == null || expenses.isEmpty()) {
            logger.warn("Aucune dépense disponible pour initialiser les graphiques");
            return;
        }
        
        // Remplir le combobox avec les périodes disponibles
        initPeriodeCombobox();
        
        // Mettre à jour les graphiques
        updateCharts();
    }

    private void initCategoryColors() {
        categoryColors = new HashMap<>();
        categoryColors.put("Logement", Color.rgb(245, 131, 69)); // Orange
        categoryColors.put("Nourriture", Color.rgb(245, 180, 69)); // Jaune orangé
        categoryColors.put("Sorties", Color.rgb(114, 190, 89));  // Vert
        categoryColors.put("Voiture/Transport", Color.rgb(69, 194, 245)); // Bleu clair
        categoryColors.put("Voyage", Color.rgb(69, 105, 245)); // Bleu foncé
        categoryColors.put("Impôts", Color.rgb(155, 89, 182)); // Violet
        categoryColors.put("Autres", Color.rgb(231, 76, 120)); // Rose
    }
    
    private void initPeriodeCombobox() {
        List<String> periodes = new ArrayList<>();
        
        // N'ajouter que les périodes pour lesquelles nous avons des données
        if (expenses != null && !expenses.isEmpty()) {
            for (Expense expense : expenses) {
                periodes.add(expense.getPeriode());
            }
        }
        
        periodeComboBox.setItems(FXCollections.observableArrayList(periodes));
        if (!periodes.isEmpty()) {
            periodeComboBox.getSelectionModel().selectFirst();
        }
    }

    @FXML
    public void initialize() {
        logger.info("Initialisation du contrôleur des statistiques");
    }
    
    @FXML
    private void onPeriodeChanged() {
        String selectedPeriode = periodeComboBox.getValue();
        logger.info("Période sélectionnée: {}", selectedPeriode);
        
        if (selectedPeriode != null) {
            showAllData = false;
            updatePieChart();
        }
    }

    @FXML
    private void onToutButtonClick() {
        logger.info("Affichage de toutes les dépenses");
        showAllData = true;
        updatePieChart();
    }

    private void updateCharts() {
        logger.debug("Mise à jour des graphiques");
        
        // Mettre à jour le graphique en camembert
        updatePieChart();
        
        // Mettre à jour le graphique en lignes
        updateLineChart();
    }

    private void updatePieChart() {
        // Vérifier si des dépenses sont disponibles
        if (expenses == null || expenses.isEmpty()) {
            logger.warn("Aucune dépense disponible pour le graphique en camembert");
            return;
        }

        ObservableList<PieChart.Data> pieChartData;
        
        if (showAllData) {
            // Calculer les totaux pour toutes les périodes
            double totalLogement = 0;
            double totalNourriture = 0;
            double totalSorties = 0;
            double totalTransport = 0;
            double totalVoyage = 0;
            double totalImpots = 0;
            double totalAutres = 0;
            
            for (Expense expense : expenses) {
                totalLogement += expense.getLogement();
                totalNourriture += expense.getNourriture();
                totalSorties += expense.getSorties();
                totalTransport += expense.getVoitureTransport();
                totalVoyage += expense.getVoyage();
                totalImpots += expense.getImpots();
                totalAutres += expense.getAutres();
            }
            
            pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Logement", totalLogement),
                new PieChart.Data("Nourriture", totalNourriture),
                new PieChart.Data("Sorties", totalSorties),
                new PieChart.Data("Transport", totalTransport),
                new PieChart.Data("Voyage", totalVoyage),
                new PieChart.Data("Impôts", totalImpots),
                new PieChart.Data("Autres", totalAutres)
            );
            
            // Mise à jour du titre
            pieChartTitleLabel.setText("Toutes les périodes");
            
            logger.info("Graphique en camembert mis à jour avec toutes les périodes");
        } else {
            // Utiliser la dernière dépense si aucune période n'est sélectionnée
            Expense selectedExpense = expenses.get(expenses.size() - 1);
            
            // Si période sélectionnée, rechercher la dépense correspondante
            String selectedPeriode = periodeComboBox.getValue();
            if (selectedPeriode != null) {
                for (Expense expense : expenses) {
                    if (expense.getPeriode().equals(selectedPeriode)) {
                        selectedExpense = expense;
                        break;
                    }
                }
            }
            
            pieChartData = FXCollections.observableArrayList(
                new PieChart.Data("Logement", selectedExpense.getLogement()),
                new PieChart.Data("Nourriture", selectedExpense.getNourriture()),
                new PieChart.Data("Sorties", selectedExpense.getSorties()),
                new PieChart.Data("Transport", selectedExpense.getVoitureTransport()),
                new PieChart.Data("Voyage", selectedExpense.getVoyage()),
                new PieChart.Data("Impôts", selectedExpense.getImpots()),
                new PieChart.Data("Autres", selectedExpense.getAutres())
            );
            
            // Mise à jour du titre
            pieChartTitleLabel.setText("Période : " + (selectedPeriode != null ? selectedPeriode : selectedExpense.getPeriode()));
            
            logger.info("Graphique en camembert mis à jour pour la période: {}", selectedPeriode);
        }
        
        pieChartDepenses.setData(pieChartData);
    }

    private void updateLineChart() {
        if (expenses == null || expenses.isEmpty()) {
            logger.warn("Aucune dépense disponible pour le graphique en lignes");
            return;
        }

        lineChartEvolution.getData().clear();
        
        // Séries pour chaque catégorie
        XYChart.Series<String, Number> logementSeries = new XYChart.Series<>();
        logementSeries.setName("Logement");
        
        XYChart.Series<String, Number> nourrSeries = new XYChart.Series<>();
        nourrSeries.setName("Nourriture");
        
        XYChart.Series<String, Number> sortiesSeries = new XYChart.Series<>();
        sortiesSeries.setName("Sortie");
        
        XYChart.Series<String, Number> transportSeries = new XYChart.Series<>();
        transportSeries.setName("Transport");
        
        XYChart.Series<String, Number> voyageSeries = new XYChart.Series<>();
        voyageSeries.setName("Voyage");
        
        XYChart.Series<String, Number> impotsSeries = new XYChart.Series<>();
        impotsSeries.setName("Impôts");
        
        XYChart.Series<String, Number> autresSeries = new XYChart.Series<>();
        autresSeries.setName("Autres");
        
        // Remplir les séries
        for (Expense expense : expenses) {
            String periode = expense.getPeriode();
            
            logementSeries.getData().add(new XYChart.Data<>(periode, expense.getLogement()));
            nourrSeries.getData().add(new XYChart.Data<>(periode, expense.getNourriture()));
            sortiesSeries.getData().add(new XYChart.Data<>(periode, expense.getSorties()));
            transportSeries.getData().add(new XYChart.Data<>(periode, expense.getVoitureTransport()));
            voyageSeries.getData().add(new XYChart.Data<>(periode, expense.getVoyage()));
            impotsSeries.getData().add(new XYChart.Data<>(periode, expense.getImpots()));
            autresSeries.getData().add(new XYChart.Data<>(periode, expense.getAutres()));
        }
        
        // Ajouter les séries au graphique
        lineChartEvolution.getData().addAll(
            logementSeries, 
            nourrSeries, 
            sortiesSeries, 
            transportSeries, 
            voyageSeries, 
            impotsSeries, 
            autresSeries
        );
        
        logger.info("Graphique en lignes mis à jour");
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