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
    // ONLY snapshots order + items (NO STOCK CHANGE)

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
                .map(i -> i.getVariant().getPrice()
                        .multiply(BigDecimal.valueOf(i.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Address address = addressRepository.findById(addressId)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found"));

        Order order = Order.builder()
                .user(user)
                .totalAmount(totalAmount)
                .status(OrderStatus.CREATED)
                .stockDeducted(false)

                // Address snapshot
                .fullName(address.getFullName())
                .mobile(address.getMobile())
                .street(address.getStreet())
                .area(address.getArea())
                .city(address.getCity())
                .state(address.getState())
                .pincode(address.getPincode())
                .country(address.getCountry())
                .latitude(address.getLatitude())
                .longitude(address.getLongitude())
                .build();

        orderRepository.save(order);

        for (CartItem item : items) {

            ProductVariant variant = item.getVariant();

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .productVariantId(variant.getId())
                    .productName(variant.getProduct().getName())
                    .variantLabel(variant.getLabel())
                    .unitPrice(variant.getPrice())
                    .quantity(item.getQuantity())
                    .itemTotal(
                            variant.getPrice()
                                    .multiply(BigDecimal.valueOf(item.getQuantity()))
                    )
                    .build();

            orderItemRepository.save(orderItem);
        }

        cart.setStatus(CartStatus.CHECKED_OUT);
        cartRepository.save(cart);

        return order.getId();
    }

    // ================= CONFIRM ORDER =================
    // SINGLE SOURCE OF TRUTH FOR STOCK DEDUCTION

    @Override
    public void confirmOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // 🚫 Illegal state protections
        if (order.getStatus() == OrderStatus.CANCELLED) {
            throw new BadRequestException("Cancelled order cannot be confirmed");
        }

        if (Boolean.TRUE.equals(order.getStockDeducted())) {
            return; // IDEMPOTENT + HARD SAFETY
        }

        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

        for (OrderItem item : items) {

            ProductVariant variant = variantRepository.findByIdForUpdate(item.getProductVariantId())
                    .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));

            if (variant.getStock() < item.getQuantity()) {
                throw new BadRequestException(
                        "Insufficient stock for " + item.getVariantLabel()
                );
            }

            variant.setStock(variant.getStock() - item.getQuantity());
            variantRepository.save(variant);
        }

        order.setStockDeducted(true);
        order.setStatus(OrderStatus.CONFIRMED);

        orderRepository.save(order);
    }

    // ================= CANCEL ORDER =================

    @Override
    public void cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        // ✅ Idempotent guard
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return;
        }

        // 🚫 Lifecycle protection
        if (order.getStatus() == OrderStatus.SHIPPED ||
            order.getStatus() == OrderStatus.DELIVERED) {
            throw new BadRequestException("Order cannot be cancelled at this stage");
        }

        // ✅ Restore stock ONLY if deducted earlier
        if (Boolean.TRUE.equals(order.getStockDeducted())) {

            List<OrderItem> items = orderItemRepository.findByOrderId(orderId);

            for (OrderItem item : items) {

                ProductVariant variant = variantRepository.findByIdForUpdate(item.getProductVariantId())
                        .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));

                variant.setStock(variant.getStock() + item.getQuantity());
                variantRepository.save(variant);
            }

            order.setStockDeducted(false);
        }

        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);
    }

    // ================= AUTH HELPER =================

    private User getLoggedInUser() {

        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        String mobile = principal.toString();

        return userRepository.findByMobile(mobile)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
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
//	private final CartRepository cartRepository;
//	private final CartItemRepository cartItemRepository;
//	private final AddressRepository addressRepository;
//	private final OrderRepository orderRepository;
//	private final OrderItemRepository orderItemRepository;
//	private final ProductVariantRepository variantRepository;
//	private final UserRepository userRepository;
//
//	// ================= CHECKOUT =================
//	// ONLY creates order + snapshots (NO STOCK DEDUCTION)
//
//	@Override
//	public Long checkout(Long addressId) {
//
//		User user = getLoggedInUser();
//
//		Cart cart = cartRepository.findByUserIdAndStatus(user.getId(), CartStatus.ACTIVE)
//				.orElseThrow(() -> new ResourceNotFoundException("Cart is empty"));
//
//		List<CartItem> items = cartItemRepository.findByCartId(cart.getId());
//
//		if (items.isEmpty()) {
//			throw new ResourceNotFoundException("No items in cart");
//		}
//
//		BigDecimal totalAmount = items.stream()
//				.map(i -> i.getVariant().getPrice().multiply(BigDecimal.valueOf(i.getQuantity())))
//				.reduce(BigDecimal.ZERO, BigDecimal::add);
//
//		Address address = addressRepository.findById(addressId)
//				.orElseThrow(() -> new ResourceNotFoundException("Address not found"));
//
//		Order order = Order.builder().user(user).totalAmount(totalAmount).status(OrderStatus.CREATED)
//
//				.fullName(address.getFullName()).mobile(address.getMobile()).street(address.getStreet())
//				.area(address.getArea()).city(address.getCity()).state(address.getState()).pincode(address.getPincode())
//				.country(address.getCountry()).latitude(address.getLatitude()).longitude(address.getLongitude())
//				.build();
//
//		orderRepository.save(order);
//
//		for (CartItem item : items) {
//
//			ProductVariant variant = item.getVariant();
//
//			OrderItem orderItem = OrderItem.builder().order(order).productVariantId(variant.getId())
//					.productName(variant.getProduct().getName()).variantLabel(variant.getLabel())
//					.unitPrice(variant.getPrice()).quantity(item.getQuantity())
//					.itemTotal(variant.getPrice().multiply(BigDecimal.valueOf(item.getQuantity()))).build();
//
//			orderItemRepository.save(orderItem);
//		}
//
//		cart.setStatus(CartStatus.CHECKED_OUT);
//		cartRepository.save(cart);
//
//		return order.getId();
//	}
//
//	// ================= CONFIRM ORDER =================
//	// SINGLE SOURCE OF TRUTH FOR STOCK
//
//	@Override
//	public void confirmOrder(Long orderId) {
//
//		Order order = orderRepository.findById(orderId)
//				.orElseThrow(() -> new ResourceNotFoundException("Order not found"));
//
//		if (Boolean.TRUE.equals(order.getStockDeducted())) {
//		    return; // HARD SAFETY GUARD
//		}
//		
//		order.setStockDeducted(true);
//		order.setStatus(OrderStatus.CONFIRMED);
//
//		List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
//
//		for (OrderItem item : items) {
//
//			ProductVariant variant = variantRepository.findByIdForUpdate(item.getProductVariantId())
//					.orElseThrow(() -> new ResourceNotFoundException("Variant not found"));
//
//			if (variant.getStock() < item.getQuantity()) {
//				throw new BadRequestException("Insufficient stock for " + item.getVariantLabel());
//			}
//
//			variant.setStock(variant.getStock() - item.getQuantity());
//			variantRepository.save(variant);
//		}
//
//		order.setStatus(OrderStatus.CONFIRMED);
//		orderRepository.save(order);
//	}
//
//	// ================= CANCEL ORDER =================
//	// SAFE RESTORE USING SNAPSHOT
//
//	@Override
//	public void cancelOrder(Long orderId) {
//
//	    Order order = orderRepository.findById(orderId)
//	            .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
//
//	    // ✅ IDEMPOTENT GUARD
//	    if (order.getStatus() == OrderStatus.CANCELLED) {
//	        return;
//	    }
//
//	    // 🚫 LIFECYCLE PROTECTION (PUT IT HERE)
//	    if (order.getStatus() == OrderStatus.SHIPPED ||
//	        order.getStatus() == OrderStatus.DELIVERED) {
//	        throw new BadRequestException("Order cannot be cancelled at this stage");
//	    }
//
//	    // ✅ RESTORE STOCK ONLY IF DEDUCTED
//	    if (Boolean.TRUE.equals(order.getStockDeducted())) {
//
//	        List<OrderItem> items = orderItemRepository.findByOrderId(orderId);
//
//	        for (OrderItem item : items) {
//
//	            ProductVariant variant = variantRepository
//	                    .findByIdForUpdate(item.getProductVariantId())
//	                    .orElseThrow(() -> new ResourceNotFoundException("Variant not found"));
//
//	            variant.setStock(variant.getStock() + item.getQuantity());
//	            variantRepository.save(variant);
//	        }
//
//	        order.setStockDeducted(false); // optional but clean
//	    }
//
//	    order.setStatus(OrderStatus.CANCELLED);
//	    orderRepository.save(order);
//	}
//
//
//	private User getLoggedInUser() {
//		String mobile = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
//
//		return userRepository.findByMobile(mobile).orElseThrow(() -> new ResourceNotFoundException("User not found"));
//	}
//}