
// package bt.edu.gcit.usermicroservice.service;
// import bt.edu.gcit.usermicroservice.dao.UserDAO;
// import bt.edu.gcit.usermicroservice.entity.User;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.security.core.context.SecurityContextHolder;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.context.annotation.Lazy;
// import org.springframework.mail.SimpleMailMessage;
// import org.springframework.mail.javamail.JavaMailSender;

// import java.util.Optional;
// import java.util.List;
// import org.springframework.web.multipart.MultipartFile;
// import java.io.IOException;
// import org.springframework.util.StringUtils;
// import java.nio.file.Path;
// import bt.edu.gcit.usermicroservice.exception.FileSizeException;
// import bt.edu.gcit.usermicroservice.exception.UserNotFoundException;
// import java.nio.file.Paths;
// import java.time.LocalDateTime;

// @Service
// public class UserServiceImpl implements UserService {
//  private UserDAO userDAO;
//  private final BCryptPasswordEncoder passwordEncoder;
//  private final String uploadDir = "src/main/resources/static/images";
//   @Autowired
//     private JavaMailSender mailSender;
//  @Autowired
//  public UserServiceImpl(UserDAO userDAO,BCryptPasswordEncoder passwordEncoder) {
//  this.userDAO = userDAO;
//  this.userDAO = userDAO;
//  this.passwordEncoder = passwordEncoder;

//  }
// //  @Override
// //  @Transactional
// //  public User save(User user) {
// //     user.setPassword(passwordEncoder.encode(user.getPassword()));
// //  return userDAO.save(user);
// //  }
//   // Register Agent User (Initially not approved)
//   @Override
// @Transactional
// public User save(User user) {
//     if (user.getRoles().stream().anyMatch(role -> role.getId() == 3)) {
//         // Set agents as not approved by default
//         user.setApproved(false);
//     } else {
//         // For other roles (like Admin), automatically approve them
//         user.setApproved(true);
//     }

//     // Encrypt the password before saving the user
//     user.setPassword(passwordEncoder.encode(user.getPassword()));
//     return userDAO.save(user);
// }
//     @Override
//     @Transactional
//     public User approveAgent(Long agentId) {
//         // Get current logged-in user (admin)
//         String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
//         User admin = userDAO.findByEmail(currentUserName).orElseThrow(() -> new RuntimeException("Admin not found"));

//         // Check if the logged-in user is an Admin (roleId = 1)
//         if (!admin.getRoles().stream().anyMatch(role -> role.getId() == 1L)) {
//             throw new RuntimeException("Only admin can approve agents.");
//         }

//         // Find the agent to approve
//         User agent = userDAO.findById(agentId).orElseThrow(() -> new RuntimeException("Agent not found"));

//         // Check if the agent's role is agent (roleId = 3) and is not already approved
//         if (agent.getRoles().stream().anyMatch(role -> role.getId() == 3L) && !agent.isApproved()) {
//             agent.setApproved(true);  // Mark as approved
//             userDAO.save(agent);  // Save changes

//             // Send approval email
//             sendEmail(agent.getEmail(), "Your agent account has been approved", "Congratulations, your agent account has been approved by the admin.");

//             return agent;
//         } else {
//             throw new RuntimeException("This agent is either not an agent or already approved.");
//         }
//     }
//     @Override
//     @Transactional
//     public User declineAgent(Long agentId) {
//         // Get current logged-in user (admin)
//         String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
//         User admin = userDAO.findByEmail(currentUserName).orElseThrow(() -> new RuntimeException("Admin not found"));

//         // Check if the logged-in user is an Admin (roleId = 1)
//         if (!admin.getRoles().stream().anyMatch(role -> role.getId() == 1L)) {
//             throw new RuntimeException("Only admin can decline agents.");
//         }

//         // Find the agent to decline
//         User agent = userDAO.findById(agentId).orElseThrow(() -> new RuntimeException("Agent not found"));

//         // Check if the agent's role is agent (roleId = 3)
//         if (agent.getRoles().stream().anyMatch(role -> role.getId() == 3L)) {
//             agent.setApproved(false);  // Mark as not approved
//             userDAO.save(agent);  // Save changes

//             // Send decline email
//             sendEmail(agent.getEmail(), "Your agent application has been declined", "We regret to inform you that your agent application has been declined.");

//             return agent;
//         } else {
//             throw new RuntimeException("This agent is not an agent.");
//         }
//     }
//    public void sendEmail(String to, String subject, String body) {
//         // Create a SimpleMailMessage object to hold the email details
//         SimpleMailMessage message = new SimpleMailMessage();
        
