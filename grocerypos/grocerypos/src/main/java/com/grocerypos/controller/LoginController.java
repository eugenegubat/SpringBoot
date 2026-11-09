package com.grocerypos.controller;

import com.grocerypos.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class LoginController {

    private final UserService userService;

    public LoginController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/")
    public String showLoginPage(Model model) {
        userService.createDefaultUserIfNoneExists();
        return "login";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam("username") String username,
                               @RequestParam("password") String password,
                               HttpSession session,
                               Model model) {
        if (userService.authenticate(username, password)) {
            session.setAttribute("loggedInUser", username);
            return "redirect:/products";
        }

        model.addAttribute("errorMessage", "Invalid username or password!");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/?logout=true";
    }
}