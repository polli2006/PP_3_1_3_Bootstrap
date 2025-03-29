package ru.kata.spring.boot_security.demo.configs;

import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.List;

@Component
public class DataInitializer {

    private final UserService userService;
    private final BCryptPasswordEncoder passwordEncoder;
    private final RoleService roleService;


    public DataInitializer(UserService userService, RoleService roleService, BCryptPasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.roleService = roleService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostConstruct
    public void init() {

        Role adminRole = new Role(1L, "ADMIN");
        Role userRole = new Role(2L, "USER");
        if (roleService.listRoles().isEmpty()) {

            roleService.addRole(adminRole);
            roleService.addRole(userRole);
        }
        if (userService.listUsers().isEmpty()) {

            User userAdmin = new User();
            userAdmin.setUsername("admin@mail.ru");
            userAdmin.setPassword("admin");
            userAdmin.setAge(35);
            userAdmin.setFirstName("admin");
            userAdmin.setLastName("admin");


            User userUser = new User();
            userUser.setUsername("user@mail.ru");
            userUser.setPassword("user");
            userUser.setAge(30);
            userUser.setFirstName("user");
            userUser.setLastName("user");


            userService.addUser(userAdmin, List.of(adminRole, userRole));
            userService.addUser(userUser, List.of(userRole));

            System.out.println("Тестовые пользователи добавлены!");
        } else {
            System.out.println("Пользователи уже есть в базе.");
        }

    }
}
