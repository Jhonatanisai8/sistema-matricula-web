package com.isai.demowebregistrationsystem.model.entities;

import com.isai.demowebregistrationsystem.model.enums.Rol;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "USUARIO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer idUsuario;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String userName;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @Column(name = "activo", nullable = false)
    private Boolean activo;

    @Column(name = "fecha_creacion", columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaCreacion;

    @Column(name = "intentos_fallidos", nullable = false)
    private Integer intentosFallidos;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_persona", unique = true, nullable = false)
    private Persona persona;

    @PrePersist
    protected void onCreate() {
        if (this.activo == null) this.activo = true;
        if (this.intentosFallidos == null) this.intentosFallidos = 0;
        if (this.fechaCreacion == null) this.fechaCreacion = LocalDateTime.now();
    }


}