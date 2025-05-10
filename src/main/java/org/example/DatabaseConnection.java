package org.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.io.InputStream;

// Classe pour gérer la connexion à la base de données
public class DatabaseConnection {
    // Méthode pour obtenir une connexion à la base de données
    public static Connection getConnection() throws SQLException {
        Properties props = new Properties();
        try (InputStream input = DatabaseConnection.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (input == null) {
                throw new SQLException("Fichier application.properties non trouvé dans les ressources.");
            }
            props.load(input); // Chargement des propriétés
        } catch (Exception e) {
            throw new SQLException("Échec du chargement des propriétés de la base de données : " + e.getMessage());
        }
        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // Chargement du pilote MySQL
        } catch (ClassNotFoundException e) {
            throw new SQLException("Pilote MySQL non trouvé : " + e.getMessage());
        }

        return DriverManager.getConnection(url, user, password); // Retour de la connexion
    }
}