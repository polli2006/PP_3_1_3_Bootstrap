package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface UserService {
    public User getUser(long id);
    void deleteUser(long id);
    void addUser(User user, List<Role> roles);
    void updateUser(long id, User user, List<Role> roles);
    List<User> listUsers();
    UserDetails loadUserByUsername(String username);  // Добавляем метод

}