//         // Set the necessary details like 'to', 'subject', and 'body' of the email
//         message.setTo(to);
//         message.setSubject(subject);
//         message.setText(body);

//         // Send the email
//         mailSender.send(message);
//     }
//     @Override
// @Transactional
// public List<User> getUnapprovedAgents() {
//     return userDAO.findAllByRoleIdAndApprovalStatus(3L, false);
// }
// @Override
// @Transactional
// public List<User> getApprovedAgents() {
//     return userDAO.findAllByRoleIdAndApprovalStatus(3L, true);
// }

//  @Override
//  public User findByID(int theId) {
//  return userDAO.findById(theId);
//  }

//  @Override
//  @Transactional
//  public List<User> findAllUsers() {
//      return userDAO.findAll();
//  }
//  @Override
//     @Transactional
//     public User findUserById(Long id) {
//         return userDAO.findById(id).orElse(null); // Return user or null if not found
//     }
//     // @Override
//     // @Transactional
//     // public void deleteUserById(Long id) {
//     //     userDAO.deleteById(id);
//     // }
//     @Override
// @Transactional
// public void deleteUserById(Long id) {
//     Optional<User> optionalUser = userDAO.findById(id);

//     if (optionalUser.isPresent()) {
//         User user = optionalUser.get();
//         boolean isAdminOrSuperAdmin = user.getRoles().stream()
//             .anyMatch(role -> role.getId() == 1L || role.getId() == 3L);

//         if (isAdminOrSuperAdmin) {
//             // Hard delete only if role is Admin (3) or Super Admin (1)
//             userDAO.deleteById(id);
//         } else {
//             // Do nothing or optionally soft delete
//             System.out.println("User not authorized for hard delete.");
//             // Optional soft delete logic:
//             // user.setDeleted(true);
//             // user.setDeletedAt(LocalDateTime.now());
//             // userDAO.save(user);
//         }
//     } else {
//         System.out.println("User not found with ID: " + id);
//     }
// }

//     @Override
//     @Transactional
//     public User updateUser(Long id, User userDetails) {
//         User existingUser = findUserById(id);
        
//         // Update basic fields
//         if (userDetails.getName() != null) {
//             existingUser.setName(userDetails.getName());
//         }
//         if (userDetails.getEmail() != null) {
//             existingUser.setEmail(userDetails.getEmail());
//         }
//         // if (userDetails.getPassword() != null) {
//         //     existingUser.setPassword(userDetails.getPassword());
//         // }
//             // Check if password is provided and encode it
//     if (userDetails.getPassword() != null) {
//         existingUser.setPassword(passwordEncoder.encode(userDetails.getPassword())); // Encode the password before saving
//     }
//         if (userDetails.getPhoto() != null) {
//             existingUser.setPhoto(userDetails.getPhoto());
//         }
        
//         // Update agent-specific fields
//         if (userDetails.getLicenseNumber() != null) {
//             existingUser.setLicenseNumber(userDetails.getLicenseNumber());
//         }
//         if (userDetails.getPhoneNumber() != null) {
//             existingUser.setPhoneNumber(userDetails.getPhoneNumber());
//         }
//         if (userDetails.getDifferentiation() != null) {
//             existingUser.setDifferentiation(userDetails.getDifferentiation());
//         }
        
//         // Update tourist-specific field
//         if (userDetails.getCountry() != null) {
//             existingUser.setCountry(userDetails.getCountry());
//         }
        
//         // // Update enabled status
//         // existingUser.setEnabled(userDetails.isEnabled());
        
//         return userDAO.save(existingUser);
//     }
//     @Override
//     @Transactional
//     public User authenticateUser(String email, String password) {
//         Optional<User> userOptional = userDAO.findByEmail(email);
//         if (userOptional.isPresent()) {
//             User user = userOptional.get();
//             if (passwordEncoder.matches(password, user.getPassword())) {
//                 return user; // Authentication successful
//             }
//         }
//         return null; // Authentication failed
//     }
//     @Transactional
//     @Override
//     public void uploadUserPhoto(int id, MultipartFile photo) throws IOException {
//     User user = findByID(id);
//     // if (user == null) {
//     // throw new UserNotFoundException("User not found with id " + id);
//     // }
//     if (photo.getSize() > 1024 * 1024) {
//     throw new FileSizeException("File size must be < 1MB");
//     }
//     // String filename = StringUtils.cleanPath(photo.getOriginalFilename());
//     // Path uploadPath = Paths.get(uploadDir, filename);
//     // photo.transferTo(uploadPath);
//     // user.setPhoto(filename);
//     // save(user);
//  String originalFilename = StringUtils.cleanPath(photo.getOriginalFilename());
//  String filenameExtension =
// originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
//  String filenameWithoutExtension = originalFilename.substring(0,
// originalFilename.lastIndexOf("."));
//  String timestamp = String.valueOf(System.currentTimeMillis());
//  // Append the timestamp to the filename
//  String filename = filenameWithoutExtension + "_" + timestamp + "." +
// filenameExtension;

