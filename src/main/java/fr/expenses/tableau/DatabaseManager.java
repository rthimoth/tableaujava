package fr.expenses.tableau;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    
    private static String getDbPath() {
        String appDir;
        String os = System.getProperty("os.name").toLowerCase();
        
        if (os.contains("win")) {
            // Windows
            appDir = System.getProperty("user.home") + File.separator + ".expenses-manager";
        } else if (os.contains("mac")) {
            // macOS
            appDir = System.getProperty("user.home") + File.separator + ".expenses-manager";
        } else {
            // Linux et autres
            appDir = System.getProperty("user.home") + File.separator + ".expenses-manager";
        }
        
        return appDir + File.separator + "expenses.db";
    }

    private static final String DB_URL = "jdbc:sqlite:" + getDbPath();

    public static Connection getConnection() throws SQLException {
        logger.debug("Connexion à la base de données: {}", DB_URL);
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        // Créer le répertoire contenant la BDD si nécessaire
        try {
            Path dbDirectory = Paths.get(getDbPath()).getParent();
            if (!Files.exists(dbDirectory)) {
                Files.createDirectories(dbDirectory);
                logger.info("Répertoire de la base de données créé: {}", dbDirectory);
            }
            
            // Créer aussi le répertoire pour les logs
            Path logDir = Paths.get(dbDirectory.toString(), "logs");
            if (!Files.exists(logDir)) {
                Files.createDirectories(logDir);
                logger.info("Répertoire de logs créé: {}", logDir);
            }
        } catch (Exception e) {
            logger.error("Erreur lors de la création des répertoires: {}", e.getMessage());
        }
        
        logger.info("Initialisation de la base de données: {}", DB_URL);
        
        String sql = "CREATE TABLE IF NOT EXISTS expenses ("
                + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "periode TEXT NOT NULL, "
                + "total REAL, "
                + "logement REAL, "
                + "nourriture REAL, "
                + "sorties REAL, "
                + "voitureTransport REAL, "
                + "voyage REAL, "
                + "impots REAL, "
                + "autres REAL"
                + ");";
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            logger.info("Structure de la base de données créée/vérifiée avec succès");
        } catch (SQLException e) {
            logger.error("Erreur lors de l'initialisation de la base de données", e);
        }
    }
    
    public static List<Expense> getAllExpenses() {
        logger.info("Chargement de toutes les dépenses depuis la base de données");
        List<Expense> expenses = new ArrayList<>();
        
        String sql = "SELECT * FROM expenses ORDER BY periode";
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Expense expense = new Expense(
                    rs.getString("periode"),
                    rs.getDouble("total"),
                    rs.getDouble("logement"),
                    rs.getDouble("nourriture"),
                    rs.getDouble("sorties"),
                    rs.getDouble("voitureTransport"),
                    rs.getDouble("voyage"),
                    rs.getDouble("impots"),
                    rs.getDouble("autres")
                );
                expense.setId(rs.getInt("id"));
                expenses.add(expense);
            }
            
            logger.info("Chargement terminé: {} dépenses trouvées", expenses.size());
        } catch (SQLException e) {
            logger.error("Erreur lors du chargement des dépenses", e);
        }
        
        return expenses;
    }
    
    public static boolean saveExpense(Expense expense) {
        logger.info("Sauvegarde d'une dépense pour la période: {}", expense.getPeriode());
        
        String sql = "INSERT INTO expenses (periode, total, logement, nourriture, sorties, "
                + "voitureTransport, voyage, impots, autres) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, expense.getPeriode());
            pstmt.setDouble(2, expense.getTotal());
            pstmt.setDouble(3, expense.getLogement());
            pstmt.setDouble(4, expense.getNourriture());
            pstmt.setDouble(5, expense.getSorties());
            pstmt.setDouble(6, expense.getVoitureTransport());
            pstmt.setDouble(7, expense.getVoyage());
            pstmt.setDouble(8, expense.getImpots());
            pstmt.setDouble(9, expense.getAutres());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Dépense sauvegardée avec succès");
                return true;
            } else {
                logger.warn("Aucune ligne ajoutée lors de la sauvegarde");
                return false;
            }
            
        } catch (SQLException e) {
            logger.error("Erreur lors de la sauvegarde de la dépense", e);
            return false;
        }
    }
    
    public static boolean deleteExpense(Expense expense) {
        logger.info("Suppression d'une dépense pour la période: {}", expense.getPeriode());
        
        // Si l'ID est défini, on supprime par ID
        if (expense.getId() > 0) {
            return deleteExpenseById(expense.getId());
        }
        
        // Sinon, on supprime par période (moins fiable si plusieurs dépenses pour la même période)
        String sql = "DELETE FROM expenses WHERE periode = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, expense.getPeriode());
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Dépense supprimée avec succès");
                return true;
            } else {
                logger.warn("Aucune ligne supprimée");
                return false;
            }
            
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression de la dépense", e);
            return false;
        }
    }
    
    private static boolean deleteExpenseById(int id) {
        logger.info("Suppression d'une dépense avec l'ID: {}", id);
        
        String sql = "DELETE FROM expenses WHERE id = ?";
        
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            
            int affectedRows = pstmt.executeUpdate();
            
            if (affectedRows > 0) {
                logger.info("Dépense supprimée avec succès");
                return true;
            } else {
                logger.warn("Aucune ligne supprimée");
                return false;
            }
            
        } catch (SQLException e) {
            logger.error("Erreur lors de la suppression de la dépense", e);
            return false;
        }
    }
} 