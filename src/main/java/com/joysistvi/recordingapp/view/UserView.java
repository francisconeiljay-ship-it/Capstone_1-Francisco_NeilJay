package com.joysistvi.recordingapp.view;

import com.joysistvi.recordingapp.model.User;
import com.joysistvi.recordingapp.service.UserService;
import java.util.List;
import java.util.Scanner;

public class UserView {
    private final UserService userService;
    private final Scanner scanner;

    public UserView(UserService userService, Scanner scanner) {
        this.userService = userService;
        this.scanner = scanner;
    }

    public void displayAllUsers() {
        List<User> users = userService.getAllActiveUsers();
        printUserTable("ACTIVE USERS", users);
    }

    public void promptCreateUser() {
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password (min 6 chars): ");
        String password = scanner.nextLine();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine();

        if (userService.createUser(username, password, email)) {
            System.out.println("User registered successfully.");
        }
    }

    public void promptUpdateUser() {
        displayAllUsers();
        int id = readInt("Enter User ID to update: ");
        System.out.print("Enter New Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter New Email: ");
        String email = scanner.nextLine();

        if (userService.updateUser(id, username, email)) {
            System.out.println("User updated successfully.");
        } else {
            System.out.println("Failed to update user.");
        }
    }

    public void promptDeleteUser() {
        displayAllUsers();
        int id = readInt("Enter User ID to permanently delete: ");
        if (userService.deleteUser(id)) {
            System.out.println("User deleted permanently.");
        } else {
            System.out.println("Failed to delete user.");
        }
    }

    public void promptArchiveUser() {
        displayAllUsers();
        int id = readInt("Enter User ID to archive: ");
        if (userService.archiveUser(id)) {
            System.out.println("User archived successfully.");
        } else {
            System.out.println("Failed to archive user.");
        }
    }

    public void promptRestoreUser() {
        List<User> archived = userService.getAllArchivedUsers();
        if (archived.isEmpty()) {
            System.out.println("No archived users found.");
            return;
        }
        printUserTable("ARCHIVED USERS", archived);
        int id = readInt("Enter User ID to restore: ");
        if (userService.restoreUser(id)) {
            System.out.println("User restored successfully.");
        } else {
            System.out.println("Failed to restore user.");
        }
    }

    public void promptSearchUsers() {
        System.out.print("Enter Username search keyword: ");
        String keyword = scanner.nextLine();
        List<User> results = userService.searchUsers(keyword);
        printUserTable("SEARCH RESULTS", results);
    }

    public User promptLoginUser() {
        System.out.print("Username: ");
        String username = scanner.nextLine();
        System.out.print("Password: ");
        String password = scanner.nextLine();

        User loggedIn = userService.loginUser(username, password);
        if (loggedIn != null) {
            System.out.println("Login successful! Welcome, " + loggedIn.getUsername() + " (" + loggedIn.getRole() + ").");
        } else {
            System.out.println("Login failed: Invalid username or password.");
        }
        return loggedIn;
    }

    private void printUserTable(String title, List<User> users) {
        System.out.println("\n=== " + title + " ===");
        if (users.isEmpty()) {
            System.out.println("No records found.");
            return;
        }
        System.out.printf("%-5s | %-18s | %-25s | %-10s%n", "ID", "Username", "Email", "Role");
        System.out.println("------------------------------------------------------------------");
        for (User u : users) {
            System.out.printf("%-5d | %-18s | %-25s | %-10s%n",
                    u.getId(), u.getUsername(), u.getEmail(), u.getRole());
        }
    }

    private int readInt(String prompt) {
        System.out.print(prompt);
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }
}