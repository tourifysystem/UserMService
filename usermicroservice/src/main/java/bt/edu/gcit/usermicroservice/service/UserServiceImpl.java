// package bt.edu.gcit.usermicroservice.service;
// import bt.edu.gcit.usermicroservice.dao.UserDAO;
// import bt.edu.gcit.usermicroservice.entity.User;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;
// import org.springframework.transaction.annotation.Transactional;
// import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
// import org.springframework.security.crypto.password.PasswordEncoder;
// import org.springframework.context.annotation.Lazy;
// import java.util.Optional;
// import java.util.List;
// @Service
// public class UserServiceImpl implements UserService {
//  private UserDAO userDAO;
//  private final BCryptPasswordEncoder passwordEncoder;

//  @Autowired
//  public UserServiceImpl(UserDAO userDAO,BCryptPasswordEncoder passwordEncoder) {
//  this.userDAO = userDAO;
//  this.userDAO = userDAO;
//  this.passwordEncoder = passwordEncoder;

//  }
//  @Override
//  @Transactional
//  public User save(User user) {
//     user.setPassword(passwordEncoder.encode(user.getPassword()));
//  return userDAO.save(user);
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
//     @Override
//     @Transactional
//     public void deleteUserById(Long id) {
//         userDAO.deleteById(id);
//     }
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
//         if (userDetails.getPassword() != null) {
//             existingUser.setPassword(userDetails.getPassword());
//         }
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
        
//         // Update enabled status
//         existingUser.setEnabled(userDetails.isEnabled());
        
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

// }
package bt.edu.gcit.usermicroservice.service;
import bt.edu.gcit.usermicroservice.dao.UserDAO;
import bt.edu.gcit.usermicroservice.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Lazy;
import java.util.Optional;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import org.springframework.util.StringUtils;
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
 public UserServiceImpl(UserDAO userDAO,BCryptPasswordEncoder passwordEncoder) {
 this.userDAO = userDAO;
 this.userDAO = userDAO;
 this.passwordEncoder = passwordEncoder;

 }
 @Override
 @Transactional
 public User save(User user) {
    user.setPassword(passwordEncoder.encode(user.getPassword()));
 return userDAO.save(user);
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


// --------------------here too----------------------
//     @Override
//     @Transactional
// public void deleteUserById(Long id) {
//     User user = userDAO.findById(id)
//         .orElseThrow(() -> new RuntimeException("User not found"));
//     user.setDeleted(true);
//     user.setDeletedAt(LocalDateTime.now());
//     userDAO.save(user); // Use save instead of remove
// }

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
        if (userDetails.getPhoto() != null) {
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
 save(user);
 }
//  ------------------------------------------------

@Transactional
@Override
    public boolean enableUser(Long userId) {
        return userDAO.toggleUserStatus(userId, true); // Set user as active
    }
@Transactional
    @Override
    public boolean disableUser(Long userId) {
        return userDAO.toggleUserStatus(userId, false); // Set user as inactive
    }

    @Override
    public List<User> getAllActiveUsers() {
        return userDAO.findAllActiveUsers();
    }
    @Override
    public List<User> getAllUsersByRoleId(Long roleId) {
        return userDAO. findAllByRoleId(roleId);
    }
}

