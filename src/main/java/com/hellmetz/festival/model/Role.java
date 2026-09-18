package com.hellmetz.festival.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "role")
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_role")
    private Long idRole;

    @Column(name = "code_role")
    private String codeRole;

    @Column(name = "libelle")
    private String libelle;

    @ManyToMany
    @JoinTable(name = "role_permission",
            joinColumns = @JoinColumn(name = "id_role"),
            inverseJoinColumns = @JoinColumn(name = "id_permission"))
    private List<Permission> permissions;


    public Role() {}

    public Long getIdRole() { return idRole; }
    public void setIdRole(Long idRole) { this.idRole = idRole; }

    public String getCodeRole() { return codeRole; }
    public void setCodeRole(String codeRole) { this.codeRole = codeRole; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public List<Permission> getPermissions() { return permissions; }
    public void setPermissions(List<Permission> permissions) { this.permissions = permissions; }
}