package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import ru.practicum.shareit.exceptions.ConflictException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "jdbc.url=jdbc:postgresql://localhost:5432/test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = ru.practicum.ShareItServer.class)
class UserServiceImplIntegrationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private final UserService userService;

    @Test
    void testCreateUser_ShouldSaveUser() {
        // Given
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");

        // When
        User createdUser = userService.createUser(user);

        // Then
        assertThat(createdUser.getId()).isNotNull();
        assertThat(userRepository.findUserByEmail("john@example.com")).isPresent();
    }

    @Test
    void testCreateUser_EmailConflict_ShouldThrowConflictException() {
        // Given
        User user1 = new User();
        user1.setName("John");
        user1.setEmail("john@example.com");
        userService.createUser(user1);

        // When & Then
        User user2 = new User();
        user2.setName("Jane");
        user2.setEmail("john@example.com");

        assertThrows(ConflictException.class, () -> userService.createUser(user2));
    }

    @Test
    void testSaveUser_UpdateEmail_ShouldThrowConflictException() {
        // Given
        User user1 = new User();
        user1.setName("John");
        user1.setEmail("john@example.com");
        User createdUser = userService.createUser(user1);

        // When
        createdUser.setEmail("new@example.com");
        User updatedUser = userService.saveUser(createdUser);

        // Then
        assertThat(updatedUser.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void testSaveUser_EmailConflictWithAnotherUser_ShouldThrowConflictException() {
        // Given
        User user1 = new User();
        user1.setName("John");
        user1.setEmail("john@example.com");
        userService.createUser(user1);

        User user2 = new User();
        user2.setName("Jane");
        user2.setEmail("jane@example.com");
        User createdUser2 = userService.createUser(user2);

        // When
        createdUser2.setEmail("john@example.com");

        // Then
        assertThrows(ConflictException.class, () -> userService.saveUser(createdUser2));
    }

    @Test
    void testGetUser_ShouldReturnUser() {
        // Given
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        User createdUser = userService.createUser(user);

        // When
        Optional<User> foundUser = userService.getUser(createdUser.getId());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("john@example.com");
    }

    @Test
    void testGetAllUsers_ShouldReturnAllUsers() {
        // Given
        User user1 = new User();
        user1.setName("John");
        user1.setEmail("john@example.com");
        userService.createUser(user1);

        User user2 = new User();
        user2.setName("Jane");
        user2.setEmail("jane@example.com");
        userService.createUser(user2);

        // When
        List<User> users = userService.getAllUsers();

        // Then
        assertThat(users).hasSize(2);
    }

    @Test
    void testDeleteUser_ShouldRemoveUser() {
        // Given
        User user = new User();
        user.setName("John");
        user.setEmail("john@example.com");
        User createdUser = userService.createUser(user);

        // When
        userService.deleteUser(createdUser.getId());

        // Then
        Optional<User> deletedUser = userService.getUser(createdUser.getId());
        assertThat(deletedUser).isEmpty();
    }
}