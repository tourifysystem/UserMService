package bt.edu.gcit.usermicroservice.rest;
import bt.edu.gcit.usermicroservice.entity.User;
import bt.edu.gcit.usermicroservice.security.JwtUtil;
import bt.edu.gcit.usermicroservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.web.bind.annotation.*; 
import java.util.Optional;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

import bt.edu.gcit.usermicroservice.dao.UserDAO;
import bt.edu.gcit.usermicroservice.entity.Role;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // 
import bt.edu.gcit.usermicroservice.service.ForgotpasswordService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.util.StringUtils;
@RestController

@RequestMapping("/api")
public class UserRestController {
 private UserService userService;
private final ForgotpasswordService forgotpasswordService;
private JwtUtil jwtUtil;
private UserDAO userDAO;
 private final BCryptPasswordEncoder passwordEncoder;
 @Autowired
 public UserRestController(UserService userService,BCryptPasswordEncoder passwordEncoder,ForgotpasswordService forgotpasswordService,JwtUtil jwtUtil,UserDAO userDAO) {
 this.userService = userService;
 this.forgotpasswordService = forgotpasswordService;
 this.jwtUtil = jwtUtil;
 this.userDAO = userDAO;
 this.passwordEncoder = passwordEncoder;

}

@PostMapping(value = "/signup", consumes = "multipart/form-data")
public User save(
    @RequestPart(value = "name", required = false) String name,
    @RequestPart(value = "email", required = false) String email,
    @RequestPart(value = "password", required = false) String password,
    @RequestPart(value = "photo", required = false) MultipartFile photo,
    @RequestPart(value = "roles", required = false) String rolesJson,

    @RequestPart(value = "licenseNumber", required = false) String licenseNumber,
    @RequestPart(value = "phoneNumber", required = false) String phoneNumber,
    @RequestPart(value = "differentiation", required = false) String differentiation,
    @RequestPart(value = "country", required = false) String country,
    @RequestPart(value = "address", required = false) String address  // Add the address field here
) {
    try {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setLicenseNumber(licenseNumber);
        user.setPhoneNumber(phoneNumber);
        user.setDifferentiation(differentiation);
        user.setCountry(country);
        user.setAddress(address);

        // Parse roles JSON if provided
        if (rolesJson != null && !rolesJson.isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();
            Set<Role> roles = objectMapper.readValue(rolesJson, new TypeReference<Set<Role>>() {});
            user.setRoles(roles);
        }

        // Save user
        User savedUser = userService.save(user);

        // Upload photo if provided
        if (photo != null && !photo.isEmpty()) {
            System.out.println("Uploading photo for user ID " + savedUser.getId().intValue());
            userService.uploadUserPhoto(savedUser.getId().intValue(), photo);
        } else {
            System.out.println("No photo uploaded");
        }

        return savedUser;
    } catch (IOException e) {
        throw new RuntimeException("Error while uploading photo", e);
    }
}

//  @PostMapping("/users")
//  public User save(@RequestBody User user) {
//  return userService.save(user);
//  }
 @GetMapping("/users")
 public List<User> findAll() {
     return userService.findAllUsers();
 }
   // New endpoint to get user by ID
   @GetMapping("/users/{id}")
   public User findUserById(@PathVariable Long id) {
       return userService.findUserById(id);
   }
//    @DeleteMapping("/users/{id}")
//    public String deleteUserById(@PathVariable Long id) {
//        userService.deleteUserById(id);
//        return "User with ID " + id + " deleted successfully!";
//    }
@DeleteMapping("/users/{id}")
public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
    try {
        userService.deleteUserById(id);
        return null;  // HTTP 204 No Content for success
    } catch (Exception e) {
        // Handle specific cases like user not found, unauthorized, etc.
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build(); // HTTP 404 Not Found
    }
}

  
//    @PutMapping("/users/{id}")
//    public ResponseEntity<User> updateUser(
//        @PathVariable Long id,
//        @RequestBody User userUpdates
//    ) {
//        User updatedUser = userService.updateUser(id, userUpdates);
//        return ResponseEntity.ok(updatedUser);
//    }
@PutMapping(value = "/users/{id}", consumes = "multipart/form-data")
public ResponseEntity<User> updateUser(
    @PathVariable Long id,
    @RequestPart(value = "name", required = false) String name,
    @RequestPart(value = "email", required = false) String email,
    @RequestPart(value = "password", required = false) String password,
    @RequestPart(value = "photo", required = false) MultipartFile photo,
    @RequestPart(value = "roles", required = false) String rolesJson,
    @RequestPart(value = "licenseNumber", required = false) String licenseNumber,
    @RequestPart(value = "phoneNumber", required = false) String phoneNumber,
    @RequestPart(value = "differentiation", required = false) String differentiation,
    @RequestPart(value = "country", required = false) String country,
    @RequestPart(value = "address", required = false) String address  // Optional address field
) {
    try {
        // Fetch existing user
        User existingUser = userService.findUserById(id);

        // Upload photo first (if provided)
        if (photo != null && !photo.isEmpty()) {
            System.out.println("Uploading photo for user ID " + id);
            userService.uploadUserPhoto(id.intValue(), photo);
        } else {
            System.out.println("No new photo uploaded");
        }

        // Now update user fields
        if (name != null) existingUser.setName(name);
        if (email != null) existingUser.setEmail(email);
        // if (password != null) existingUser.setPassword(password);
        if (password != null && !passwordEncoder.matches(password, existingUser.getPassword())) {
            existingUser.setPassword(passwordEncoder.encode(password));
        }
        
        if (licenseNumber != null) existingUser.setLicenseNumber(licenseNumber);
        if (phoneNumber != null) existingUser.setPhoneNumber(phoneNumber);
        if (differentiation != null) existingUser.setDifferentiation(differentiation);
        if (country != null) existingUser.setCountry(country);
        if (address != null) existingUser.setAddress(address);

        // Update roles if provided
        if (rolesJson != null && !rolesJson.isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();
            Set<Role> roles = objectMapper.readValue(rolesJson, new TypeReference<Set<Role>>() {});
            existingUser.setRoles(roles);
        }

        // Save updated user after photo is handled
        User updatedUser = userDAO.save(existingUser);
        return ResponseEntity.ok(updatedUser);

    } catch (IOException e) {
        throw new RuntimeException("Error while uploading photo", e);
    }
}

