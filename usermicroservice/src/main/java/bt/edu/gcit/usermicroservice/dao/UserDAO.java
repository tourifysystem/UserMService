package bt.edu.gcit.usermicroservice.dao;
import java.util.Optional;

import java.util.List;
import bt.edu.gcit.usermicroservice.entity.User;

public interface UserDAO{
 User save(User user);
 List<User> findAll();
 Optional<User> findById(Long id);
 void deleteById(Long id);
 Optional<User> findByEmail(String email);
 User findById(int theId);
 List<User> findByRoleId(long l);
 List<User> findAllByRoleId(long l);
 boolean toggleUserStatus(Long userId, boolean isActive);
 List<User> findAllActiveUsers();

}
