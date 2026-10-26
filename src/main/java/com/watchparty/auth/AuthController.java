package com.watchparty.auth;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {
    private final UserRepository users;
    private final PasswordEncoder encoder;

    public AuthController(UserRepository users, PasswordEncoder encoder) {
        this.users = users;
        this.encoder = encoder;
    }

    @GetMapping({"/", "/login"})
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String register(Model model) {
        model.addAttribute("form", new Registration());
        return "register";
    }

    @PostMapping("/register")
    public String create(@Valid @ModelAttribute("form") Registration form, BindingResult result) {
        if (users.existsByUsername(form.username)) {
            result.rejectValue("username", "used", "Username is already taken");
        }
        if (form.email != null && users.existsByEmail(form.email.trim().toLowerCase())) {
            result.rejectValue("email", "used", "Email is already registered");
        }
        if (form.password != null && !form.password.equals(form.confirmPassword)) {
            result.rejectValue("confirmPassword", "mismatch", "Passwords must match");
        }
        if (result.hasErrors()) {
            return "register";
        }
        users.save(new User(form.username.trim(), encoder.encode(form.password), form.displayName.trim(), form.email.trim().toLowerCase()));
        return "redirect:/login?registered";
    }

    public static class Registration {
        @NotBlank
        @Size(min = 3, max = 30)
        public String username;

        @NotBlank
        @Size(min = 8, max = 72)
        public String password;

        @NotBlank
        @Size(max = 60)
        public String displayName;

        @NotBlank
        @Email
        @Size(max = 254)
        public String email;

        @NotBlank
        public String confirmPassword;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getConfirmPassword() {
            return confirmPassword;
        }

        public void setConfirmPassword(String confirmPassword) {
            this.confirmPassword = confirmPassword;
        }
    }
}
