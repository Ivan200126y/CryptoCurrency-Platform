package com.telerikacademy.web.cryptocurrency_trading_platform.controllers.mvc;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public String handleTypeMismatchException(Exception ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "NotFound";
    }
}
