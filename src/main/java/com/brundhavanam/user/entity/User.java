package com.brundhavanam.user.entity;
//new
import com.brundhavanam.common.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "users",
        uniqueConstraints = @UniqueConstraint(columnNames = "mobile")
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String fullName;

    @NotBlank
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Invalid mobile number")
    @Column(nullable = false)
    private String mobile; // primary identifier

    private String email; // optional

    @Enumerated(EnumType.STRING)
    private Role role;

    private Boolean active = true;

    private LocalDateTime createdAt = LocalDateTime.now();
}
