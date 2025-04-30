
package bt.edu.gcit.usermicroservice.dao;

import bt.edu.gcit.usermicroservice.entity.User;
import java.util.List;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
@Repository
public class UserDAOImpl implements UserDAO {
    private final EntityManager entityManager;

    @Autowired
    public UserDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    @Transactional
    public User save(User user) {
        return entityManager.merge(user);
    }
    // @Override
    // public List<User> findAll() {
    //     TypedQuery<User> query = entityManager.createQuery("SELECT u FROM User u", User.class);
    //     return query.getResultList();
    // }
// //   for soft deltion cahnged here --------------------------------
    @Override
public List<User> findAll() {
    TypedQuery<User> query = entityManager.createQuery(
        "SELECT u FROM User u WHERE u.deleted = false", User.class);
    return query.getResultList();
}

    @Override
    public Optional<User> findById(Long id) {
        User user = entityManager.find(User.class, id);
        return Optional.ofNullable(user); // Wrap user in Optional to avoid null pointer
    }
   @Override
    public void deleteById(Long id) {
        User user = entityManager.find(User.class, id);
        if (user != null) {
            entityManager.remove(user);
        }
    }

//   for soft deltion cahnged here ---------------------
@Override
public Optional<User> findByEmail(String email) {
    try {
        // User user = entityManager.createQuery(
        //         "SELECT u FROM User u WHERE u.email = :email", User.class)
        //         .setParameter("email", email)
        //         .getSingleResult();
        User user = entityManager.createQuery(
        "SELECT u FROM User u WHERE u.email = :email AND u.deleted = false", User.class)
        .setParameter("email", email)
        .getSingleResult();

        return Optional.ofNullable(user);
    } catch (NoResultException e) {
        return Optional.empty();
    }
}

@Override
public User findById(int theId) {
    User user = entityManager.find(User.class, theId);
    if (user != null) {
        return user;
    } else {
        throw new RuntimeException("User not found with id - " + theId);
    }
}
@Override
public List<User> findByRoleId(long roleId) {
    String queryStr = "SELECT u FROM User u JOIN u.roles r WHERE r.id = :roleId AND u.deleted = false";
    TypedQuery<User> query = entityManager.createQuery(queryStr, User.class);
    query.setParameter("roleId", roleId);
    return query.getResultList();
}
@Override
public List<User> findAllByRoleId(long roleId) {
    String queryStr = "SELECT u FROM User u JOIN u.roles r WHERE r.id = :roleId";
    TypedQuery<User> query = entityManager.createQuery(queryStr, User.class);
    query.setParameter("roleId", roleId);
    return query.getResultList();
}

// ---------------------------------------------------
// public boolean toggleUserStatus(Long userId, boolean isActive) {
//     // Create query to fetch the user based on userId and roleId
//     String queryStr = "SELECT u FROM User u JOIN u.roles r WHERE u.id = :userId AND r.id = :roleId";
//     TypedQuery<User> query = entityManager.createQuery(queryStr, User.class);
//     query.setParameter("userId", userId);
//     query.setParameter("roleId", 3); // Assuming roleId 3 is the target role

//     // Fetch the user directly using getSingleResult
//     try {
//         User user = query.getSingleResult();
//         user.setActive(isActive); // Set active or inactive status
//         entityManager.merge(user); // Save the updated user entity
//         return true;
//     } catch (jakarta.persistence.NoResultException e) {
//         // If no result is found, return false
//         return false;
//     }
// }
public boolean toggleUserStatusWithReason(Long userId, boolean isActive, String reason) {
    // Create query to fetch the user based on userId and roleId
    String queryStr = "SELECT u FROM User u JOIN u.roles r WHERE u.id = :userId AND r.id = :roleId";
    TypedQuery<User> query = entityManager.createQuery(queryStr, User.class);
    query.setParameter("userId", userId);
    query.setParameter("roleId", 3); // Assuming roleId 3 is the target role (e.g., agent)

    // Fetch the user directly using getSingleResult
    try {
        User user = query.getSingleResult();
        
        // Set active or inactive status
        user.setActive(isActive);
        
        if (isActive) {
            // If enabling, set the enableReason and clear the disableReason
            user.setEnableReason(reason);  // Set the reason for enabling
            user.setDisableReason(null);    // Clear the disable reason
        } else {
            // If disabling, set the disableReason and clear the enableReason
            user.setDisableReason(reason);  // Set the reason for disabling
            user.setEnableReason(null);     // Clear the enable reason
        }

        // Merge and save the updated user entity
        entityManager.merge(user);
        return true;
    } catch (jakarta.persistence.NoResultException e) {
        // If no result is found, return false
        return false;
    }
}


@Override
public List<User> findAllByRoleIdAndApprovalStatus(Long roleId, boolean isApproved) {
    String queryStr = "SELECT u FROM User u JOIN u.roles r WHERE r.id = :roleId AND u.isApproved = :isApproved AND u.deleted = false";
    TypedQuery<User> query = entityManager.createQuery(queryStr, User.class);
    query.setParameter("roleId", roleId);
    query.setParameter("isApproved", isApproved);
    return query.getResultList();
}

@Override
    public List<User> findAllActiveUsers() {
        TypedQuery<User> query = entityManager.createQuery(
            "SELECT u FROM User u WHERE u.isActive = true", // Changed to isActive
            User.class
        );
        return query.getResultList();
    }

    // 
 
   
    @Override
    public List<User> findUsersByRoleIdAndNotDeleted(Long roleId) {
        String jpql = "SELECT DISTINCT u FROM User u " +
                      "JOIN u.roles r " +
                      "WHERE r.id = :roleId AND u.deleted = false";
        return entityManager.createQuery(jpql, User.class)
                            .setParameter("roleId", roleId)
                            .getResultList();
    }
    
    @Override
public long countAdmins() {
    String jpql = "SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.id = 1";
    return entityManager.createQuery(jpql, Long.class).getSingleResult();
}

@Override
public long countTourists() {
    String jpql = "SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.id = 2";
    return entityManager.createQuery(jpql, Long.class).getSingleResult();
}

@Override
public long countAgents() {
    String jpql = "SELECT COUNT(u) FROM User u JOIN u.roles r WHERE r.id = 3";
    return entityManager.createQuery(jpql, Long.class).getSingleResult();
}

}