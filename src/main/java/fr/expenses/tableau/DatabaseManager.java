package fr.expenses.tableau;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    
    // Utiliser directement un chemin fixe pour la base de données
    private static final String DB_PATH = System.getProperty("user.home") + File.separator + ".expenses-manager" + File.separator + "expenses.db";
    private static final String DB_URL = "jdbc:sqlite:" + DB_PATH;

    public static Connection getConnection() throws SQLException {
        logger.debug("Connexion à la base de données: {}", DB_URL);
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        // Créer le répertoire contenant la BDD si nécessaire
        try {
            Path dbDirectory = Paths.get(DB_PATH).getParent();
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
} 