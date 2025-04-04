package fr.expenses.tableau;

import java.util.Objects;

public class Income {
    private int id; // Identifiant unique en base de données
    private String periode;
    private double total;
    private double salaire;
    private double aides;
    private double autoEntreprise;
    private double revenusPassifs;
    private double autres;

    // Constructeur
    public Income(String periode, double total, double salaire, double aides, 
                 double autoEntreprise, double revenusPassifs, double autres) {
        this.id = 0; // Par défaut, l'ID est 0 (non défini)
        this.periode = periode;
        this.total = total;
        this.salaire = salaire;
        this.aides = aides;
        this.autoEntreprise = autoEntreprise;
        this.revenusPassifs = revenusPassifs;
        this.autres = autres;
    }

    // Getters et Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPeriode() {
        return periode;
    }

    public void setPeriode(String periode) {
        this.periode = periode;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getSalaire() {
        return salaire;
    }

    public void setSalaire(double salaire) {
        this.salaire = salaire;
    }

    public double getAides() {
        return aides;
    }

    public void setAides(double aides) {
        this.aides = aides;
    }

    public double getAutoEntreprise() {
        return autoEntreprise;
    }

    public void setAutoEntreprise(double autoEntreprise) {
        this.autoEntreprise = autoEntreprise;
    }

    public double getRevenusPassifs() {
        return revenusPassifs;
    }

    public void setRevenusPassifs(double revenusPassifs) {
        this.revenusPassifs = revenusPassifs;
    }

    public double getAutres() {
        return autres;
    }

    public void setAutres(double autres) {
        this.autres = autres;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Income income = (Income) o;
        return Objects.equals(periode, income.periode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(periode);
    }

    @Override
    public String toString() {
        return "Income{" +
                "periode='" + periode + '\'' +
                ", total=" + total +
                '}';
    }
} 