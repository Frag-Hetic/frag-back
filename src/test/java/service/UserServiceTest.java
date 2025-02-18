package service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.projet.hetic.frag.dto.UserInputDto;
import com.projet.hetic.frag.dto.UserOutputDto;
import com.projet.hetic.frag.exception.EntityNotFoundException;
import com.projet.hetic.frag.mapper.UserMapper;
import com.projet.hetic.frag.model.User;
import com.projet.hetic.frag.repository.UserRepository;
import com.projet.hetic.frag.service.UserService;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    // Constants for test data
    private static final Long USER_ID = 1L;
    private static final String USER_NAME = "Test User";
    private static final String USER_EMAIL = "test@test.com";
    private static final String UPDATED_NAME = "Updated User";
    private static final String UPDATED_EMAIL = "updated@test.com";

    // Common test objects
    private User mockUser;
    private User mockUpdatedUser;
    private UserOutputDto mockUserDto;
    private UserOutputDto mockUpdatedUserDto;
    private UserInputDto mockInputDto;
    private List<User> mockUserList;
    private List<UserOutputDto> mockUserDtoList;


    @BeforeEach
    void setUp() {
        // Initialize mock user
        mockUser = new User();
        mockUser.setId(USER_ID);
        mockUser.setName(USER_NAME);
        mockUser.setEmail(USER_EMAIL);

        // Initialize mock updated user
        mockUpdatedUser = new User();
        mockUpdatedUser.setId(USER_ID);
        mockUpdatedUser.setName(UPDATED_NAME);
        mockUpdatedUser.setEmail(UPDATED_EMAIL);

        // Initialize DTOs
        mockUserDto = new UserOutputDto(USER_ID, USER_NAME, USER_EMAIL);
        mockUpdatedUserDto = new UserOutputDto(USER_ID, UPDATED_NAME, UPDATED_EMAIL);
        mockInputDto = new UserInputDto(UPDATED_NAME, UPDATED_EMAIL);

        // Initialize lists
        mockUserList = Arrays.asList(mockUser);
        mockUserDtoList = Arrays.asList(mockUserDto);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Given
        when(userRepository.findAll()).thenReturn(mockUserList);
        when(userMapper.toDTO(mockUser)).thenReturn(mockUserDto);

        // When
        List<UserOutputDto> result = userService.getAllUsers();

        // Then
        assertEquals(mockUserDtoList.size(), result.size());
        assertEquals(mockUserDtoList.get(0).getId(), result.get(0).getId());
        verify(userRepository).findAll();
        verify(userMapper).toDTO(any(User.class));
    }

    @Test
    void getUserById_ShouldReturnUser_WhenUserExists() {
        // Given
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockUser));
        when(userMapper.toDTO(mockUser)).thenReturn(mockUserDto);

        // When
        UserOutputDto result = userService.getUserById(USER_ID);

        // Then
        assertEquals(mockUserDto.getId(), result.getId());
        assertEquals(mockUserDto.getName(), result.getName());
        assertEquals(mockUserDto.getEmail(), result.getEmail());
        verify(userRepository).findById(USER_ID);
        verify(userMapper).toDTO(mockUser);
    }

    @Test
    void getUserById_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.findById(USER_ID)).thenReturn(Optional.empty());
    
        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
            () -> userService.getUserById(USER_ID));
        
        // Verify exception message
        assertEquals("User not found with id: " + USER_ID, exception.getMessage());
        
        verify(userRepository).findById(USER_ID);
    }

    @Test
    void createUser_ShouldReturnCreatedUser() {
        // Given
        when(userMapper.toEntity(mockInputDto)).thenReturn(mockUser);
        when(userRepository.save(mockUser)).thenReturn(mockUser);
        when(userMapper.toDTO(mockUser)).thenReturn(mockUserDto);

        // When
        UserOutputDto result = userService.createUser(mockInputDto);

        // Then
        assertEquals(mockUserDto.getId(), result.getId());
        assertEquals(mockUserDto.getName(), result.getName());
        assertEquals(mockUserDto.getEmail(), result.getEmail());
        verify(userMapper).toEntity(mockInputDto);
        verify(userRepository).save(mockUser);
        verify(userMapper).toDTO(mockUser);
    }

    @Test
    void updateUser_ShouldReturnUpdatedUser_WhenUserExists() {
        // Given
        when(userRepository.findById(USER_ID)).thenReturn(Optional.of(mockUser));
        when(userRepository.save(mockUser)).thenReturn(mockUpdatedUser);
        when(userMapper.toDTO(mockUpdatedUser)).thenReturn(mockUpdatedUserDto);

        // When
        UserOutputDto result = userService.updateUser(USER_ID, mockInputDto);

        // Then
        assertEquals(mockUpdatedUserDto.getId(), result.getId());
        assertEquals(mockUpdatedUserDto.getName(), result.getName());
        assertEquals(mockUpdatedUserDto.getEmail(), result.getEmail());
        verify(userRepository).findById(USER_ID);
        verify(userRepository).save(mockUser);
        verify(userMapper).toDTO(mockUpdatedUser);
    }

    @Test
    void deleteUser_ShouldDeleteUser_WhenUserExists() {
        // Given
        when(userRepository.existsById(USER_ID)).thenReturn(true);

        // When
        userService.deleteUser(USER_ID);

        // Then
        verify(userRepository).existsById(USER_ID);
        verify(userRepository).deleteById(USER_ID);
    }

    @Test
    void deleteUser_ShouldThrowException_WhenUserNotFound() {
        // Given
        when(userRepository.existsById(USER_ID)).thenReturn(false);

        // When & Then
        EntityNotFoundException exception = assertThrows(EntityNotFoundException.class, 
        () -> userService.deleteUser(USER_ID));
        
        assertEquals("User not found with id: " + USER_ID, exception.getMessage());

        verify(userRepository).existsById(USER_ID);
        verify(userRepository, never()).deleteById(any());
    }
}