//  Path uploadPath = Paths.get(uploadDir, filename);
//  photo.transferTo(uploadPath);

//  user.setPhoto(filename);
//  save(user);
//  }
// //  ------------------------------------------------

// // @Transactional
// // @Override
// //     public boolean enableUser(Long userId) {
// //         return userDAO.toggleUserStatus(userId, true); // Set user as active
// //     }
// // @Transactional
// //     @Override
// //     public boolean disableUser(Long userId) {
// //         return userDAO.toggleUserStatus(userId, false); // Set user as inactive
// //     }
// @Transactional
// @Override
// public boolean enableUser(Long userId, String reason) {
//     return userDAO.toggleUserStatusWithReason(userId, true, reason); // Set user as active
// }

// @Transactional
// @Override
// public boolean disableUser(Long userId, String reason) {
//     return userDAO.toggleUserStatusWithReason(userId, false, reason); // Set user as inactive
// }


//     @Override
//     public List<User> getAllActiveUsers() {
//         return userDAO.findAllActiveUsers();
//     }
//     @Override
//     public List<User> getAllUsersByRoleId(Long roleId) {
//         return userDAO. findAllByRoleId(roleId);
//     }

//     @Override
//     public List<User> getUsersByRoleIdAndNotDeleted(Long roleId) {
//         return userDAO.findUsersByRoleIdAndNotDeleted(roleId);
//     }
//     // ----------------------------------------------------------
//     public User changePassword(Long userId, String oldPassword, String newPassword, String confirmPassword) {
//         // Step 1: Find the user by ID
//         User user = userDAO.findById(userId)
//                 .orElseThrow(() -> new IllegalArgumentException("User not found"));
    
//         // Step 2: Verify the old password
//         BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//         if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
//             throw new IllegalArgumentException("Old password is incorrect");
//         }
    
//         // Step 3: Check if new password matches confirm password
//         if (!newPassword.equals(confirmPassword)) {
//             throw new IllegalArgumentException("New passwords do not match");
//         }
    
//         // Step 4: Hash the new password
//         String hashedPassword = passwordEncoder.encode(newPassword);
    
//         // Step 5: Save the updated password
//         user.setPassword(hashedPassword);
//         return userDAO.save(user);
//     }
    
// }

package bt.edu.gcit.usermicroservice.service;
import bt.edu.gcit.usermicroservice.dao.UserDAO;
import bt.edu.gcit.usermicroservice.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Lazy;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.util.Optional;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import org.springframework.util.StringUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import bt.edu.gcit.usermicroservice.exception.FileSizeException;
import bt.edu.gcit.usermicroservice.exception.UserNotFoundException;
import java.nio.file.Paths;
import java.time.LocalDateTime;

