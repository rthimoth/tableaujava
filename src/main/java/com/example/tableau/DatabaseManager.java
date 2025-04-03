package com.example.tableau;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseManager {
    private static final Logger logger = LoggerFactory.getLogger(DatabaseManager.class);
    
    // Le fichier de base de données sera créé dans le répertoire courant sous le nom "expenses.db"
    private static final String DB_URL = "jdbc:sqlite:expenses.db";

    public static Connection getConnection() throws SQLException {
        logger.debug("Tentative de connexion à la base de données");
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            logger.info("Connexion à la base de données établie avec succès");
            return conn;
        } catch (SQLException e) {
            logger.error("Erreur lors de la connexion à la base de données", e);
            throw e;
        }
    }

    public static void initializeDatabase() {
        logger.info("Démarrage de l'initialisation de la base de données");
        logger.info("Chemin de la base de données : {}", new File("expenses.db").getAbsolutePath());
        
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
        try (Connection conn = DatabaseManager.getConnection(); 
             Statement stmt = conn.createStatement()) {
            logger.debug("Exécution de la requête SQL de création de table");
            stmt.execute(sql);
            logger.info("Base de données initialisée avec succès");
        } catch (SQLException e) {
            logger.error("Erreur lors de l'initialisation de la base de données", e);
            e.printStackTrace();
        }
    }
} 