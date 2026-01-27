package com.event.scope.eventScope.validation;


import com.event.scope.eventScope.model.User;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class UserRegistrationValidator implements Validator {
    private final UserRegistrationService userRegistrationService;

    public UserRegistrationValidator(UserRegistrationService userRegistrationService) {
        this.userRegistrationService = userRegistrationService;
    }


    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;

        if (userRegistrationService.findByUsername(user.getUsername()).isPresent()) {
            errors.rejectValue("username", "", "Пользователь с таким именем уже существует");
        }
    }
}
