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
 boolean enableUser(Long userId);
 boolean disableUser(Long userId);
 List<User> getAllActiveUsers(); 
 List<User> getAllUsersByRoleId(Long roleId);
 
}
