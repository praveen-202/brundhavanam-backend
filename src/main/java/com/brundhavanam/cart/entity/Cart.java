package com.brundhavanam.cart.entity;

import com.brundhavanam.common.enums.CartStatus;
import com.brundhavanam.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(
		name = "carts",
		indexes = {
				@Index(name = "idx_cart_user_status", columnList = "user_id,status")
		}
		)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// ✅ Cart belongs to one user
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// ✅ Only one ACTIVE cart per user
	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private CartStatus status;

	@CreationTimestamp
	@Column(updatable = false)
	private LocalDateTime createdAt;

	@UpdateTimestamp
	private LocalDateTime updatedAt;
}
