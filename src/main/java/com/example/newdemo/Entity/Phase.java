package com.example.newdemo.Entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;

@Data
@Entity
@Getter
public class Phase {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private State state;

    @ManyToOne
    private City city;

    private String name;

    private String phaseId;

//    @OneToMany(mappedBy = "phrases", fetch = FetchType.EAGER)
//    private List<Property> properties = new ArrayList<>();

    public Phase() {}

    public Phase(Long id, State state, City city, String name, String phraseId) {
        this.id = id;
        this.state = state;
        this.city = city;
        this.name = name;
        this.phaseId = phraseId;
//        this.properties = properties;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setCity(City city) {
        this.city = city;
    }
    public void setPhaseId(String phaseId) {this.phaseId = phaseId;}

//    public void setProperties(List<Property> properties) {
//        this.properties = properties;
//    }
}
