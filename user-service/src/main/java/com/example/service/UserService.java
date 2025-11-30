package com.example.service;

import com.example.dto.UserRequest;
import com.example.dto.UserResponse;
import com.example.entity.User;
import com.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final UserEventProducer userEventProducer;

    @Autowired
    public UserService(UserRepository userRepository, UserEventProducer userEventProducer) {
        this.userRepository = userRepository;
        this.userEventProducer = userEventProducer;
    }

    public UserResponse createUser(UserRequest userRequest) {
        // Проверка уникальности email
        if (userRepository.existsByEmail(userRequest.getEmail())) {
            throw new IllegalArgumentException("User with email " + userRequest.getEmail() + " already exists");
        }

        User user = new User(userRequest.getName(), userRequest.getEmail(), userRequest.getAge());
        User savedUser = userRepository.save(user);

        // Отправка события в Kafka
        try {
            userEventProducer.sendUserCreatedEvent(savedUser.getEmail(), savedUser.getName());
        } catch (Exception e) {
            logger.error("Failed to send user created event for email: {}", savedUser.getEmail(), e);
            // Не прерываем операцию, если не удалось отправить событие
        }

        return convertToResponse(savedUser);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        return convertToResponse(user);
    }

    public UserResponse updateUser(Long id, UserRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        // Проверка уникальности email для других пользователей
        if (!user.getEmail().equals(userRequest.getEmail()) &&
                userRepository.existsByEmailAndIdNot(userRequest.getEmail(), id)) {
            throw new IllegalArgumentException("User with email " + userRequest.getEmail() + " already exists");
        }

        user.setName(userRequest.getName());
        user.setEmail(userRequest.getEmail());
        user.setAge(userRequest.getAge());

        User updatedUser = userRepository.save(user);
        return convertToResponse(updatedUser);
    }

    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));

        String email = user.getEmail();
        String userName = user.getName();

        userRepository.deleteById(id);

        // Отправка события в Kafka
        try {
            userEventProducer.sendUserDeletedEvent(email, userName);
        } catch (Exception e) {
            logger.error("Failed to send user deleted event for email: {}", email, e);
            // Не прерываем операцию, если не удалось отправить событие
        }
    }

    private UserResponse convertToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getAge(),
                user.getCreatedAt()
        );
    }
}