@Service
public class UserServiceImpl implements UserService {
 private UserDAO userDAO;
 private final BCryptPasswordEncoder passwordEncoder;
 private final String uploadDir = "src/main/resources/static/images";
  @Autowired
    private JavaMailSender mailSender;
 @Autowired
 public UserServiceImpl(UserDAO userDAO,BCryptPasswordEncoder passwordEncoder) {
 this.userDAO = userDAO;
 this.userDAO = userDAO;
 this.passwordEncoder = passwordEncoder;

 }
//  @Override
//  @Transactional
//  public User save(User user) {
//     user.setPassword(passwordEncoder.encode(user.getPassword()));
//  return userDAO.save(user);
//  }
  // Register Agent User (Initially not approved)
  @Override
@Transactional
public User save(User user) {
    if (user.getRoles().stream().anyMatch(role -> role.getId() == 3)) {
        // Set agents as not approved by default
        user.setApproved(false);
    } else {
        // For other roles (like Admin), automatically approve them
        user.setApproved(true);
    }

    // Encrypt the password before saving the user
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userDAO.save(user);
}
    @Override
    @Transactional
    public User approveAgent(Long agentId) {
        // Get current logged-in user (admin)
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User admin = userDAO.findByEmail(currentUserName).orElseThrow(() -> new RuntimeException("Admin not found"));

        // Check if the logged-in user is an Admin (roleId = 1)
        if (!admin.getRoles().stream().anyMatch(role -> role.getId() == 1L)) {
            throw new RuntimeException("Only admin can approve agents.");
        }

        // Find the agent to approve
        User agent = userDAO.findById(agentId).orElseThrow(() -> new RuntimeException("Agent not found"));

        // Check if the agent's role is agent (roleId = 3) and is not already approved
        if (agent.getRoles().stream().anyMatch(role -> role.getId() == 3L) && !agent.isApproved()) {
            agent.setApproved(true);  // Mark as approved
            userDAO.save(agent);  // Save changes

            // Send approval email
            sendEmail(agent.getEmail(), "Your agent account has been approved", "Congratulations, your agent account has been approved by the admin.");

            return agent;
        } else {
            throw new RuntimeException("This agent is either not an agent or already approved.");
        }
    }
    @Override
    @Transactional
    public User declineAgent(Long agentId) {
        // Get current logged-in user (admin)
        String currentUserName = SecurityContextHolder.getContext().getAuthentication().getName();
        User admin = userDAO.findByEmail(currentUserName).orElseThrow(() -> new RuntimeException("Admin not found"));

        // Check if the logged-in user is an Admin (roleId = 1)
        if (!admin.getRoles().stream().anyMatch(role -> role.getId() == 1L)) {
            throw new RuntimeException("Only admin can decline agents.");
        }

        // Find the agent to decline
        User agent = userDAO.findById(agentId).orElseThrow(() -> new RuntimeException("Agent not found"));

        // Check if the agent's role is agent (roleId = 3)
        if (agent.getRoles().stream().anyMatch(role -> role.getId() == 3L)) {
            agent.setApproved(false);  // Mark as not approved
            userDAO.save(agent);  // Save changes

            // Send decline email
            sendEmail(agent.getEmail(), "Your agent application has been declined", "We regret to inform you that your agent application has been declined.");

            return agent;
        } else {
            throw new RuntimeException("This agent is not an agent.");
        }
    }
   public void sendEmail(String to, String subject, String body) {
        // Create a SimpleMailMessage object to hold the email details
        SimpleMailMessage message = new SimpleMailMessage();
        
        // Set the necessary details like 'to', 'subject', and 'body' of the email
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        // Send the email
        mailSender.send(message);
    }
    @Override
@Transactional
public List<User> getUnapprovedAgents() {
    return userDAO.findAllByRoleIdAndApprovalStatus(3L, false);
}
@Override
@Transactional
public List<User> getApprovedAgents() {
    return userDAO.findAllByRoleIdAndApprovalStatus(3L, true);
}

 @Override
 public User findByID(int theId) {
 return userDAO.findById(theId);
 }

 @Override
 @Transactional
 public List<User> findAllUsers() {
     return userDAO.findAll();
 }
 @Override
    @Transactional
    public User findUserById(Long id) {
        return userDAO.findById(id).orElse(null); // Return user or null if not found
    }
    // @Override
    // @Transactional
    // public void deleteUserById(Long id) {
    //     userDAO.deleteById(id);
    // }
    @Override
@Transactional
public void deleteUserById(Long id) {
    Optional<User> optionalUser = userDAO.findById(id);

    if (optionalUser.isPresent()) {
        User user = optionalUser.get();
        boolean isAdminOrSuperAdmin = user.getRoles().stream()
            .anyMatch(role -> role.getId() == 1L || role.getId() == 3L);

        if (isAdminOrSuperAdmin) {
            // Hard delete only if role is Admin (3) or Super Admin (1)
            userDAO.deleteById(id);
        } else {
            // Do nothing or optionally soft delete
            System.out.println("User not authorized for hard delete.");
            // Optional soft delete logic:
            // user.setDeleted(true);
            // user.setDeletedAt(LocalDateTime.now());
            // userDAO.save(user);
        }
    } else {
        System.out.println("User not found with ID: " + id);
    }
}

