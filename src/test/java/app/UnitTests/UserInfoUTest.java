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

        UserInfo userInfo = new UserInfo(UUID.randomUUID(), "admin", "password", UserRole.ADMIN, true);


        Collection<? extends GrantedAuthority> authorities = userInfo.getAuthorities();


        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).isEqualTo("ROLE_ADMIN");
    }

    @Test
    void getAuthorities_UserRoleUser_ReturnsRoleUser() {

        UserInfo userInfo = new UserInfo(UUID.randomUUID(), "user", "password", UserRole.CLIENT, true);

        Collection<? extends GrantedAuthority> authorities = userInfo.getAuthorities();

        assertThat(authorities).hasSize(1);
        assertThat(authorities.iterator().next().getAuthority()).
                isEqualTo("ROLE_CLIENT");
    }

    @Test
    void isAccountNonExpired_ActiveUser_ReturnsTrue() {

        UserInfo userInfo = new UserInfo(UUID.randomUUID(), "user", "password", UserRole.CLIENT, true);

        assertTrue(userInfo.isAccountNonExpired());
    }

    @Test
    void isAccountNonExpired_InactiveUser_ReturnsFalse() {

        UserInfo userInfo = new UserInfo(UUID.randomUUID(), "user", "password", UserRole.CLIENT, false);

        assertFalse(userInfo.isAccountNonExpired());
    }

    @Test
    void isEnabled_ActiveUser_ReturnsTrue() {

        UserInfo userInfo = new UserInfo(UUID.randomUUID(), "user", "password", UserRole.CLIENT, true);

        assertTrue(userInfo.isEnabled());
    }

    @Test
    void isEnabled_InactiveUser_ReturnsFalse() {

        UserInfo userInfo = new UserInfo(UUID.randomUUID(), "user", "password", UserRole.CLIENT, false);

        assertFalse(userInfo.isEnabled());
    }
}

