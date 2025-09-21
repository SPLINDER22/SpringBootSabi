package com.sabi.sabi.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Manejo centralizado de excepciones relacionadas a subida de archivos.
 */
@ControllerAdvice(annotations = Controller.class)
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleMaxUpload(MaxUploadSizeExceededException ex,
                                  HttpServletRequest request,
                                  RedirectAttributes ra) {
        ra.addFlashAttribute("error", "La imagen excede el tamaño máximo permitido (5MB). Reduce el tamaño y vuelve a intentar.");
        return redirectBack(request);
    }

    @ExceptionHandler(MultipartException.class)
    public String handleMultipartGeneric(MultipartException ex,
                                         HttpServletRequest request,
                                         RedirectAttributes ra) {
        ra.addFlashAttribute("error", "Ocurrió un problema procesando el archivo. Verifica tamaño/formato e inténtalo nuevamente.");
        return redirectBack(request);
    }

    private String redirectBack(HttpServletRequest request) {
        String referer = request.getHeader("Referer");
        if (referer != null && (referer.startsWith("http://") || referer.startsWith("https://"))) {
            // Evitar open redirect: solo permitir volver al mismo host
            // Como es una app típica sin dominios múltiples, devolvemos a lista si no coincide
            try {
                java.net.URI uri = java.net.URI.create(referer);
                String path = uri.getPath();
                if (path != null && (path.startsWith("/ejercicios/editar") || path.startsWith("/ejercicios/nuevo"))) {
                    return "redirect:" + path + (uri.getQuery() != null ? "?" + uri.getQuery() : "");
                }
            } catch (Exception ignored) { }
        }
        return "redirect:/ejercicios";
    }
}

