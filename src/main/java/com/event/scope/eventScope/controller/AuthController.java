package com.event.scope.eventScope.controller;

import com.event.scope.eventScope.model.User;
import com.event.scope.eventScope.service.RegistrationService;
import com.event.scope.eventScope.validation.UserRegistrationValidator;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserRegistrationValidator userRegistrationValidator;
    private final RegistrationService registrationService;

    public AuthController(UserRegistrationValidator userRegistrationValidator,RegistrationService registrationService) {
        this.userRegistrationValidator = userRegistrationValidator;
        this.registrationService = registrationService;
    }

    @GetMapping("/login")
    public String loginPage(
            @RequestParam(required = false) String error,
            Model model
    ) {
        if (error != null) {
            model.addAttribute("errorMessage", "Неверный логин или пароль");
        }
        return "auth/login";
    }

    @GetMapping("/registration")
    public String registrationPage(Model model) {
        model.addAttribute("user", new User());
        return "auth/registration";
    }

    @PostMapping("/registration")
    public String registration(@ModelAttribute("user")@Valid User user,
                               BindingResult bindingResult){

        userRegistrationValidator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            return "auth/registration";
        }

        registrationService.register(user);
        return "redirect:/login";
    }
}
