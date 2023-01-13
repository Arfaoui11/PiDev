package com.example.spacecourses.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.util.Date;
import java.util.Set;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Table( name = "Formation")
public class Formation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer idFormation;
    private String title;
    @Enumerated(EnumType.STRING)
    private Niveau niveau;
    @Temporal (TemporalType.TIMESTAMP)
    private Date start;
    @Temporal (TemporalType.TIMESTAMP)
    private Date end;
    private Integer nbrHeures;
    @Enumerated(EnumType.STRING)
    private Domain domain;

    private Integer nbrMaxParticipant;
    private Integer frais;

    private Integer likes;
    private Integer dislikes;

    @ManyToOne
    @JsonIgnore
    private User formateur;

    @ManyToMany
    @JsonIgnore
    private Set<User> apprenant ;

    @OneToMany(mappedBy = "formation" ,cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
    @JsonIgnore
    private Set<Quiz> quizzes;


    @OneToMany(mappedBy = "formation",cascade = {CascadeType.PERSIST,CascadeType.REMOVE})
    @JsonIgnore
    private Set<DatabaseFile> databaseFiles;

    @Override
    public String toString() {
        return "Formation{" +
                "idFormation=" + idFormation +
                ", title='" + title + '\'' +
                ", niveau=" + niveau +
                ", start=" + start +
                ", end=" + end +
                ", nbrHeures=" + nbrHeures +
                ", domain=" + domain +
                ", nbrMaxParticipant=" + nbrMaxParticipant +
                ", frais=" + frais +
                '}';
    }
}
