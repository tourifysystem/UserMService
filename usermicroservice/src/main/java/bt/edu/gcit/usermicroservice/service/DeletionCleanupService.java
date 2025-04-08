// // package bt.edu.gcit.usermicroservice.service;

// // import org.springframework.beans.factory.annotation.Autowired;
// // import org.springframework.scheduling.annotation.Scheduled;
// // import org.springframework.stereotype.Service;

// // import bt.edu.gcit.usermicroservice.dao.UserDAO;
// // import bt.edu.gcit.usermicroservice.entity.User;
// // import jakarta.transaction.Transactional;

// // import java.time.LocalDateTime;
// // import java.util.List;
// // import java.util.stream.Collectors;

// // @Service
// // public class DeletionCleanupService {

// //     @Autowired
// //     private UserDAO userDAO;

//     // Run once daily
//     // @Scheduled(cron = "0 0 0 * * ?") // Every day at midnight
// //     @Scheduled(cron = "0 * * * * ?")
// //     @Transactional
// //     public void checkExpiredDeletions() {
// //         List<User> allUsers = userDAO.findAll(); // Will already exclude deleted ones
// //         List<User> expiredDeletedUsers = allUsers.stream()
// //                 .filter(User::isDeleted)
// //                 .filter(user -> user.getDeletedAt() != null &&
// //                         user.getDeletedAt().isBefore(LocalDateTime.now().minusDays(5)))
// //                 .collect(Collectors.toList());

// //         for (User user : expiredDeletedUsers) {
// //             System.out.println("User soft-deleted for over 5 days: " + user.getEmail());
// //             // You can choose to:
// //             // - archive the user
// //             // - notify admin
// //             // - log for future permanent deletion
// //         }
// //     }
// // }
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

    // @Scheduled(cron = "*/30 * * * * ?") // Every 30 seconds for testing purposes (adjust as needed)
    @Scheduled(cron = "0 0/5 * * * ?") 
    @Transactional
    public void checkExpiredDeletions() {
        logger.info("🕒 Scheduled cleanup task running...");

        // Fetch all users with role ID = 2 (this assumes you have a method for this in your DAO)
        List<User> allUsers = userDAO.findByRoleId(2L); // Role ID 2

        // Filter for soft-deleted users who have been deleted for more than 1 day (24 hours) from the createdAt
        List<User> expiredDeletedUsers = allUsers.stream()
                .filter(User::isDeleted)
                .filter(user -> user.getCreatedAt() != null &&
                        user.getCreatedAt().isBefore(LocalDateTime.now().minusDays(1))) // Check if created more than 1 day ago
                .collect(Collectors.toList());

        if (expiredDeletedUsers.isEmpty()) {
            logger.info("✅ No users found for deletion cleanup.");
        } else {
            for (User user : expiredDeletedUsers) {
                logger.info("🗑️ Soft-deleted user eligible for cleanup: {}", user.getEmail());
                // You can choose to:
                // - Archive the user
                // - Notify admin
                // - Log for future permanent deletion
            }
        }
    }
}
