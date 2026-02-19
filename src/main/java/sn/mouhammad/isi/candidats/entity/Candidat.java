package sn.mouhammad.isi.candidats.entity;

import jakarta.persistence.*;

@Entity
public class Candidat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer niveau;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getNiveau() {
        return niveau;
    }

    public void setNiveau(Integer niveau) {
        this.niveau = niveau;
    }

    public Candidat() {
    }

    public Candidat(Integer id, String name, Integer niveau) {
        this.id = id;
        this.name = name;
        this.niveau = niveau;
    }
}