@PostMapping("/forgot-password")
public ResponseEntity<String> forgotPassword(@RequestBody Map<String, String> requestBody) {
    String email = requestBody.get("email");
    
    boolean isOtpSent = forgotpasswordService.sendOtpToEmail(email);
    
    if (isOtpSent) {
        // If OTP is successfully sent, return success message with 200 OK status
        return ResponseEntity.ok("OTP has been sent to your email!");
    } else {
        // If OTP is not sent (i.e., email not found), return error message with 400 Bad Request status
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                             .body("Failed to send OTP. Please check the email address.");
    }
}

@PostMapping("/verify-otp")
public ResponseEntity<String> verifyOtp(@RequestBody Map<String, String> requestBody) {
    String email = requestBody.get("email");
    String otp = requestBody.get("otp");

    boolean isVerified = forgotpasswordService.verifyOtp(email, otp);

    if (isVerified) {
        return ResponseEntity.ok("OTP verified successfully!");
    } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid OTP or OTP expired.");
    }
}

@PostMapping("/reset-password")
public ResponseEntity<String> resetPassword(@RequestBody Map<String, String> requestBody) {
    String email = requestBody.get("email");
    String newPassword = requestBody.get("newPassword");
    String confirmPassword = requestBody.get("confirmPassword");

    boolean isPasswordReset = forgotpasswordService.resetPassword(email, newPassword, confirmPassword);

    if (isPasswordReset) {
        return ResponseEntity.ok("Password reset successfully!");
    } else {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Passwords do not match or email is invalid.");
    }
}
// @PostMapping("/login")
// public ResponseEntity<?> login(@RequestBody User loginRequest) {
//     // Authenticate the user using email and password
//     User authenticatedUser = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());

//     if (authenticatedUser != null) {
//         // Extract role IDs and names
//         Set<Integer> roleIds = authenticatedUser.getRoles().stream()
//                 .map(role -> role.getId())  // Assuming Role has getId()
//                 .collect(Collectors.toSet());

//         Set<String> roleNames = authenticatedUser.getRoles().stream()
//                 .map(role -> role.getName().toLowerCase())  // Assuming Role has getName()
//                 .collect(Collectors.toSet());

//         // Generate JWT token using user's email
//         String token = jwtUtil.generateToken(authenticatedUser.getEmail());

//         // Prepare response payload
//         Map<String, Object> response = new HashMap<>();
//         response.put("token", token);
//         response.put("user", authenticatedUser);
//         response.put("roleIds", roleIds);
//         response.put("roleNames", roleNames);

//         return ResponseEntity.ok(response);
//     } else {
//         // Return 401 if authentication fails
//         return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                 .body("Invalid email or password.");
//     }
// }
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody User loginRequest) {
    // Authenticate the user using email and password
    User authenticatedUser = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());

    if (authenticatedUser != null) {
        // Extract role IDs and names
        Set<Integer> roleIds = authenticatedUser.getRoles().stream()
                .map(role -> role.getId())  // Assuming Role has getId()
                .collect(Collectors.toSet());

        Set<String> roleNames = authenticatedUser.getRoles().stream()
                .map(role -> role.getName().toLowerCase())  // Assuming Role has getName()
                .collect(Collectors.toSet());

        // Generate JWT token using user's email and userId
        String token = jwtUtil.generateToken(authenticatedUser.getEmail(), authenticatedUser.getId());

        // Prepare response payload
        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", authenticatedUser);  // You can use a DTO to avoid exposing sensitive data
        response.put("roleIds", roleIds);
        response.put("roleNames", roleNames);

        return ResponseEntity.ok(response);
    } else {
        // Return 401 if authentication fails
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Invalid email or password.");
    }
}


  // Endpoint for admin to approve an agent
  @PutMapping("/approve-agent/{agentId}")
  public User approveAgent(@PathVariable Long agentId) {
      return userService.approveAgent(agentId);
  }
  // Endpoint to decline agent
  @PostMapping("/decline-agent/{agentId}")
  public ResponseEntity<String> declineAgent(@PathVariable Long agentId) {
      try {
          // Call the declineAgent method in UserService
          userService.declineAgent(agentId);

          // Return a success message
          return ResponseEntity.ok("Agent has been declined successfully.");
      } catch (Exception e) {
          // Handle errors and send the error message back
          return ResponseEntity.status(400).body("Error: " + e.getMessage());
      }
  }

