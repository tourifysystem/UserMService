


package bt.edu.gcit.usermicroservice.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.time.LocalDateTime;

@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(length = 128, nullable = false, unique = true)
    private String email;
    
    // @Column(nullable = false)
    // private boolean enabled = true;
    
    @Column(length = 100, nullable = false)
    private String name;
    
    @Column(length = 64, nullable = false)
    private String password;
    
    @Transient
    private String confirmPassword;
    
    @Column(length = 64)
    private String photo;
    
    // Agent-specific fields
    @Column(name = "license_number", length = 50)
    private String licenseNumber;
    
    @Column(name = "phone_number", length = 20)  // Changed to String and increased length
    private String phoneNumber;  // Changed to String and camelCase naming
    
    @Column(columnDefinition = "TEXT")
    private String differentiation;
    
    // Tourist-specific field
    @Column(length = 50)
    private String country;
    // ---------------------------------------------------------for soft delation-------------
    @Column(name = "deleted", nullable = false)
    private boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;
    // // Creation date field
    // @Column(name = "created_at", nullable = false)
    // private LocalDateTime createdAt;
        // Automatically set the current date and time before persisting
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // -----------------------------------------------------------------------
   // Active or Disabled status field
   @Column(name = "is_active", nullable = false)
   private boolean isActive = true; // Default to active
//    --------------------------------------------------
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "users_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name ="role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // Constructors
    public User() {
        
    }

    // General user constructor
    public User(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    // Tourist constructor
    public User(String email, String password, String name, String country) {
        this(email, password, name);
        this.country = country;
    }

    // Agent constructor
    public User(String email, String password, String name, 
               String licenseNumber, String phoneNumber, String differentiation) {
        this(email, password, name);
        this.licenseNumber = licenseNumber;
        this.phoneNumber = phoneNumber;
        this.differentiation = differentiation;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // public boolean isEnabled() { return enabled; }
    // public void setEnabled(boolean enabled) { this.enabled = enabled; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getPhoto() { return photo; }
    public void setPhoto(String photo) { this.photo = photo; }

    public String getLicenseNumber() { return licenseNumber; }
    public void setLicenseNumber(String licenseNumber) { this.licenseNumber = licenseNumber; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getDifferentiation() { return differentiation; }
    public void setDifferentiation(String differentiation) { this.differentiation = differentiation; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    public void addRole(Role role) { this.roles.add(role); }
//  ----------------------------soft delation-------------------------------------
    public boolean isDeleted() {
        return deleted;
    }
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
    
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
    @PrePersist  // This method is called before the entity is persisted to the database
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now(); // Set current timestamp
        }
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
// -----------------------------------------------------
// Getter and Setter for isActive
public boolean isActive() { return isActive; }
public void setActive(boolean isActive) { this.isActive = isActive; }

}