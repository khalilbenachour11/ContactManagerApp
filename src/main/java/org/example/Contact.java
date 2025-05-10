package org.example;

// Classe représentant un contact
public class Contact {
    private int id;
    private String name;
    private String phone;
    private String email;

    // Constructeur avec tous les paramètres
    public Contact(int id, String name, String phone, String email) {
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }

    // Getter pour l'ID
    public int getId() { return id; }
    // Setter pour l'ID
    public void setId(int id) { this.id = id; }
    // Getter pour le nom
    public String getName() { return name; }
    // Setter pour le nom
    public void setName(String name) { this.name = name; }
    // Getter pour le téléphone
    public String getPhone() { return phone; }
    // Setter pour le téléphone
    public void setPhone(String phone) { this.phone = phone; }
    // Getter pour l'email
    public String getEmail() { return email; }
    // Setter pour l'email
    public void setEmail(String email) { this.email = email; }

    // Méthode pour afficher les informations du contact
    @Override
    public String toString() {
        return name + " - " + phone + " - " + email;
    }
}