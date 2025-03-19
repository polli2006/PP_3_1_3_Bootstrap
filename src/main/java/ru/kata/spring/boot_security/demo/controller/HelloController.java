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
		roleService.deleteRole(id);
		return "redirect:/admin";

	}

	@GetMapping("/add")
	public String showAddUserForm(Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("allRoles", roleService.listRoles());
		return "user-form";
	}

	@PostMapping("/add")
	public String addUser(@ModelAttribute User user,
						  @RequestParam(value = "roles", required = false) List<Role> roles,
						  Model model) {

		userService.addUser(user, roles);
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
		roleService.updateRole(id, role);
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
							 @RequestParam(value = "roles", required = false) List<Role> roles) {

		userService.updateUser(id, user, roles);
		return "redirect:/admin";
	}

}