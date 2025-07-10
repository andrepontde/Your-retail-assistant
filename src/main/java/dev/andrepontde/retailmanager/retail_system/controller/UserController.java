package dev.andrepontde.retailmanager.retail_system.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import dev.andrepontde.retailmanager.retail_system.dto.UserDTO;
import dev.andrepontde.retailmanager.retail_system.entity.User;
import dev.andrepontde.retailmanager.retail_system.security.JwtUtil;
import dev.andrepontde.retailmanager.retail_system.service.UserService;

/**
 * REST Controller for User management and authentication.
 * 
 * This controller handles user registration, login simulation, and user context.
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * Create a new user.
     * 
     * POST /api/users/register
     * {
     *   "username": "john.doe",
     *   "email": "john@example.com",
     *   "firstName": "John",
     *   "lastName": "Doe",
     *   "role": "EMPLOYEE",
     *   "password": "password123",
     *   "primaryStoreId": 1
     * }
     */
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@RequestBody Map<String, Object> request) {
        try {
            // Extract user data
            UserDTO userDTO = new UserDTO();
            userDTO.setUsername((String) request.get("username"));
            userDTO.setEmail((String) request.get("email"));
            userDTO.setFirstName((String) request.get("firstName"));
            userDTO.setLastName((String) request.get("lastName"));
            userDTO.setRole((String) request.get("role"));

            String password = (String) request.get("password");
            Long primaryStoreId = Long.valueOf(request.get("primaryStoreId").toString());

            UserDTO createdUser = userService.createUser(userDTO, password, primaryStoreId);
            return new ResponseEntity<>(createdUser, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * User login with JWT token generation.
     * 
     * POST /api/users/login
     * {
     *   "username": "john.doe",
     *   "password": "password123"
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> request) {
        try {
            String username = request.get("username");
            String password = request.get("password");
            
            if (username == null || password == null) {
                return new ResponseEntity<>(
                    Map.of("error", "Username and password are required"), 
                    HttpStatus.BAD_REQUEST
                );
            }
            
            // Authenticate user
            Optional<User> userOpt = userService.authenticateUser(username, password);
            
            if (userOpt.isEmpty()) {
                return new ResponseEntity<>(
                    Map.of("error", "Invalid username or password"), 
                    HttpStatus.UNAUTHORIZED
                );
            }
            
            User user = userOpt.get();
            
            // Generate JWT token
            String token = jwtUtil.generateToken(
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                user.getPrimaryStore().getId(),
                user.getPrimaryStore().getName()
            );
            
            // Update last login time
            userService.updateLastLogin(username);
            
            // Return token and user info
            Map<String, Object> response = Map.of(
                "token", token,
                "tokenType", "Bearer",
                "expiresIn", jwtUtil.getExpirationTime(),
                "user", Map.of(
                    "id", user.getId(),
                    "username", user.getUsername(),
                    "fullName", user.getFullName(),
                    "role", user.getRole().name(),
                    "primaryStore", Map.of(
                        "id", user.getPrimaryStore().getId(),
                        "name", user.getPrimaryStore().getName(),
                        "location", user.getPrimaryStore().getLocation()
                    )
                )
            );

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (Exception e) {
            return new ResponseEntity<>(
                Map.of("error", "Internal server error"), 
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Get current user information.
     * 
     * GET /api/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser() {
        try {
            var currentUser = userService.getCurrentUser();
            var store = userService.getCurrentUserStore();

            Map<String, Object> response = Map.of(
                "id", currentUser.getId(),
                "username", currentUser.getUsername(),
                "fullName", currentUser.getFullName(),
                "role", currentUser.getRole().name(),
                "primaryStore", Map.of(
                    "id", store.getId(),
                    "name", store.getName(),
                    "location", store.getLocation()
                )
            );

            return new ResponseEntity<>(response, HttpStatus.OK);

        } catch (RuntimeException e) {
            return new ResponseEntity<>(
                Map.of("error", e.getMessage()), 
                HttpStatus.UNAUTHORIZED
            );
        } catch (Exception e) {
            return new ResponseEntity<>(
                Map.of("error", "Internal server error"), 
                HttpStatus.INTERNAL_SERVER_ERROR
            );
        }
    }

    /**
     * Get all users in current user's store.
     * 
     * GET /api/users/my-store
     */
    @GetMapping("/my-store")
    public ResponseEntity<List<UserDTO>> getUsersInMyStore() {
        try {
            List<UserDTO> users = userService.getUsersInCurrentStore();
            return new ResponseEntity<>(users, HttpStatus.OK);

        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Logout endpoint (for JWT, client just needs to discard the token).
     * 
     * POST /api/users/logout
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        // With JWT, logout is handled client-side by discarding the token
        // No server-side session to clear
        return new ResponseEntity<>(
            Map.of("message", "Logout successful. Please discard your JWT token."), 
            HttpStatus.OK
        );
    }
}
