package org.example;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;

// Classe pour gérer l'interface d'authentification
public class AuthenticationView {
    private Stage stage; // Fenêtre principale
    private ContactManager contactManager = new ContactManager(); // Gestionnaire des contacts
    private MainApp mainApp; // Référence à l'application principale

    // Constructeur pour initialiser la fenêtre et l'application
    public AuthenticationView(Stage stage, MainApp mainApp) {
        this.stage = stage;
        this.mainApp = mainApp;
        createAuthenticationScene();
    }

    // Création de la scène d'authentification avec les champs et boutons
    private void createAuthenticationScene() {
        System.out.println("Création de la scène d'authentification...");
        Label titleLabel = new Label("Authentification"); // Titre de la fenêtre
        TextField usernameField = new TextField();
        usernameField.setPromptText("Nom d'utilisateur"); // Indication pour le champ utilisateur
        TextField passwordField = new TextField();
        passwordField.setPromptText("Mot de passe"); // Indication pour le champ mot de passe
        Button loginButton = new Button("Login"); // Bouton de connexion
        Button signupButton = new Button("Signup"); // Bouton d'inscription

        // Action lors du clic sur le bouton Login
        loginButton.setOnAction(e -> {
            String username = usernameField.getText();
            String password = passwordField.getText();
            System.out.println("Tentative de connexion avec username: " + username + ", password: " + password);
            try {
                if (contactManager.authenticate(username, password)) {
                    System.out.println("Authentification réussie, ouverture de MainApp...");
                    showMainApp();
                } else {
                    System.out.println("Échec de l'authentification: Nom d'utilisateur ou mot de passe incorrect");
                    showAlert("Erreur", "Nom d'utilisateur ou mot de passe incorrect");
                }
            } catch (SQLException ex) {
                System.out.println("Erreur SQL lors de l'authentification: " + ex.getMessage());
                showAlert("Erreur", "Échec de l'authentification : " + ex.getMessage());
            }
        });

        // Action lors du clic sur le bouton Signup
        signupButton.setOnAction(e -> showSignupDialog());

        // Disposition verticale des éléments
        VBox layout = new VBox(10, titleLabel, usernameField, passwordField, loginButton, signupButton);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);
        // Configuration de la scène
        Scene scene = new Scene(layout, 300, 200);
        stage.setScene(scene);
        stage.setTitle("Authentification");
        stage.show();
        System.out.println("Scène d'authentification affichée.");
    }

    // Affichage d'une fenêtre d'inscription
    private void showSignupDialog() {
        Stage signupStage = new Stage();
        signupStage.initOwner(stage); // Lien avec la fenêtre principale
        signupStage.setTitle("Inscription");

        Label signupLabel = new Label("Créer un compte"); // Titre de l'inscription
        TextField newUsernameField = new TextField();
        newUsernameField.setPromptText("Nom d'utilisateur");
        TextField newPasswordField = new TextField();
        newPasswordField.setPromptText("Mot de passe");
        Button submitButton = new Button("S'inscrire");
        Button cancelButton = new Button("Annuler");

        // Action lors du clic sur S'inscrire
        submitButton.setOnAction(e -> {
            String username = newUsernameField.getText();
            String password = newPasswordField.getText();
            if (username.isEmpty() || password.isEmpty()) {
                showAlert("Erreur", "Veuillez remplir tous les champs");
                return;
            }
            try {
                contactManager.registerUser(username, password); // Enregistrement du nouvel utilisateur
                showAlert("Succès", "Inscription réussie ! Vous pouvez vous connecter maintenant.");
                signupStage.close();
            } catch (SQLException ex) {
                showAlert("Erreur", "Échec de l'inscription : " + ex.getMessage());
            }
        });

        // Action pour annuler l'inscription
        cancelButton.setOnAction(e -> signupStage.close());

        VBox signupLayout = new VBox(10, signupLabel, newUsernameField, newPasswordField, submitButton, cancelButton);
        signupLayout.setPadding(new Insets(10));
        signupLayout.setAlignment(Pos.CENTER);

        Scene signupScene = new Scene(signupLayout, 300, 200);
        signupStage.setScene(signupScene);
        signupStage.show();
    }

    // Affichage de l'interface principale après authentification
    private void showMainApp() {
        try {
            mainApp.showMainInterface(stage); // Affichage de l'interface principale sur le même Stage
            System.out.println("MainApp démarré, fenêtre d'authentification mise à jour.");
        } catch (Exception e) {
            System.out.println("Erreur lors du démarrage de MainApp: " + e.getMessage());
            showAlert("Erreur", "Échec du chargement de l'application principale : " + e.getMessage());
        }
    }

    // Affichage d'une alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}