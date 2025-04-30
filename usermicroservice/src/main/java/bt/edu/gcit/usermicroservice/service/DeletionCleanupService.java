
package bt.edu.gcit.usermicroservice.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import bt.edu.gcit.usermicroservice.dao.UserDAO;
import bt.edu.gcit.usermicroservice.entity.User;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class DeletionCleanupService {

    @Autowired
    private UserDAO userDAO;

    private static final Logger logger = LoggerFactory.getLogger(DeletionCleanupService.class);

    // // @Scheduled(cron = "*/30 * * * * ?") // Every 30 seconds for testing purposes (adjust as needed)
    // @Scheduled(cron = "0 0/5 * * * ?") 
    // @Transactional
    // public void checkExpiredDeletions() {
    //     logger.info("🕒 Scheduled cleanup task running...");

    //     // Fetch all users with role ID = 2 (this assumes you have a method for this in your DAO)
    //     List<User> allUsers = userDAO.findByRoleId(2L); // Role ID 2

    //     // Filter for soft-deleted users who have been deleted for more than 1 day (24 hours) from the createdAt
    //     List<User> expiredDeletedUsers = allUsers.stream()
    //             .filter(User::isDeleted)
    //             .filter(user -> user.getCreatedAt() != null &&
    //                     user.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1))) // Check if created more than 1 day ago
    //             .collect(Collectors.toList());

    //     if (expiredDeletedUsers.isEmpty()) {
    //         logger.info("✅ No users found for deletion cleanup.");
    //     } else {
    //         for (User user : expiredDeletedUsers) {
    //             logger.info("🗑️ Soft-deleted user eligible for cleanup: {}", user.getEmail());
    //             // You can choose to:
    //             // - Archive the user
    //             // - Notify admin
    //             // - Log for future permanent deletion
    //         }
    //     }
    // }
    // @Scheduled(cron = "0 0/5 * * * ?") // Every 5 minutes
    // @Transactional
    // public void checkExpiredDeletions() {
    //     logger.info("🕒 Scheduled soft-deletion task running...");
    
    //     List<User> users = userDAO.findByRoleId(2L);  // users with role ID = 2
    
    //     for (User user : users) {
    //         if (!user.isDeleted() &&
    //             user.getCreatedAt() != null &&
    //             user.getCreatedAt().isBefore(LocalDateTime.now().minusHours(24))) {
    
    //             user.setDeleted(true);
    //             user.setDeletedAt(LocalDateTime.now());
    
    //             userDAO.save(user); // ❗IMPORTANT
    //             logger.info("✅ Soft-deleted user: {}", user.getEmail());
    //         }
    //     }
    // }
    @Scheduled(cron = "0 0/1 * * * ?") // Every 1 minute for testing
@Transactional
public void checkExpiredDeletions() {
    logger.info("🧹 Running scheduled cleanup...");

    List<User> users = userDAO.findByRoleId(2L); // Custom query to fetch role=2

    for (User user : users) {
        logger.info("Checking user {} - deleted: {} - created_at: {}", 
                    user.getEmail(), user.isDeleted(), user.getCreatedAt());

        // Soft delete users who were created more than 10 days ago
        if (!user.isDeleted() && 
            user.getCreatedAt() != null &&
            user.getCreatedAt().isBefore(LocalDateTime.now().minusDays(10))) {

            user.setDeleted(true);
            user.setDeletedAt(LocalDateTime.now());
            userDAO.save(user);

            logger.info("✅ Soft-deleted user: {}", user.getEmail());
        }
    }
}

//     @Scheduled(cron = "0 0/1 * * * ?") // Every 1 minute for testing
// @Transactional
// public void checkExpiredDeletions() {
//     logger.info("🧹 Running scheduled cleanup...");

//     List<User> users = userDAO.findByRoleId(2L); // Custom query to fetch role=2

//     for (User user : users) {
//         logger.info("Checking user {} - deleted: {} - created_at: {}", user.getEmail(), user.isDeleted(), user.getCreatedAt());

//         if (!user.isDeleted() && user.getCreatedAt().isBefore(LocalDateTime.now().minusHours(12))) {
//             user.setDeleted(true);
//             user.setDeletedAt(LocalDateTime.now());
//             userDAO.save(user);

//             logger.info("✅ Soft-deleted user: {}", user.getEmail());
//         }
//     }
// }

}
