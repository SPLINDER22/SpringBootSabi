package com.sabi.sabi.security;

import com.sabi.sabi.entity.Usuario;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;

public class CustomUserDetails implements UserDetails {

    private final Usuario usuario;

    public CustomUserDetails(Usuario usuario) {
        this.usuario = usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public String getNombre() {
        return usuario.getNombre();
    }


    public String getNombreCompleto() {
        return usuario.getNombre();
    }

    public String getTelefono() {
        try {
            java.lang.reflect.Method m = usuario.getClass().getMethod("getTelefono");
            Object value = m.invoke(usuario);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    public String getCiudad() {
        try {
            java.lang.reflect.Method m = usuario.getClass().getMethod("getCiudad");
            Object value = m.invoke(usuario);
            return value != null ? value.toString() : null;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name()));
    }

    @Override
    public String getPassword() {
        return usuario.getContraseña();
    }

    @Override
    public String getUsername() {
        return usuario.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { return usuario.getEstado(); }

    @Override
    public boolean isAccountNonLocked() { return usuario.getEstado(); }

    @Override
    public boolean isCredentialsNonExpired() { return usuario.getEstado(); }

    @Override
    public boolean isEnabled() { return usuario.getEstado(); }

    
}
