package com.brundhavanam.order.service.impl;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.brundhavanam.address.entity.Address;
import com.brundhavanam.address.repository.AddressRepository;
import com.brundhavanam.cart.entity.Cart;
import com.brundhavanam.cart.entity.CartItem;
import com.brundhavanam.cart.repository.CartItemRepository;
import com.brundhavanam.cart.repository.CartRepository;
import com.brundhavanam.common.enums.CartStatus;
import com.brundhavanam.common.enums.OrderStatus;
import com.brundhavanam.common.exception.BadRequestException;
import com.brundhavanam.common.exception.ResourceNotFoundException;
import com.brundhavanam.order.entity.Order;
import com.brundhavanam.order.entity.OrderItem;
import com.brundhavanam.order.repository.OrderItemRepository;
import com.brundhavanam.order.repository.OrderRepository;
import com.brundhavanam.order.service.OrderService;
import com.brundhavanam.product.entity.ProductVariant;
import com.brundhavanam.product.repository.ProductVariantRepository;
import com.brundhavanam.user.entity.User;
import com.brundhavanam.user.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

	private final CartRepository cartRepository;
	private final CartItemRepository cartItemRepository;
	private final AddressRepository addressRepository;
	private final OrderRepository orderRepository;
	private final OrderItemRepository orderItemRepository;
	private final ProductVariantRepository variantRepository;
	private final UserRepository userRepository;

	// ================= CHECKOUT =================
	// ONLY creates order + snapshots (NO STOCK DEDUCTION)

	@Override
	public Long checkout(Long addressId) {

		User user = getLoggedInUser();

		Cart cart = cartRepository.findByUserIdAndStatus(user.getId(), CartStatus.ACTIVE)
				.orElseThrow(() -> new ResourceNotFoundException("Cart is empty"));

		List<CartItem> items = cartItemRepository.findByCartId(cart.getId());

		if (items.isEmpty()) {
			throw new ResourceNotFoundException("No items in cart");
		}

		BigDecimal totalAmount = items.stream()
				.map(i -> i.getVariant().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
				.reduce(BigDecimal.ZERO, BigDecimal::add);

		Address address = addressRepository.findById(addressId)
				.orElseThrow(() -> new ResourceNotFoundException("Address not found"));

		Order order = Order.builder().user(user).totalAmount(totalAmount).status(OrderStatus.CREATED)

				.fullName(address.getFullName()).mobile(address.getMobile()).street(address.getStreet())
				.area(address.getArea()).city(address.getCity()).state(address.getState()).pincode(address.getPincode())
				.country(address.getCountry()).latitude(address.getLatitude()).longitude(address.getLongitude())
				.build();

		orderRepository.save(order);

		for (CartItem item : items) {

			ProductVariant variant = item.getVariant();

			OrderItem orderItem = OrderItem.builder().order(order).productVariantId(variant.getId())
					.productName(variant.getProduct().getName()).variantLabel(variant.getLabel())
					.unitPrice(variant.getPrice()).quantity(item.getQuantity())
					.itemTotal(variant.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))).build();

			orderItemRepository.save(orderItem);
		}

		cart.setStatus(CartStatus.CHECKED_OUT);
		cartRepository.save(cart);

		return order.getId();
	}

	// ================= CONFIRM ORDER =================
	// SINGLE SOURCE OF TRUTH FOR STOCK

	@Override
	public void confirmOrder(Long orderId) {

		Order order = orderRepository.findById(orderId)
				.orElseThrow(() -> new ResourceNotFoundException("Order not found"));

		if (Boolean.TRUE.equals(order.getStockDeducted())) {
		    return; // HARD SAFETY GUARD
		}
		
		order.setStockDeducted(true);
		order.setStatus(OrderStatus.CONFIRMED);

		List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

		for (OrderItem item : items) {

			ProductVariant variant = variantRepository.findByIdForUpdate(item.getProductVariantId())
					.orElseThrow(() -> new ResourceNotFoundException("Variant not found"));

			if (variant.getStock() < item.getQuantity()) {
				throw new BadRequestException("Insufficient stock for " + item.getVariantLabel());
			}

			variant.setStock(variant.getStock() - item.getQuantity());
			variantRepository.save(variant);
		}

		order.setStatus(OrderStatus.CONFIRMED);
		orderRepository.save(order);
	}

	// ================= CANCEL ORDER =================
	// SAFE RESTORE USING SNAPSHOT

	@Override
	public void cancelOrder(Long orderId) {

	    Order order = orderRepository.findById(orderId)
	            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

	    // ✅ IDEMPOTENT GUARD
	    if (order.getStatus() == OrderStatus.CANCELLED) {
	        return;
	    }

	    // 🚫 LIFECYCLE PROTECTION (PUT IT HERE)
	    if (order.getStatus() == OrderStatus.SHIPPED ||
	        order.getStatus() == OrderStatus.DELIVERED) {
	        throw new BadRequestException("Order cannot be cancelled at this stage");
	    }

	    // ✅ RESTORE STOCK ONLY IF DEDUCTED
	    if (Boolean.TRUE.equals(order.getStockDeducted())) {

	        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

	        for (OrderItem item : items) {

	            ProductVariant variant = variantRepository
	                    .findByIdForUpdate(item.getProductVariantId())
	                    .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));

	            variant.setStock(variant.getStock() + item.getQuantity());
	            variantRepository.save(variant);
	        }

	        order.setStockDeducted(false); // optional but clean
	    }

	    order.setStatus(OrderStatus.CANCELLED);
	    orderRepository.save(order);
	}


	private User getLoggedInUser() {
		String mobile = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();

		return userRepository.findByMobile(mobile).orElseThrow(() -> new ResourceNotFoundException("User not found"));
	}
}

