package bt.edu.gcit.usermicroservice.rest;
import bt.edu.gcit.usermicroservice.entity.User;
import bt.edu.gcit.usermicroservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*; 
import java.util.Optional;

import org.springframework.web.multipart.MultipartFile;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import bt.edu.gcit.usermicroservice.entity.Role;
import java.util.Set;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity; // 
import bt.edu.gcit.usermicroservice.service.ForgotpasswordService;

import java.util.List;
import java.util.Map;
@RestController

@RequestMapping("/api")
public class UserRestController {
 private UserService userService;
private final ForgotpasswordService forgotpasswordService;

 @Autowired
 public UserRestController(UserService userService,ForgotpasswordService forgotpasswordService) {
 this.userService = userService;
 this.forgotpasswordService = forgotpasswordService;

}
@PostMapping(value = "/users", consumes = "multipart/form-data")
public User save(
    @RequestPart("name") @Valid @NotNull String name,
    @RequestPart("email") @Valid @NotNull String email,
    @RequestPart("password") @Valid @NotNull String password,
    @RequestPart("photo") @Valid @NotNull MultipartFile photo,
    @RequestPart("roles") @Valid @NotNull String rolesJson,
    
    @RequestPart(value = "licenseNumber", required = false) String licenseNumber,
    @RequestPart(value = "phoneNumber", required = false) String phoneNumber,
    @RequestPart(value = "differentiation", required = false) String differentiation,
    @RequestPart(value = "country", required = false) String country
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

        // Parse roles JSON string
        ObjectMapper objectMapper = new ObjectMapper();
        Set<Role> roles = objectMapper.readValue(rolesJson, new TypeReference<Set<Role>>() {});
        user.setRoles(roles);

        System.out.println("Uploading photo");

        // Save user
        User savedUser = userService.save(user);

        // Upload photo
        System.out.println("Uploading photo " + savedUser.getId().intValue());
        userService.uploadUserPhoto(savedUser.getId().intValue(), photo);

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

  
   @PutMapping("/users/{id}")
   public ResponseEntity<User> updateUser(
       @PathVariable Long id,
       @RequestBody User userUpdates
   ) {
       User updatedUser = userService.updateUser(id, userUpdates);
       return ResponseEntity.ok(updatedUser);
   }
//    @PostMapping("/forgot-password")
//     public String forgotPassword(@RequestParam String email) {
//         boolean isOtpSent = forgotpasswordService.sendOtpToEmail(email);
        
//         if (isOtpSent) {
//             return "OTP has been sent to your email!";
//         } else {
//             return "Failed to send OTP. Please check the email address.";
//         }
//     }
@PostMapping("/forgot-password")
public String forgotPassword(@RequestBody Map<String, String> requestBody) {
    String email = requestBody.get("email");
    boolean isOtpSent = forgotpasswordService.sendOtpToEmail(email);

    if (isOtpSent) {
        return "OTP has been sent to your email!";
    } else {
        return "Failed to send OTP. Please check the email address.";
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
// login
@PostMapping("/login")
public ResponseEntity<?> login(@RequestBody User loginRequest) {
    User authenticatedUser = userService.authenticateUser(loginRequest.getEmail(), loginRequest.getPassword());

    if (authenticatedUser != null) {
        return ResponseEntity.ok(authenticatedUser); // Return user details if authentication is successful
    } else {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid email or password.");
    }
}
// ------------------------------------------------------------------------------------
@PostMapping("/enable/{userId}")
public String enableUser(@PathVariable Long userId) {
    boolean success = userService.enableUser(userId);
    return success ? "User enabled successfully" : "User with the specified role not found";
}

@PostMapping("/disable/{userId}")
public String disableUser(@PathVariable Long userId) {
    boolean success = userService.disableUser(userId);
    return success ? "User disabled successfully" : "User with the specified role not found";
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
}
