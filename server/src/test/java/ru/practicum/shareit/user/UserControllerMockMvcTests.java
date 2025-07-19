package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.ConflictException;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerMockMvcTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("John");
        user.setEmail("john@example.com");

        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("John");
        userDto.setEmail("john@example.com");
    }

    @Test
    void getAllUsers_ShouldReturnOk() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[0].email").value("john@example.com"));

        verify(userService, times(1)).getAllUsers();
    }

    @Test
    void getUser_ExistingId_ShouldReturnOk() throws Exception {
        when(userService.getUser(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService, times(1)).getUser(1L);
    }

    @Test
    void getUser_NonExistingId_ShouldReturnNotFound() throws Exception {
        when(userService.getUser(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/users/999"))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUser(999L);
    }

    @Test
    void createNewUser_Valid_ShouldReturnCreated() throws Exception {
        UserDto inputDto = new UserDto();
        inputDto.setName("Jane");
        inputDto.setEmail("jane@example.com");

        when(userService.createUser(any(User.class))).thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("John"))
                .andExpect(jsonPath("$.email").value("john@example.com"));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void createNewUser_Conflict_ShouldReturnConflict() throws Exception {
        UserDto inputDto = new UserDto();
        inputDto.setName("Jane");
        inputDto.setEmail("jane@example.com");

        doThrow(new ConflictException("Пользователь с таким email существует"))
                .when(userService).createUser(any(User.class));

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.id").value(inputDto.getId()))
                .andExpect(jsonPath("$.name").value(inputDto.getName()))
                .andExpect(jsonPath("$.email").value(inputDto.getEmail()));

        verify(userService, times(1)).createUser(any(User.class));
    }

    @Test
    void updateUser_ExistingId_ShouldReturnOk() throws Exception {
        UserDto updateDto = new UserDto();
        updateDto.setName("Updated Name");
        updateDto.setEmail("updated@example.com");

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Updated Name");
        updatedUser.setEmail("updated@example.com");

        when(userService.getUser(1L)).thenReturn(Optional.of(user));
        when(userService.saveUser(any(User.class))).thenReturn(updatedUser);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));

        verify(userService, times(1)).getUser(1L);
        verify(userService, times(1)).saveUser(any(User.class));
    }

    @Test
    void updateUser_InvalidEmail_ShouldReturnBadRequest() throws Exception {
        UserDto updateDto = new UserDto();
        updateDto.setEmail("invalid-email"); // Невалидный email

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateUser_UserNotFound_ShouldReturnNotFound() throws Exception {
        UserDto updateDto = new UserDto();
        updateDto.setName("Updated Name");

        when(userService.getUser(999L)).thenReturn(Optional.empty());

        mockMvc.perform(patch("/users/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());

        verify(userService, times(1)).getUser(999L);
    }

    @Test
    void deleteUser_ShouldReturnNoContent() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteUser(1L);
    }
}