//package com.brundhavanam.order.service.impl;
//
//import java.math.BigDecimal;
//import java.util.List;
//
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import com.brundhavanam.address.entity.Address;
//import com.brundhavanam.address.repository.AddressRepository;
//import com.brundhavanam.cart.entity.Cart;
//import com.brundhavanam.cart.entity.CartItem;
//import com.brundhavanam.cart.repository.CartItemRepository;
//import com.brundhavanam.cart.repository.CartRepository;
//import com.brundhavanam.common.enums.CartStatus;
//import com.brundhavanam.common.enums.OrderStatus;
//import com.brundhavanam.common.exception.BadRequestException;
//import com.brundhavanam.common.exception.ResourceNotFoundException;
//import com.brundhavanam.order.entity.Order;
//import com.brundhavanam.order.entity.OrderItem;
//import com.brundhavanam.order.repository.OrderItemRepository;
//import com.brundhavanam.order.repository.OrderRepository;
//import com.brundhavanam.order.service.OrderService;
//import com.brundhavanam.product.entity.ProductVariant;
//import com.brundhavanam.product.repository.ProductVariantRepository;
//import com.brundhavanam.user.entity.User;
//import com.brundhavanam.user.repository.UserRepository;
//
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class OrderServiceImpl implements OrderService {
//
//    private final CartRepository cartRepository;
//    private final CartItemRepository cartItemRepository;
//    private final AddressRepository addressRepository;
//    private final OrderRepository orderRepository;
//    private final OrderItemRepository orderItemRepository;   // ✅ NEW
//    private final ProductVariantRepository variantRepository;
//    private final UserRepository userRepository;
//
// // ================= CHECKOUT =================
// // Creates order + snapshots items
// // 🔐 WITH stock locking + atomic deduction (NO overselling)
//
// @Transactional
// @Override
// public Long checkout(Long addressId) {
//
//     User user = getLoggedInUser();
//
//     Cart cart = cartRepository
//             .findByUserIdAndStatus(user.getId(), CartStatus.ACTIVE)
//             .orElseThrow(() -> new ResourceNotFoundException("Cart is empty"));
//
//     List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
//
//     if (items.isEmpty()) {
//         throw new ResourceNotFoundException("No items in cart");
//     }
//
//     // 🔢 Calculate total dynamically
//     BigDecimal totalAmount = items.stream()
//             .map(i -> i.getVariant().getPrice()
//                     .multiply(BigDecimal.valueOf(i.getQuantity())))
//             .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//     Address address = addressRepository.findById(addressId)
//             .orElseThrow(() -> new ResourceNotFoundException("Address not found"));
//
//     // 🔐 STEP 2 & 3: LOCK + RECHECK + ATOMIC DEDUCTION (CRITICAL SECTION)
//     for (CartItem item : items) {
//
//         ProductVariant variant = variantRepository
//                 .findByIdForUpdate(item.getVariant().getId())
//                 .orElseThrow(() ->
//                         new ResourceNotFoundException("Variant not found: " + item.getVariant().getId())
//                 );
//
//         if (variant.getStock() < item.getQuantity()) {
//             throw new BadRequestException(
//                     "Insufficient stock for variant: " + variant.getLabel()
//             );
//         }
//
//         // ✅ Atomic deduction
//         variant.setStock(variant.getStock() - item.getQuantity());
//         variantRepository.save(variant);
//     }
//
//     // 📦 Create Order (address snapshot)
//     Order order = Order.builder()
//             .user(user)
//             .totalAmount(totalAmount)
//             .status(OrderStatus.CREATED)
//
//             .fullName(address.getFullName())
//             .mobile(address.getMobile())
//             .street(address.getStreet())
//             .area(address.getArea())
//             .city(address.getCity())
//             .state(address.getState())
//             .pincode(address.getPincode())
//             .country(address.getCountry())
//             .latitude(address.getLatitude())
//             .longitude(address.getLongitude())
//
//             .build();
//
//     orderRepository.save(order);
//
//     // 📄 SAVE ORDER ITEM SNAPSHOTS (billing proof)
//     for (CartItem item : items) {
//
//         ProductVariant variant = item.getVariant();
//
//         OrderItem orderItem = OrderItem.builder()
//                 .order(order)
//                 .productVariantId(variant.getId())
//                 .productName(variant.getProduct().getName())
//                 .variantLabel(variant.getLabel())
//                 .unitPrice(variant.getPrice())
//                 .quantity(item.getQuantity())
//                 .itemTotal(
//                         variant.getPrice()
//                                 .multiply(BigDecimal.valueOf(item.getQuantity()))
//                 )
//                 .build();
//
//         orderItemRepository.save(orderItem);
//     }
//
//     // 🔒 Lock cart (prevents reuse)
//     cart.setStatus(CartStatus.CHECKED_OUT);
//     cartRepository.save(cart);
//
//     return order.getId();
// }
//
//
//    // ================= CONFIRM AFTER PAYMENT =================
//    // Deduct stock safely after payment or COD confirmation
//
//    @Override
//    public void confirmOrder(Long orderId) {
//
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
//
//        Cart cart = cartRepository
//                .findByUserIdAndStatus(order.getUser().getId(), CartStatus.CHECKED_OUT)
//                .orElseThrow();
//
//        List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
//
//        // 📉 Reduce inventory now (correct time)
//        for (CartItem item : items) {
//            ProductVariant variant = item.getVariant();
//            variant.setStock(variant.getStock() - item.getQuantity());
//            variantRepository.save(variant);
//        }
//
//        order.setStatus(OrderStatus.CONFIRMED);
//        orderRepository.save(order);
//    }
//
//    // ================= CANCEL ORDER =================
//    // Restores stock if already deducted
//
//    @Override
//    public void cancelOrder(Long orderId) {
//
//        Order order = orderRepository.findById(orderId)
//                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
//
//        // Restore stock only if it was deducted
//        if (order.getStatus() == OrderStatus.CONFIRMED) {
//
//            Cart cart = cartRepository
//                    .findByUserIdAndStatus(order.getUser().getId(), CartStatus.CHECKED_OUT)
//                    .orElseThrow();
//
//            List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
//
//            for (CartItem item : items) {
//                ProductVariant variant = item.getVariant();
//                variant.setStock(variant.getStock() + item.getQuantity());
//                variantRepository.save(variant);
//            }
//        }
//
//        order.setStatus(OrderStatus.CANCELLED);
//        orderRepository.save(order);
//    }
//
//    // ================= HELPER =================
//
//    private User getLoggedInUser() {
//        String mobile = SecurityContextHolder.getContext()
//                .getAuthentication()
//                .getPrincipal()
//                .toString();
//
//        return userRepository.findByMobile(mobile)
//                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
//    }
//}
