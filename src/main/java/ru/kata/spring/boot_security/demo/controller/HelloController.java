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

import java.util.ArrayList;
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

	@GetMapping({"/", "/index"})  // Обрабатывает главную страницу
	public String showLoginPage() {
		return "index";  // Открывает index.html
	}

	@GetMapping("/user")
	public String userPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		model.addAttribute("user", userService.loadUserByUsername(userDetails.getUsername()));
		return "user";
	}

	@GetMapping("/useradmin")
	public String useradminPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		model.addAttribute("user", userService.loadUserByUsername(userDetails.getUsername()));
		return "useradmin";
	}

	@GetMapping("/admin")
	public String adminPage(@AuthenticationPrincipal UserDetails userDetails, Model model) {
		model.addAttribute("allUsers", userService.listUsers());
		model.addAttribute("allRoles", roleService.listRoles());
		model.addAttribute("user", new User());
		return "admin";
	}
	@GetMapping("/delete")
	public String deleteUser(@RequestParam("id") long id) {
		userService.deleteUser(id);
		return "redirect:/admin";
	}


	@PostMapping("/add")
	public String addUser(@ModelAttribute User user,
						  @RequestParam(value = "roles", required = false) List<Role> roles,
						  Model model) {

		userService.addUser(user, roles);
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
		if (roles != null) {
			userService.updateUser(id, user, roles);
		} else {
			userService.updateUser(id, user, new ArrayList<>(userService.getUser(id).getRoles()));
		}

		return "redirect:/admin";
	}



}