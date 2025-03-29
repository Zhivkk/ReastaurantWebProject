package app.UnitTests;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import app.Security.UserInfo;
import app.User.model.UserRole;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

class UserInfoUTest {

    @Test
    void getAuthorities_UserRoleAdmin_ReturnsRoleAdmin() {
        // Arrange
        UserInfo userInfo = new UserInfo(UUID.randomUUID(), "admin", "password", UserRole.ADMIN, true);

        // Act
        Collection<? extends GrantedAuthority> authorities = userInfo.getAuthorities();

        // Assert
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void getAuthorities_UserRoleUser_ReturnsRoleUser() {
        // Arrange
        UserInfo userInfo = new UserInfo(UUID.randomUUID(), "user", "password", UserRole.CLIENT, true);

        // Act
        Collection<? extends GrantedAuthority> authorities = userInfo.getAuthorities();

        // Assert
        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).
                isEqualTo("ROLE_CLIENT");
    }

    @Test
    void isAccountNonExpired_ActiveUser_ReturnsTrue() {
        // Arrange
        UserInfo userInfo = new UserInfo(UUID.randomUUID(), "user", "password", UserRole.CLIENT, true);

        // Act & Assert
        assertTrue(userInfo.isAccountNonExpired());
    }

    @Test
    void isAccountNonExpired_InactiveUser_ReturnsFalse() {
        // Arrange
        UserInfo userInfo = new UserInfo(UUID.randomUUID(), "user", "password", UserRole.CLIENT, false);

        // Act & Assert
        assertFalse(userInfo.isAccountNonExpired());
    }

    @Test
    void isEnabled_ActiveUser_ReturnsTrue() {
        // Arrange
        UserInfo userInfo = new UserInfo(UUID.randomUUID(), "user", "password", UserRole.CLIENT, true);

        // Act & Assert
        assertTrue(userInfo.isEnabled());
    }

    @Test
    void isEnabled_InactiveUser_ReturnsFalse() {
        // Arrange
        UserInfo userInfo = new UserInfo(UUID.randomUUID(), "user", "password", UserRole.CLIENT, false);

        // Act & Assert
        assertFalse(userInfo.isEnabled());
    }
}