    @Override
    @Transactional
    public User updateUser(Long id, User userDetails) {
        User existingUser = findUserById(id);
        
        // Update basic fields
        if (userDetails.getName() != null) {
            existingUser.setName(userDetails.getName());
        }
        if (userDetails.getEmail() != null) {
            existingUser.setEmail(userDetails.getEmail());
        }
        if (userDetails.getPassword() != null) {
            existingUser.setPassword(userDetails.getPassword());
        }
        // if (userDetails.getPhoto() != null) {
        //     existingUser.setPhoto(userDetails.getPhoto());
        // }
        if (userDetails.getPhoto() != null) {  // <== The photo filename passed from frontend
            existingUser.setPhoto(userDetails.getPhoto());
        }
        
        // Update agent-specific fields
        if (userDetails.getLicenseNumber() != null) {
            existingUser.setLicenseNumber(userDetails.getLicenseNumber());
        }
        if (userDetails.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(userDetails.getPhoneNumber());
        }
        if (userDetails.getDifferentiation() != null) {
            existingUser.setDifferentiation(userDetails.getDifferentiation());
        }
        
        // Update tourist-specific field
        if (userDetails.getCountry() != null) {
            existingUser.setCountry(userDetails.getCountry());
        }
        
        // // Update enabled status
        // existingUser.setEnabled(userDetails.isEnabled());
        
        return userDAO.save(existingUser);
    }
    @Override
    @Transactional
    public User authenticateUser(String email, String password) {
        Optional<User> userOptional = userDAO.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            if (passwordEncoder.matches(password, user.getPassword())) {
                return user; // Authentication successful
            }
        }
        return null; // Authentication failed
    }
    @Transactional
    @Override
    public void uploadUserPhoto(int id, MultipartFile photo) throws IOException {
    User user = findByID(id);
    // if (user == null) {
    // throw new UserNotFoundException("User not found with id " + id);
    // }
    if (photo.getSize() > 1024 * 1024) {
    throw new FileSizeException("File size must be < 1MB");
    }
    // String filename = StringUtils.cleanPath(photo.getOriginalFilename());
    // Path uploadPath = Paths.get(uploadDir, filename);
    // photo.transferTo(uploadPath);
    // user.setPhoto(filename);
    // save(user);
 String originalFilename = StringUtils.cleanPath(photo.getOriginalFilename());
 String filenameExtension =
originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
 String filenameWithoutExtension = originalFilename.substring(0,
originalFilename.lastIndexOf("."));
 String timestamp = String.valueOf(System.currentTimeMillis());
 // Append the timestamp to the filename
 String filename = filenameWithoutExtension + "_" + timestamp + "." +
filenameExtension;

 Path uploadPath = Paths.get(uploadDir, filename);
 photo.transferTo(uploadPath);

 user.setPhoto(filename);
//  save(user);
 userDAO.save(user);
 }
//  ------------------------------------------------

// @Transactional
// @Override
//     public boolean enableUser(Long userId) {
//         return userDAO.toggleUserStatus(userId, true); // Set user as active
//     }
// @Transactional
//     @Override
//     public boolean disableUser(Long userId) {
//         return userDAO.toggleUserStatus(userId, false); // Set user as inactive
//     }
@Transactional
@Override
public boolean enableUser(Long userId, String reason) {
    return userDAO.toggleUserStatusWithReason(userId, true, reason); // Set user as active
}

@Transactional
@Override
public boolean disableUser(Long userId, String reason) {
    return userDAO.toggleUserStatusWithReason(userId, false, reason); // Set user as inactive
}


    @Override
    public List<User> getAllActiveUsers() {
        return userDAO.findAllActiveUsers();
    }
    @Override
    public List<User> getAllUsersByRoleId(Long roleId) {
        return userDAO. findAllByRoleId(roleId);
    }

    @Override
    public List<User> getUsersByRoleIdAndNotDeleted(Long roleId) {
        return userDAO.findUsersByRoleIdAndNotDeleted(roleId);
    }
    // ----------------------------------------------------------
    public User changePassword(Long userId, String oldPassword, String newPassword, String confirmPassword) {
        // Step 1: Find the user by ID
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
    
        // Step 2: Verify the old password
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
    
        // Step 3: Check if new password matches confirm password
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("New passwords do not match");
        }
    
        // Step 4: Hash the new password
        String hashedPassword = passwordEncoder.encode(newPassword);
    
        // Step 5: Save the updated password
        user.setPassword(hashedPassword);
        return userDAO.save(user);
    }

    @Override
    public String getUserPhotoFilename(Long userId) {
        // Retrieve the user by their ID from the database
        User user = userDAO.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Return the filename of the user's photo (assuming the 'photo' field holds the filename)
        return user.getPhoto();  // This returns the filename, e.g., "sangay_1745597990112.jpg"
    }
    @Override
public long countAdmins() {
    return userDAO.countAdmins();
}

@Override
public long countTourists() {
    return userDAO.countTourists();
}

@Override
public long countAgents() {
    return userDAO.countAgents();
}

    
}