package app.UnitTests;

import app.APIMessage.MailClient;
import app.Security.UserInfo;
import app.User.model.User;
import app.User.model.UserRole;
import app.User.repository.UserRepository;
import app.User.service.UserService;
import app.exception.DomainException;
import app.exception.UserDontExistException;
import app.exception.UsernameAlreadyExistException;
import app.web.dto.RegisterRequest;
import app.web.dto.UserEditRequest;
import app.web.dto.UserEditRequestAdmin;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class UserServiceUTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private MailClient mailClient;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    @Test
    void givenExistingUsersInDatabase_whenGetAllUsers_thenReturnThemAll() {


        List<User> userList = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(userList);


        List<User> users = userService.getAllUsers();


        assertThat(users).hasSize(2);
    }

    @Test
    void givenExistingUsername_whenRegister_thenExceptionIsThrown() {

        // Given
        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("chef01")
                .password("123123")
                .phone("123123")
                .email("V4m1T@example.com")
                .address("address")
                .build();
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(new User()));


        assertThrows(UsernameAlreadyExistException.class, () -> userService.register(registerRequest));
        verify(userRepository, never()).save(any());
    }


    @Test
    void givenHappyPath_whenRegister() {


        RegisterRequest registerRequest = RegisterRequest.builder()
                .username("chef02")
                .password("123123")
                .phone("123123")
                .email("V4m1T@example.com")
                .address("address")
                .build();
        User user = User.builder()
                .id(UUID.randomUUID())
                .build();
        when(userRepository.findByUsername(registerRequest.getUsername())).thenReturn(Optional.empty());
        when(userRepository.save(any())).thenReturn(user);


        User registeredUser = userService.register(registerRequest);

        assertEquals(UserRole.CLIENT, registeredUser.getRole(), "Ролята не съвпада!");

    }

    @Test
    void givenMissingUserFromDatabase_whenLoadUserByUsername_thenExceptionIsThrown() {

        String username = "Vik123";
        when(userRepository.findByUsername(username)).thenReturn(Optional.empty());

        assertThrows(DomainException.class, () -> userService.loadUserByUsername(username));
    }

    @Test
    void givenExistingUser_whenLoadUserByUsername_thenReturnCorrectAuthenticationMetadata() {

        String username = "Vik123";
        User user = User.builder()
                .id(UUID.randomUUID())
                .isActive(true)
                .password("123123")
                .role(UserRole.ADMIN)
                .build();
        when(userRepository.findByUsername(username)).thenReturn(Optional.of(user));

        UserDetails authenticationMetadata = userService.loadUserByUsername(username);

        assertInstanceOf(UserInfo.class, authenticationMetadata);
        UserInfo result = (UserInfo) authenticationMetadata;
        assertEquals(user.getId(), result.getUserId());
        assertEquals(username, result.getUsername());
        assertEquals(user.getPassword(), result.getPassword());
        assertEquals(user.isActive(), result.isActive());
        assertEquals(user.getRole(), result.getRole());
        assertThat(result.getAuthorities()).hasSize(1);
        assertEquals("ROLE_ADMIN", result.getAuthorities().iterator().next().getAuthority());
    }

    @Test
    void givenMissingUserFromDatabase_whenEditUserDetails_thenExceptionIsThrown() {

        UUID userId = UUID.randomUUID();
        UserEditRequest dto = UserEditRequest.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserDontExistException.class, () -> userService.editUserDetails(userId, dto));
    }

    @Test
    void givenExistingUser_whenEditTheirProfileWithActualEmail_thenChangeTheirDetailsSaveNotificationPreferenceAndSaveToDatabase() {

        UUID userId = UUID.randomUUID();
        UserEditRequest dto = UserEditRequest.builder()
                .firstName("Viktor")
                .lastName("Aleksandrov")
                .email("vik123@abv.bg")
                .profilePicture("www.image.com")
                .build();
        User user = User.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.editUserDetails(userId, dto);

        assertEquals("Viktor", user.getFirstName());
        assertEquals("Aleksandrov", user.getLastName());
        assertEquals("vik123@abv.bg", user.getEmail());
        assertEquals("www.image.com", user.getProfilePicture());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void whenMappingUserToUserEditRequest_thenReturnUserEditRequest() {

        User user = User.builder().build();

        UserEditRequest resultDto = userService.mapUserToUserEditRequest(user);

        assertNotNull(resultDto);

    }

    @Test
    void mapUserToUserEditRequestAdmin_thenReturnUserEditRequestAdmin() {

        User user = User.builder().role(UserRole.ADMIN).build();

        UserEditRequestAdmin resultDto = userService.mapUserToUserEditRequestAdmin(user);

        assertNotNull(resultDto);
    }

    @Test
    void editUserDetailsAdmin_thenReturnUserEditRequestAdmin() {

        UUID userId = UUID.randomUUID();
        UserEditRequestAdmin dto = UserEditRequestAdmin.builder()
                .id(userId)
                .build();
        User user = User.builder().build();

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.editUserDetailsAdmin(dto);

        verify(userRepository, times(1)).save(user);
    }


    @Test
    void givenExistingUser_whenEditTheirProfileWithEmptyEmail_thenChangeTheirDetailsSaveNotificationPreferenceAndSaveToDatabase() {

        UUID userId = UUID.randomUUID();
        UserEditRequest dto = UserEditRequest.builder()
                .firstName("Viktor")
                .lastName("Aleksandrov")
                .email("")
                .profilePicture("www.image.com")
                .build();
        User user = User.builder().build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.editUserDetails(userId, dto);

        assertEquals("Viktor", user.getFirstName());
        assertEquals("Aleksandrov", user.getLastName());
        assertEquals("", user.getEmail());
        assertEquals("www.image.com", user.getProfilePicture());
        verify(userRepository, times(1)).save(user);
    }
}
