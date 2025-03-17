package ru.kata.spring.boot_security.demo.service;

import org.springframework.security.core.userdetails.UserDetails;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;

public interface RoleService {
    public Role getRole(long id);
    void deleteRole(long id);
    void addRole(Role role);
    void updateRole(Role role);
    List<Role> listRoles();

}
