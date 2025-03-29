package ru.kata.spring.boot_security.demo.service;

import ru.kata.spring.boot_security.demo.model.Role;

import java.util.List;

public interface RoleService {
    public Role getRole(long id);
    void deleteRole(long id);
    void addRole(Role role);
    void updateRole(long id, Role role);
    List<Role> listRoles();
    public void deleteAllRoles();
    List<Role> listRolesByIds(List<Long> roleIds);
}
