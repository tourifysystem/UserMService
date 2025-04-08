// package bt.edu.gcit.usermicroservice.service;



// import bt.edu.gcit.usermicroservice.entity.User;
// import bt.edu.gcit.usermicroservice.dao.UserDAO;
// import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.stereotype.Service;

// import java.util.Map;
// import java.util.Optional;
// import java.util.Random;
// import java.util.concurrent.ConcurrentHashMap;
// import java.util.concurrent.ConcurrentHashMap;
// import org.springframework.mail.javamail.JavaMailSender;
// import org.springframework.mail.javamail.MimeMessageHelper;
// import jakarta.mail.internet.MimeMessage;
// import jakarta.mail.MessagingException;

// @Service
// public class ForgotpasswordServiceImpl implements ForgotpasswordService {
    
//     private final UserDAO userDAO;
//     private final JavaMailSender mailSender;  // For sending OTP to email
//  private final Map<String, String> otpStorage = new ConcurrentHashMap<>();
//     @Autowired
//     public ForgotpasswordServiceImpl(UserDAO userDAO, JavaMailSender mailSender) {
//         this.userDAO = userDAO;
//         this.mailSender = mailSender;
//     }

//     @Override
//     public boolean sendOtpToEmail(String email) {
//         Optional<User> userOptional = userDAO.findByEmail(email);
        
//         if (!userOptional.isPresent()) {
//             return false;  // User not found
//         }

//         // Generate OTP (6-digit random number)
//         String otp = generateOtp();
//         otpStorage.put(email, otp); 

//         // Send OTP email
//         try {
//             sendOtpEmail(email, otp);
//             return true;
//         } catch (MessagingException e) {
//             e.printStackTrace();
//             return false;  // Failed to send OTP
//         }
//     }

//     private String generateOtp() {
//         Random rand = new Random();
//         StringBuilder otp = new StringBuilder();
        
//         // Generate a 6-digit OTP
//         for (int i = 0; i < 6; i++) {
//             otp.append(rand.nextInt(10));  // Add random digit between 0-9
//         }
        
//         return otp.toString();
//     }

//     private void sendOtpEmail(String email, String otp) throws MessagingException {
//         MimeMessage message = mailSender.createMimeMessage();
//         MimeMessageHelper helper = new MimeMessageHelper(message, true);
        
//         helper.setTo(email);
//         helper.setSubject("Your OTP for Password Reset");
//         helper.setText("Your OTP for resetting your password is: " + otp);

//         mailSender.send(message);
//     }
//     @Override
//     public boolean verifyOtp(String email, String enteredOtp) {
//         String storedOtp = otpStorage.get(email);

//         if (storedOtp != null && storedOtp.equals(enteredOtp)) {
//             otpStorage.remove(email);  // OTP verified, remove it
//             return true;
//         }
//         return false;
//     }
// }
package bt.edu.gcit.usermicroservice.service;

import bt.edu.gcit.usermicroservice.entity.User;
import bt.edu.gcit.usermicroservice.dao.UserDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.mail.internet.MimeMessage;

import jakarta.mail.MessagingException;

@Service
public class ForgotpasswordServiceImpl implements ForgotpasswordService {

    private final UserDAO userDAO;
    private final JavaMailSender mailSender;
    private final BCryptPasswordEncoder passwordEncoder;
    // Store OTP and its generation timestamp
    private final Map<String, OtpInfo> otpStorage = new ConcurrentHashMap<>();

    private static final long OTP_VALID_DURATION_SECONDS = 5 * 60; // 5 minutes

    @Autowired
    public ForgotpasswordServiceImpl(UserDAO userDAO, JavaMailSender mailSender, BCryptPasswordEncoder passwordEncoder) {
        this.userDAO = userDAO;
        this.mailSender = mailSender;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public boolean sendOtpToEmail(String email) {
        Optional<User> userOptional = userDAO.findByEmail(email);

        if (!userOptional.isPresent()) {
            return false;  // User not found
        }

        String otp = generateOtp();

        // Store OTP and generation timestamp
        otpStorage.put(email, new OtpInfo(otp, Instant.now()));

        try {
            sendOtpEmail(email, otp);
            return true;
        } catch (MessagingException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String generateOtp() {
        Random rand = new Random();
        StringBuilder otp = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            otp.append(rand.nextInt(10));
        }
        return otp.toString();
    }

    private void sendOtpEmail(String email, String otp) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        helper.setTo(email);
        helper.setSubject("Your OTP for Password Reset");
        helper.setText("Your OTP for resetting your password is: " + otp + "\n\nNote: This OTP is valid for 5 minutes.");
        mailSender.send(message);
    }

    @Override
    public boolean verifyOtp(String email, String enteredOtp) {
        OtpInfo otpInfo = otpStorage.get(email);

        if (otpInfo != null) {
            // Check expiration
            Instant now = Instant.now();
            if (now.isBefore(otpInfo.generatedAt.plusSeconds(OTP_VALID_DURATION_SECONDS)) &&
                otpInfo.otp.equals(enteredOtp)) {
                otpStorage.remove(email); // OTP is valid and used
                return true;
            } else {
                otpStorage.remove(email); // Remove expired OTP
            }
        }

        return false;
    }
    

    // Helper class to store OTP and timestamp
    private static class OtpInfo {
        String otp;
        Instant generatedAt;

        OtpInfo(String otp, Instant generatedAt) {
            this.otp = otp;
            this.generatedAt = generatedAt;
        }
    }
//     @Override
//     @Transactional
// public boolean resetPassword(String email, String newPassword, String confirmPassword) {
//     // Validate that the password and confirmation password match
//     if (!newPassword.equals(confirmPassword)) {
//         return false;  // Passwords do not match
//     }

//     Optional<User> userOptional = userDAO.findByEmail(email);

//     if (!userOptional.isPresent()) {
//         return false;  // User not found
//     }

//     User user = userOptional.get();
//     user.setPassword(newPassword); // Set the new password

//     userDAO.save(user); // Save the user with the new password
//     otpStorage.remove(email); // Remove the OTP after successful reset
//     return true;
// }
    @Transactional
@Override
public boolean resetPassword(String email, String newPassword, String confirmPassword) {
    // Validate that the password and confirmation password match
    if (!newPassword.equals(confirmPassword)) {
        return false;  // Passwords do not match
    }

    Optional<User> userOptional = userDAO.findByEmail(email);

    if (!userOptional.isPresent()) {
        return false;  // User not found
    }

    User user = userOptional.get();

    // Hash the new password before saving
    String hashedPassword = passwordEncoder.encode(newPassword);
    user.setPassword(hashedPassword); // Set the hashed password

    userDAO.save(user); // Save the user with the new password
    otpStorage.remove(email); // Remove the OTP after successful reset
    return true;
}
}
