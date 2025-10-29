package com.sabi.sabi.entity;

import com.sabi.sabi.entity.enums.Departamento;
import com.sabi.sabi.entity.enums.Rol;
import com.sabi.sabi.entity.enums.Sexo;
import com.sabi.sabi.entity.enums.TipoDocumento;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(length = 100)
    private String apellido;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "contrasena", nullable = false)
    private String contraseña;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Sexo sexo;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(length = 50)
    private Departamento departamento;

    @Column(length = 100)
    private String ciudad;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_documento", length = 20)
    private TipoDocumento tipoDocumento;

    @Column(name = "numero_documento", length = 20)
    private String numeroDocumento;

    @Column(length = 20)
    private String telefono;

    @Column(name = "foto_perfil_url", length = 500)
    private String fotoPerfilUrl;

    @Column(length = 1000)
    private String descripcion;

    @OneToMany(mappedBy = "usuario")
    private List<Ejercicio> ejercicios;

    @Builder.Default
    @Column(name = "estado", nullable = false)
    private Boolean estado = true;

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + this.rol.name()));
    }

    public Integer getEdad() {
        if (fechaNacimiento == null) {
            return null;
        }
        return LocalDate.now().getYear() - fechaNacimiento.getYear();
    }
}