@PostMapping("/enable/{userId}")
public ResponseEntity<Map<String, Object>> enableUser(@PathVariable Long userId, @RequestParam String reason) {
    boolean success = userService.enableUser(userId, reason);

    Map<String, Object> response = new HashMap<>();
    response.put("message", success ? "User enabled successfully" : "User with the specified role not found");
    response.put("success", success);

    return success 
        ? ResponseEntity.ok(response) 
        : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
}

@PostMapping("/disable/{userId}")
public ResponseEntity<Map<String, Object>> disableUser(@PathVariable Long userId, @RequestParam String reason) {
    boolean success = userService.disableUser(userId, reason);

    Map<String, Object> response = new HashMap<>();
    response.put("message", success ? "User disabled successfully" : "User with the specified role not found");
    response.put("success", success);

    return success 
        ? ResponseEntity.ok(response) 
        : ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
}

@GetMapping("/activeusers")
public ResponseEntity<List<User>> getAllActiveUsers() {
    List<User> users = userService.getAllActiveUsers();
    return ResponseEntity.ok(users);
}
// ----------byroleid
  // GET users by role ID
 @ GetMapping("/role/{roleId}/all")
  public List<User> getAllUsersByRole(@PathVariable Long roleId) {
      return userService.getAllUsersByRoleId(roleId); // includes deleted
  }

 // ✅ Get Unapproved Agents (Specific route — placed before dynamic route)
 @GetMapping("/unapproved-agents")
 public List<User> getUnapprovedAgents() {
     return userService.getUnapprovedAgents();
 }
// Get Approved Agents
@GetMapping("/approved-agents")
public List<User> getApprovedAgents() {
    return userService.getApprovedAgents();
}

@GetMapping("/by-role/{roleId}")
public ResponseEntity<List<User>> getUsersByRole(@PathVariable Long roleId) {
    List<User> users = userService.getUsersByRoleIdAndNotDeleted(roleId);
    if (users.isEmpty()) {
        return ResponseEntity.noContent().build();
    }
    return ResponseEntity.ok(users);
}

// -------------------------------------------
// Change password endpoint
@PutMapping("/{id}/change-password")
public ResponseEntity<String> changePassword(
        @PathVariable Long id,
        @RequestParam String oldPassword,
        @RequestParam String newPassword,
        @RequestParam String confirmPassword) {
    
    try {
        userService.changePassword(id, oldPassword, newPassword, confirmPassword);
        return ResponseEntity.ok("Password updated successfully.");
    } catch (IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
// Endpoint to get the photo of a user by user ID
@GetMapping("/photo/{id}")
public ResponseEntity<Resource> getPhoto(@PathVariable Long id) throws IOException {
    // Get the photo filename from the service
    String filename = userService.getUserPhotoFilename(id);

    // Define the path to the images directory (make sure it's correct for your project)
    Path path = Paths.get("src/main/resources/static/images", filename);

    // Load the resource
    Resource resource = new UrlResource(path.toUri());

    // If the file exists and is readable, return it in the response
    if (resource.exists() && resource.isReadable()) {
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    } else {
        throw new RuntimeException("Could not read the file!");
    }
}
@GetMapping("/count/admins")
public ResponseEntity<Long> countAdmins() {
    long count = userService.countAdmins();
    return ResponseEntity.ok(count);
}

@GetMapping("/count/tourists")
public ResponseEntity<Long> countTourists() {
    long count = userService.countTourists();
    return ResponseEntity.ok(count);
}

@GetMapping("/count/agents")
public ResponseEntity<Long> countAgents() {
    long count = userService.countAgents();
    return ResponseEntity.ok(count);
}
@GetMapping("/tourists-by-country")
public ResponseEntity<List<Map<String, Object>>> getTouristsByCountry() {
    List<Object[]> result = userService.getTouristCountByCountry();
    List<Map<String, Object>> response = new ArrayList<>();

    for (Object[] row : result) {
        Map<String, Object> map = new HashMap<>();
        map.put("country", row[0]);
        map.put("count", row[1]);
        response.add(map);
    }

    return ResponseEntity.ok(response);
}
}
