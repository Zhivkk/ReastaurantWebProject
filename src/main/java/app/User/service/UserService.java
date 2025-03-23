package app.User.service;

import app.APIMessage.MailClient;
import app.Security.UserInfo;
import app.exception.DomainException;
import app.User.model.User;
import app.User.model.UserRole;
import app.User.repository.UserRepository;
import app.web.dto.RegisterRequest;
import app.web.dto.UserEditRequest;
import app.web.dto.UserEditRequestAdmin;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final MailClient mailClient;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, MailClient mailClient, MailClient mailClient1) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mailClient = mailClient1;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new DomainException("User with this username does not exist."));
        return new UserInfo(user.getId(), username, user.getPassword(), user.getRole(), user.isActive());
    }


    @Transactional
    public User register(RegisterRequest registerRequest) {

        Optional<User> optionUser = userRepository.findByUsername(registerRequest.getUsername());
        if (optionUser.isPresent()) {
            throw new DomainException("Username [%s] already exist.".formatted(registerRequest.getUsername()));
        }

        User user = User.builder()
                .username(registerRequest.getUsername())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .phone(registerRequest.getPhone())
                .address(registerRequest.getAddress())
                .email(registerRequest.getEmail())
                .role(UserRole.CLIENT)
                .isActive(true)
                .createdOn(LocalDateTime.now())
                .updatedOn(LocalDateTime.now())
                .build();

                userRepository.save(user);
        log.info("Successfully create new user account for username [%s] and id [%s]".formatted(user.getUsername(), user.getId()));

//        //Изпращане на имейл за успешна регистрация
//        MailRequest mailRequest = MailRequest.builder()
//                .recipient(user.getEmail())
//                .subject("Account activation")
//                .body("Здравейте! Вие успешно се регистрирахте на сайта на ресторант Вистоди")
//                .build();
//        mailClient.sendMail(mailRequest);

        return user;
    }


    public User getById(UUID userId) {

        return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User with id [%s] does not exist.".formatted(userId)));
    }


    public UserEditRequest mapUserToUserEditRequest(User user) {

        return UserEditRequest.builder()
                .username( user.getUsername())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .email(user.getEmail())
                .profilePicture(user.getProfilePicture())
                .address(user.getAddress())
                .phone(user.getPhone())
                .build();
    }

    public void editUserDetails(UUID userId, UserEditRequest userEditRequest) {
        User user = getById(userId);

        user.setFirstName(userEditRequest.getFirstName());
        user.setLastName(userEditRequest.getLastName());
        user.setEmail(userEditRequest.getEmail());
        user.setProfilePicture(userEditRequest.getProfilePicture());
        user.setAddress(userEditRequest.getAddress());
        user.setPhone(userEditRequest.getPhone());
        user.setUpdatedOn(LocalDateTime.now());

        userRepository.save(user);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public UserEditRequestAdmin mapUserToUserEditRequestAdmin(User user) {

        return UserEditRequestAdmin.builder()
                .id(user.getId())
                .lastName(user.getLastName())
                .firstName(user.getFirstName())
                .email(user.getEmail())
                .address(user.getAddress())
                .phone(user.getPhone())
                .accountAmount(user.getAccountAmount())
                .role(user.getRole())
                .build();
    }

    public void editUserDetailsAdmin(UserEditRequestAdmin userEditRequestAdmin) {

        User user = getById(userEditRequestAdmin.getId());

        user.setFirstName(userEditRequestAdmin.getFirstName());
        user.setLastName(userEditRequestAdmin.getLastName());
        user.setEmail(userEditRequestAdmin.getEmail());
        user.setAddress(userEditRequestAdmin.getAddress());
        user.setPhone(userEditRequestAdmin.getPhone());
        user.setUpdatedOn(LocalDateTime.now());
        user.setAccountAmount(userEditRequestAdmin.getAccountAmount());
        user.setRole(userEditRequestAdmin.getRole());

        userRepository.save(user);
    }
}
