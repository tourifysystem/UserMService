package bt.edu.gcit.usermicroservice.dao;
import bt.edu.gcit.usermicroservice.entity.Role;
import bt.edu.gcit.usermicroservice.entity.User;

import org.springframework.stereotype.Repository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
@Repository
public class RoleDAOImpl implements RoleDAO {
 private EntityManager entityManager;
 @Autowired
 public RoleDAOImpl(EntityManager entityManager) {
 this.entityManager = entityManager;
 }
 @Override
 public void addRole(Role role) {
 // TODO Auto-generated method
 entityManager.persist(role);
 }

   @Override
    public List<User> findByRoleId(Long roleId) {
        // Create the query to fetch users by role ID
        String queryStr = "SELECT u FROM User u JOIN u.roles r WHERE r.id = :roleId";
        TypedQuery<User> query = entityManager.createQuery(queryStr, User.class);
        query.setParameter("roleId", roleId);
        return query.getResultList();
    }
}