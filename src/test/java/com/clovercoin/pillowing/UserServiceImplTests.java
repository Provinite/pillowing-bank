package com.clovercoin.pillowing;

import com.clovercoin.pillowing.entity.Role;
import com.clovercoin.pillowing.entity.User;
import com.clovercoin.pillowing.forms.UserAddForm;
import com.clovercoin.pillowing.repository.RoleRepository;
import com.clovercoin.pillowing.repository.UserRepository;
import com.clovercoin.pillowing.service.NoSuchRoleException;
import com.clovercoin.pillowing.service.UserServiceImpl;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceImplTests {

    private UserServiceImpl userService;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    private static final String hashedPassword = "L00k@meallBcrypted";

    private void initUserService() {
        userService = new UserServiceImpl(userRepository, roleRepository, bCryptPasswordEncoder);
    }

    @Before
    public void setup() {
        roleRepository = mock(RoleRepository.class);

        when(roleRepository.findByRole(anyString())).thenAnswer(i -> {
            Role r = new Role();
            r.setRole(i.getArgumentAt(0, String.class));
            return r;
        });
        when(roleRepository.save(any(Role.class))).thenAnswer(i -> i.getArgumentAt(0, Role.class));

        userRepository = mock(UserRepository.class);
        when(userRepository.save(any(User.class))).thenAnswer(i -> i.getArgumentAt(0, User.class));
        when(userRepository.findByEmail(anyString())).thenAnswer(i -> {
            User user = new User();
            user.setEmail(i.getArgumentAt(0, String.class));
            return user;
        });

        bCryptPasswordEncoder = mock(BCryptPasswordEncoder.class);
        when(bCryptPasswordEncoder.encode(any(CharSequence.class))).thenReturn(hashedPassword);

        initUserService();
    }

    @Test
    public void CreateUserFromForm_Should_CreateUser_When_GivenValidForm() {
        String test_username = "test_username";
        String test_password = "test_password";
        String test_email = "test@email.com";
        UserAddForm form = new UserAddForm();
        form.setUsername(test_username);
        form.setPassword(test_password);
        form.setEmail(test_email);
        form.setIsAdmin(true);
        form.setIsMod(true);
        form.setIsGuestArtist(false);
        form.setIsActive(true);

        User user = userService.createUserFromForm(form);
        assertThat(user.getId()).isNull();
        assertThat(user.getUsername()).isEqualTo(test_username);
        assertThat(user.getPassword()).isEqualTo(test_password);
        assertThat(user.getEmail()).isEqualTo(test_email);

        assertThat(user.getRoles())
                .withFailMessage("Role list must not be null").isNotNull()
                .withFailMessage("Role list should not be empty under the given conditions").isNotEmpty();

        List<String> roles = user.getRoles().stream().map(Role::getRole).collect(Collectors.toList());

        assertThat(roles).contains(SecurityConfiguration.ADMIN_ROLE, SecurityConfiguration.MOD_ROLE);
        assertThat(roles).doesNotContain(SecurityConfiguration.GUEST_ARTIST_ROLE, SecurityConfiguration.CLIENT_ROLE);
    }

    @Test
    public void AddRole_Should_AddRole_When_UserRolesIsNull() {
        String expectedRoleName = "expected_role";
        User user = new User();
        user.setRoles(null);
        userService.addRole(user, expectedRoleName);
        assertThat(user.getRoles())
                .withFailMessage("User should have a non null roles set").isNotNull()
                .withFailMessage("User should have a non empty roles set").isNotEmpty();
        assertThat(user
                        .getRoles()
                        .stream()
                        .map(Role::getRole)
                        .collect(Collectors.toList()))
                .withFailMessage("User should have expected role").contains(expectedRoleName);
    }

    @Test
    public void AddRole_Should_AddRole_When_UserDoesntHaveRoleAndRoleExists() {
        String expectedRoleName = "expected_role";
        User user = new User();
        userService.addRole(user, expectedRoleName);

        assertThat(user
                        .getRoles()
                        .stream()
                        .map(Role::getRole)
                        .collect(Collectors.toList()))
                .withFailMessage("User should have expected role").contains(expectedRoleName);
    }

    @Test
    public void AddRole_Should_AddRole_When_UserRolesIsUnwmodifiableSet() {
        String expectedRoleName = "expected_role";
        User user = new User();
        user.setRoles(Collections.unmodifiableSet(new HashSet<>()));
        userService.addRole(user, expectedRoleName);

        assertThat(user
                .getRoles()
                .stream()
                .map(Role::getRole)
                .collect(Collectors.toList()))
                .withFailMessage("User should have expected role").contains(expectedRoleName);
    }

    @Test(expected = NoSuchRoleException.class)
    public void AddRole_Should_ThrowRoleNotFoundException_When_RoleDoesNotExist() {
        roleRepository = mock(RoleRepository.class);
        when(roleRepository.findByRole(anyString())).thenReturn(null);

        initUserService();

        User user = new User();
        userService.addRole(user, "noneexistantrole");
    }

    @Test(expected = IllegalArgumentException.class)
    public void AddRole_Should_ThrowIllegalArgumentException_When_NullUser() {
        userService.addRole(null, "somerole");
    }

    @Test(expected = IllegalArgumentException.class)
    public void AddRole_Should_ThrowIllegalArgumentException_When_NullRole() {
        userService.addRole(new User(), null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void CreateUser_Should_ThrowIllegalArgumentException_When_NullUser() {
        userService.saveUser(null, false);
    }

    @Test
    public void SaveUser_Should_HashPassword_When_ChangedPasswordIsTrue() {
        User user = new User();
        user.setPassword("top$ecret");
        user = userService.saveUser(user, true);

        assertThat(user.getPassword()).isEqualTo(hashedPassword);
    }

    @Test
    public void SaveUser_Should_NotHashPassword_When_ChangedPasswordIsFalse() {
        String password = "middle$ecret";

        User user = new User();
        user.setPassword(password);
        user = userService.saveUser(user, false);

        assertThat(user.getPassword()).isEqualTo(password);
    }

    @Test
    public void CreateUser_Should_HashPassword_When_GivenAnyUser() {
        User user = new User();
        user.setPassword("bottom$ecret");
        user = userService.createUser(user);

        assertThat(user.getPassword()).isEqualTo(hashedPassword);
    }

    @Test
    @WithMockUser(username = "admin@foo.com")
    public void GetCurrentUser_Should_ReturnCurrentUser_When_LoggedIn() {
        when(userRepository.findByEmail("admin@foo.com")).then(i -> {
           User user = new User();
           user.setId(1L);
           user.setUsername("something");
           user.setEmail("admin@foo.com");
           return user;
        });

        User currentUser = userService.getCurrentUser();
        assertThat(currentUser).isNotNull();
        assertThat(currentUser.getUsername()).isEqualTo("something");
        assertThat(currentUser.getEmail()).isEqualTo("admin@foo.com");
    }

    @Test
    public void GetCurrentUser_Should_ReturnNull_When_NotLoggedIn() {
        assertThat(userService.getCurrentUser()).isNull();
    }

    @Test(expected = DuplicateKeyException.class)
    public void SaveUser_Should_ThrowDuplicateKeyException_When_SaveThrowsDataIntegrityViolationException() {
        when(userRepository.save(any(User.class))).thenThrow(new DataIntegrityViolationException("", new ConstraintViolationException("",null,null)));
        User user = new User();
        userService.createUser(user);
    }

    @Test
    public void FindUserByEmail_Should_ReturnNull_When_NoUserWithGivenEmail() {
        when(userRepository.findByEmail(anyString())).thenReturn(null);
        assertThat(userService.findUserByEmail("anything@anywhere.com")).isNull();
    }

    @Test
    public void FindUserByEmail_Should_ReturnUser_When_UserWithEmailExists() {
        User expected = new User();
        expected.setUsername("somebody");
        expected.setEmail("anything@anywhere.com");
        expected.setActive(1);

        when(userRepository.findByEmail(expected.getEmail())).thenReturn(expected);

        assertThat(userService.findUserByEmail(expected.getEmail())).isEqualTo(expected);
    }

}
