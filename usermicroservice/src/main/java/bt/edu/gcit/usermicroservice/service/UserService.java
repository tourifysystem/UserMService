package bt.edu.gcit.usermicroservice.service;
import bt.edu.gcit.usermicroservice.entity.User;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

import java.util.List;
public interface UserService {
 User save(User user);
 List<User> findAllUsers();
 User findUserById(Long id);
 void deleteUserById(Long id);
 User updateUser(Long id, User userDetails);
 

 User authenticateUser(String email, String password);
 void uploadUserPhoto(int id, MultipartFile photo) throws IOException;
 User findByID(int theId);
//  boolean enableUser(Long userId);
//  boolean disableUser(Long userId);
boolean enableUser(Long userId, String reason);
boolean disableUser(Long userId, String reason);
 List<User> getAllActiveUsers(); 
 List<User> getAllUsersByRoleId(Long roleId);
//  void approveAgent(Long userId, Long adminId);
 User approveAgent(Long agentId);
//  List<User> getUnapprovedAgents();
 // ✅ Newly added methods for agent approval status
 List<User> getUnapprovedAgents();
 List<User> getApprovedAgents();
 List<User> getUsersByRoleIdAndNotDeleted(Long roleId);
 User declineAgent(Long agentId);
 User changePassword(Long userId, String oldPassword, String newPassword, String confirmPassword);
 String getUserPhotoFilename(Long userId); 
 long countAdmins();
 long countTourists();
 long countAgents();
 
}
