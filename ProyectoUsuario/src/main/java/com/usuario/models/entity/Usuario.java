package com.usuario.models.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import java.io.Serializable;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Usuario implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "La clave es obligatoria")
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String clave;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "Email debe ser válido")
    @Size(max = 150)
    @Column(unique = true, length = 150)
    private String email;

    @NotNull(message = "El estado es obligatorio")
    @Column(nullable = false)
    private Boolean estado;

    @Column(name = "foto_url")
    private String fotoUrl;

    @Column(name = "cedula_url")
    private String cedulaUrl;
}