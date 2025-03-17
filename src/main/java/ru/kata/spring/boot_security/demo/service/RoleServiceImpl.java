package ru.kata.spring.boot_security.demo.service;

import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.model.Role;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleDao roleDao;

    public RoleServiceImpl(RoleDao roleDao) {
        this.roleDao = roleDao;
    }

    @Override
    public Role getRole(long id) {
        return roleDao.findById(id).orElse(null);
    }

    @Override
    public void deleteRole(long id) {
        roleDao.deleteById(id);
    }

    @Override
    public void addRole(Role user) {
        roleDao.save(user);
    }

    @Override
    public void updateRole(Role role) {

        roleDao.save(role);
    }

    public List<Role> listRoles() {
        return roleDao.findAll();
    }

}
