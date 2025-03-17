package ru.kata.spring.boot_security.demo.configs;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Set;

@Component
public class DataInitializer {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleService roleService;


    public DataInitializer(UserService userService, RoleService roleService,BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {
        Role adminRole = new Role(1L, "ROLE_ADMIN");
        Role userRole = new Role(2L, "ROLE_USER");
        if (roleService.listRoles().isEmpty()) { // Проверяем, есть ли уже пользователи
            // Создаем роли
            roleService.addRole(adminRole);  // Сохраняем роли в БД
            roleService.addRole(userRole);
        }
        if (userService.listUsers().isEmpty()) {
                // Создаем пользователей и кодируем пароли
            User userAdmin = new User();
            userAdmin.setUsername("admin");
            userAdmin.setPassword(passwordEncoder.encode("admin"));
            userAdmin.setRoles(Collections.singleton(adminRole)); // Назначаем роль

            User userUser = new User();
            userUser.setUsername("user");
            userUser.setPassword(passwordEncoder.encode("user"));
            userUser.setRoles(Collections.singleton(userRole)); // Назначаем роль

            // Добавляем пользователей в базу данных
            userService.addUser(userAdmin);
            userService.addUser(userUser);

            System.out.println("Тестовые пользователи добавлены!");
        } else {
            System.out.println("Пользователи уже есть в базе.");
        }
    }
}
