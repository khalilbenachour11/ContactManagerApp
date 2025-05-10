package org.example;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.sql.SQLException;
import java.util.List;

// Classe principale de l'application JavaFX
public class MainApp extends Application {
    private ContactManager contactManager = new ContactManager(); // Gestionnaire des contacts
    private ListView<Contact> contactListView = new ListView<>(); // Liste des contacts

    // Point d'entrée de l'application
    @Override
    public void start(Stage primaryStage) {
        System.out.println("Démarrage de l'application...");
        new AuthenticationView(primaryStage, this); // Lancement de l'interface d'authentification
        System.out.println("AuthenticationView créé.");
    }

    // Rafraîchissement de la liste des contacts
    public void refreshContactList(String searchTerm) throws SQLException {
        contactListView.getItems().clear(); // Vidage de la liste actuelle
        List<Contact> contacts = searchTerm.isEmpty()
                ? contactManager.getAllContacts() // Récupération de tous les contacts
                : contactManager.searchContacts(searchTerm); // Recherche par nom
        contactListView.getItems().addAll(contacts);
    }

    // Affichage d'une alerte
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode principale pour lancer l'application
    public static void main(String[] args) {
        launch(args);
    }

    // Affichage de l'interface principale de gestion des contacts
    public void showMainInterface(Stage stage) {
        System.out.println("Création de l'interface principale...");
        TextField nameField = new TextField();
        nameField.setPromptText("Nom"); // Champ pour le nom
        TextField phoneField = new TextField();
        phoneField.setPromptText("Téléphone"); // Champ pour le téléphone
        TextField emailField = new TextField();
        emailField.setPromptText("Email"); // Champ pour l'email

        TextField searchField = new TextField();
        searchField.setPromptText("Rechercher par nom"); // Champ de recherche
        searchField.textProperty().addListener((obs, oldValue, newValue) -> {
            try {
                refreshContactList(newValue); // Mise à jour de la liste lors de la saisie
            } catch (SQLException e) {
                showAlert("Erreur", "Échec de la recherche : " + e.getMessage());
            }
        });

        Button addButton = new Button("Ajouter"); // Bouton pour ajouter un contact
        addButton.setOnAction(e -> {
            try {
                String name = nameField.getText();
                String phone = phoneField.getText();
                String email = emailField.getText();
                if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                    showAlert("Erreur", "Veuillez remplir tous les champs");
                    return;
                }
                if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                    showAlert("Erreur", "L'email n'est pas valide");
                    return;
                }
                if (!phone.matches("\\d{7,8}")) {
                    showAlert("Erreur", "Le numéro de téléphone doit contenir entre 8 chiffres");
                    return;
                }
                contactManager.addContact(name, phone, email); // Ajout du contact
                refreshContactList("");
                nameField.clear();
                phoneField.clear();
                emailField.clear();
            } catch (SQLException ex) {
                showAlert("Erreur", "Échec de l'ajout : " + ex.getMessage());
            }
        });

        Button updateButton = new Button("Mettre à jour"); // Bouton pour mettre à jour
        updateButton.setOnAction(e -> {
            Contact selectedContact = contactListView.getSelectionModel().getSelectedItem();
            if (selectedContact == null) {
                showAlert("Erreur", "Veuillez sélectionner un contact");
                return;
            }
            String name = nameField.getText();
            String phone = phoneField.getText();
            String email = emailField.getText();
            if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
                showAlert("Erreur", "Veuillez remplir tous les champs");
                return;
            }
            if (!email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
                showAlert("Erreur", "L'email n'est pas valide");
                return;
            }
            if (!phone.matches("\\d{7,8}")) {
                showAlert("Erreur", "Le numéro de téléphone doit contenir 8 chiffres");
                return;
            }
            try {
                contactManager.updateContact(
                        selectedContact.getId(),
                        name,
                        phone,
                        email
                ); // Mise à jour du contact
                refreshContactList("");
                nameField.clear();
                phoneField.clear();
                emailField.clear();
            } catch (SQLException ex) {
                showAlert("Erreur", "Échec de la mise à jour : " + ex.getMessage());
            }
        });

        Button deleteButton = new Button("Supprimer"); // Bouton pour supprimer
        deleteButton.setOnAction(e -> {
            Contact selectedContact = contactListView.getSelectionModel().getSelectedItem();
            if (selectedContact == null) {
                showAlert("Erreur", "Veuillez sélectionner un contact");
                return;
            }
            try {
                contactManager.deleteContact(selectedContact.getId()); // Suppression du contact
                refreshContactList("");
            } catch (SQLException ex) {
                showAlert("Erreur", "Échec de la suppression : " + ex.getMessage());
            }
        });

        // Sélection d'un contact pour remplir les champs
        contactListView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                phoneField.setText(newSelection.getPhone());
                emailField.setText(newSelection.getEmail());
            }
        });

        // Personnalisation de l'affichage des contacts dans la liste
        contactListView.setCellFactory(param -> new ListCell<Contact>() {
            @Override
            protected void updateItem(Contact contact, boolean empty) {
                super.updateItem(contact, empty);
                if (empty || contact == null) {
                    setText(null);
                } else {
                    setText(String.format("%s | %s | %s", contact.getName(), contact.getPhone(), contact.getEmail()));
                }
            }
        });

        // Disposition horizontale pour les champs d'entrée
        HBox inputBox = new HBox(10, nameField, phoneField, emailField);
        HBox buttonBox = new HBox(10, addButton, updateButton, deleteButton);
        VBox layout = new VBox(10, searchField, inputBox, buttonBox, contactListView);
        layout.setPadding(new Insets(10));
        layout.setAlignment(Pos.CENTER);

        Scene scene = new Scene(layout, 600, 400);
        stage.setScene(scene);
        stage.setTitle("Gestionnaire de contacts");
        stage.show();
        System.out.println("Interface principale affichée.");

        // Chargement initial des contacts
        try {
            refreshContactList("");
            System.out.println("Liste des contacts chargée.");
        } catch (SQLException e) {
            System.out.println("Erreur lors du chargement des contacts: " + e.getMessage());
            showAlert("Erreur", "Échec du chargement des contacts : " + e.getMessage());
        }
    }
}