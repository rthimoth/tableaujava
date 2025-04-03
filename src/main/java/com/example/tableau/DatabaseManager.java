package com.example.tableau;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.io.File;

public class DatabaseManager {
    // Le fichier de base de données sera créé dans le répertoire courant sous le nom "expenses.db"
    private static final String DB_URL = "jdbc:sqlite:expenses.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        // Affichez le chemin absolu pour vérifier où se trouve votre base
        System.out.println("Chemin de la base de données : " + new File("expenses.db").getAbsolutePath());
        
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
        try (Connection conn = DatabaseManager.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 