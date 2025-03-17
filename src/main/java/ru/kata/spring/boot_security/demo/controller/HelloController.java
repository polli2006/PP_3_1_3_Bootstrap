package ru.kata.spring.boot_security.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.RoleService;
import ru.kata.spring.boot_security.demo.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class HelloController {

	private final UserService userService;
	private final PasswordEncoder passwordEncoder;
	private final RoleService roleService;

	public HelloController(UserService userService, RoleService roleService) {
		this.userService = userService;
		this.passwordEncoder = new BCryptPasswordEncoder();
		this.roleService = roleService;
	}

	@GetMapping("/user")
	public String userPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		model.addAttribute("user", userService.loadUserByUsername(userDetails.getUsername()));
		return "user";
	}

	@GetMapping("/admin")
	public String adminPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		model.addAttribute("allUsers", userService.listUsers());
		model.addAttribute("allRoles", roleService.listRoles());
		return "admin";
	}
	@GetMapping("/delete")
	public String deleteUser(@RequestParam("id") long id) {
		userService.deleteUser(id);
		return "redirect:/admin";
	}

	@GetMapping("/deleterole")
	public String deleteRole(@RequestParam("id") long id) {
		try {
			roleService.deleteRole(id);
			return "redirect:/admin";
		}
		catch (Exception e) {
			System.out.println(e.getMessage());
			return "redirect:/admin";
		}
	}

	@GetMapping("/add")
	public String showAddUserForm(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("allRoles", roleService.listRoles());
		return "user-form";
	}

	@PostMapping("/add")
	public String addUser(@ModelAttribute User user,
						  @RequestParam(value = "roles", required = false) List<Long> roles,
						  Model model) {

		if (roles == null || roles.isEmpty()) {
			model.addAttribute("error", "Please select at least one role.");
			model.addAttribute("allRoles", roleService.listRoles());
			return "user-form"; // возвращаемся на страницу с ошибкой
		}
		// Проверяем, есть ли уже пользователь с таким username
		if (userService.loadUserByUsername(user.getUsername()) != null) {
			model.addAttribute("error", "This username is already in use!");
			model.addAttribute("user", user);
			model.addAttribute("allRoles", roleService.listRoles());
			return "user-form";  // Возвращаем форму с ошибкой
		}

		// Убираем дублирующиеся роли
		Set<Role> updatedRoles = roles.stream()
				.map(roleService::getRole)
				.collect(Collectors.toSet());

		user.setRoles(updatedRoles);

		// Если пароль не пустой, кодируем его
		if (user.getPassword() != null && !user.getPassword().isEmpty()) {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}

		// Добавляем пользователя в базу данных
		userService.addUser(user);
		return "redirect:/admin";
	}

	@GetMapping("/addrole")
	public String showAddRoleForm(Model model) {
		model.addAttribute("role", new Role());
		return "role-form";
	}

	@PostMapping("/addrole")
	public String addRole(@ModelAttribute Role role) {
		roleService.addRole(role);
		return "redirect:/admin";
	}

	@GetMapping("/editrole")
	public String showEditRoleForm(@RequestParam("id") int id, Model model) {
		Role role = roleService.getRole(id);
		model.addAttribute("role", role);
		return "role-form";
	}

	@PostMapping("/editrole")
	public String updateRole(@RequestParam("id") long id, @ModelAttribute Role role) {
		Role oldRole = roleService.getRole(id);

		for (User users: userService.listUsers()) {
			if (users.getRoles().contains(oldRole)) {
				users.getRoles().remove(oldRole);
				users.getRoles().add(role);
				userService.updateUser(users);
			}
		}
		role.setId(id);
		roleService.addRole(role);
		return "redirect:/admin";
	}

	@GetMapping("/edit")
	public String showEditUserForm(@RequestParam("id") int id, Model model) {
		User user = userService.getUser(id);
		List<Role> allRoles = roleService.listRoles();
		model.addAttribute("user", user);
		model.addAttribute("allRoles", allRoles);
		return "user-form";
	}


	@PostMapping("/edit")
	public String updateUser(@RequestParam("id") long id,
							 @ModelAttribute User user,
							 Model model,
							 @RequestParam(value = "roles", required = false) List<Long> roles) {

		if (roles == null || roles.isEmpty()) {
			model.addAttribute("error", "Please select at least one role.");
			model.addAttribute("allRoles", roleService.listRoles());
			return "user-form"; // возвращаемся на страницу с ошибкой
		}

		User existingUser = userService.getUser(id);
		if (existingUser == null) {
			model.addAttribute("error", "User not found.");
			return "user-form";
		}

		user.setId(id);
		if (user.getPassword() == null || user.getPassword().isEmpty()) {
			user.setPassword(existingUser.getPassword()); // Оставляем старый пароль
		} else {
			user.setPassword(passwordEncoder.encode(user.getPassword()));
		}

		Set<Role> updatedRoles = roles.stream()
				.map(roleService::getRole)
				.collect(Collectors.toSet());
		user.setRoles(updatedRoles);

		userService.updateUser(user);
		return "redirect:/admin";
	}

}