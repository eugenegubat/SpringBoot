package com.grocerypos.service;

import com.grocerypos.model.User;
import com.grocerypos.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public boolean authenticate(String username, String password) {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isPresent()) {
            return userOptional.get().getPassword().equals(password);
        }
        return false;
    }

    public void createDefaultUserIfNoneExists() {
        if (userRepository.count() == 0) {
            User defaultUser = new User("admin", "admin123", "Store Cashier", "CASHIER");
            userRepository.save(defaultUser);
        }
    }
}