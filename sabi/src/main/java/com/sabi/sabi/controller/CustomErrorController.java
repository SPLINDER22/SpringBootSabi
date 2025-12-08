package com.sabi.sabi.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    private static final Logger logger = LoggerFactory.getLogger(CustomErrorController.class);

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Object message = request.getAttribute(RequestDispatcher.ERROR_MESSAGE);
        Object ex = request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        Object uri = request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);

        logger.error("Handling error request: status={}, message={}, exception={}, uri={}", status, message, ex, uri);

        model.addAttribute("status", status != null ? status : "500");
        model.addAttribute("error", message != null ? message : "Internal Server Error");
        if (ex != null) {
            model.addAttribute("message", ex.toString());
        } else {
            model.addAttribute("message", request.getAttribute("javax.servlet.error.message"));
        }
        model.addAttribute("path", uri != null ? uri : request.getRequestURI());

        return "error"; // templates/error.html
    }

    // Serve favicon requests by redirecting to the existing PNG logo to avoid 404 for /favicon.ico
    @GetMapping("/favicon.ico")
    public String faviconRedirect() {
        return "redirect:/img/logoF.png";
    }
}
