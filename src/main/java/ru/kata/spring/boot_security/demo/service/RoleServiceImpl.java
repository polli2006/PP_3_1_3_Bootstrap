package ru.kata.spring.boot_security.demo.service;

import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.model.Role;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.model.User;

import java.util.List;


@Service
@Transactional
public class RoleServiceImpl implements RoleService {

    private final RoleDao roleDao;
    private final UserDao userDao;

    public RoleServiceImpl(RoleDao roleDao, UserDao userDao) {
        this.roleDao = roleDao;
        this.userDao = userDao;
    }

    @Override
    public Role getRole(long id) {
        return roleDao.findById(id).orElse(null);
    }

    @Override
    public void deleteRole(long id) {
        Role role = getRole(id);
        if (role != null) {
            List<User> users = userDao.findByRolesContains(role);
            users.forEach(user -> {
                        user.getRoles().remove(role);
                        userDao.save(user);
                        if (user.getRoles().isEmpty()) {
                            userDao.delete(user);
                        }
                    }
            );
            roleDao.delete(role);
        }
    }

    @Override
    public void addRole(Role role) {
        roleDao.save(role);
    }

    @Override
    public void updateRole(long id, Role role) {

        roleDao.save(role);
    }

    public List<Role> listRoles() {
        return roleDao.findAll();
    }

}
