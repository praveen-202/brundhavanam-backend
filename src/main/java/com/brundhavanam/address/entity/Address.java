package com.brundhavanam.address.entity;

import com.brundhavanam.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/*
 This entity represents a user's saved delivery location.

 WHY this design:
 - A user can have multiple addresses (home, work, etc.)
 - GPS coordinates are stored for map navigation (delivery app)
 - Text address is stored for display and order snapshot

 IMPORTANT:
 Later when order is placed, all address fields INCLUDING latitude & longitude
 will be copied into Order table (snapshot).
*/

@Entity
@Table(
        name = "addresses",
        indexes = {
                // Fast lookup for user addresses
                @Index(name = "idx_address_user", columnList = "user_id"),
                // Fast default address fetch
                @Index(name = "idx_address_default", columnList = "user_id,is_default")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /*
     Many addresses belong to one user.
     This creates user_id foreign key in DB.
    */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // -------- UI display fields --------
    private String label;     // Home, Work
    private String fullName;
    private String mobile;

    private String street;
    private String area;
    private String city;
    private String state;
    private String pincode;
    private String country;

    // -------- Map navigation fields --------
    private Double latitude;
    private Double longitude;

    /*
     Only one default address per user
    */
    @Column(name = "is_default")
    private Boolean isDefault;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
