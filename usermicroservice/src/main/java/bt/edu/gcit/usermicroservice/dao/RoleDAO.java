package bt.edu.gcit.usermicroservice.dao;
import java.util.List;

import bt.edu.gcit.usermicroservice.entity.Role;
import bt.edu.gcit.usermicroservice.entity.User;
public interface RoleDAO {
 void addRole(Role role);
 List<User> findByRoleId(Long roleId);
 
}