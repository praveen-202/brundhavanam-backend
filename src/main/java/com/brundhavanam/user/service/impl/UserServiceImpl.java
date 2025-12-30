package com.brundhavanam.user.service.impl;

import com.brundhavanam.common.enums.Role;
import com.brundhavanam.common.exception.ResourceNotFoundException;
import com.brundhavanam.user.dto.UserCreateRequest;
import com.brundhavanam.user.dto.UserResponse;
import com.brundhavanam.user.entity.User;
import com.brundhavanam.user.repository.UserRepository;
import com.brundhavanam.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserResponse createUserIfNotExists(UserCreateRequest request) {

        return userRepository.findByMobile(request.mobile())
                .map(this::mapToResponse)
                .orElseGet(() -> {
                    User user = User.builder()
                            .fullName(request.fullName())
                            .mobile(request.mobile())
                            .role(Role.USER)
                            .active(true)
                            .build();

                    return mapToResponse(userRepository.save(user));
                });
    }

    @Override
    public UserResponse getByMobile(String mobile) {
        return userRepository.findByMobile(mobile)
                .map(this::mapToResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    @Override
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getFullName(),
                user.getMobile(),
                user.getRole(),
                user.getActive()
        );
    }
}
