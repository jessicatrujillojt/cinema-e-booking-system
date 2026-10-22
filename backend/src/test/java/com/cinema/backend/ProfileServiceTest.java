package com.cinema.backend;

import com.cinema.backend.dto.UpdateProfileRequest;
import com.cinema.backend.model.User;
import com.cinema.backend.repository.UserRepository;
import com.cinema.backend.service.ProfileService;
import com.cinema.backend.service.ProfileNotificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;

class ProfileServiceTest {

    private UserRepository userRepository;
    private ProfileService profileService;
    private ProfileNotificationService notificationService;
    private User testUser;

    @BeforeEach
    void setUp() {

        userRepository = mock(UserRepository.class);
        notificationService = mock(ProfileNotificationService.class);

        profileService = new ProfileService(
                userRepository,
                notificationService
        );

        testUser = new User();
        testUser.setId(1L);
        testUser.setFirstName("Jane");
        testUser.setLastName("Doe");
        testUser.setEmail("jane@example.com");
        testUser.setPhoneNumber("5551234567");
        testUser.setAddress("123 Main Street");
        testUser.setCity("Athens");
        testUser.setState("GA");
        testUser.setZipCode("30601");
        testUser.setSubscribedToPromotions(false);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(testUser));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));
    }

    private UpdateProfileRequest validUpdate() {

        UpdateProfileRequest request =
                new UpdateProfileRequest();

        request.setFirstName("Janet");
        request.setLastName("Smith");
        request.setPhoneNumber("5559876543");
        request.setAddress("456 Oak Street");
        request.setCity("Atlanta");
        request.setState("GA");
        request.setZipCode("30301");
        request.setSubscribedToPromotions(true);

        return request;
    }

    @Test
    void shouldRetrieveExistingProfile() {

        User user = profileService.getProfile(1L);

        assertEquals("Jane", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("jane@example.com", user.getEmail());
        assertEquals("123 Main Street", user.getAddress());
    }

    @Test
    void shouldUpdatePersonalInformation() {

        User updated =
                profileService.updateProfile(1L, validUpdate());

        assertEquals("Janet", updated.getFirstName());
        assertEquals("Smith", updated.getLastName());
        assertEquals("5559876543", updated.getPhoneNumber());
        assertEquals("456 Oak Street", updated.getAddress());
        assertEquals("Atlanta", updated.getCity());
        assertEquals("30301", updated.getZipCode());
        assertTrue(updated.isSubscribedToPromotions());

        verify(userRepository).save(testUser);
    }

    @Test
    void shouldNotChangeEmailAddress() {

        String originalEmail = testUser.getEmail();

        profileService.updateProfile(1L, validUpdate());

        assertEquals(originalEmail, testUser.getEmail());
    }

    @Test
    void shouldRejectMissingFirstName() {

        UpdateProfileRequest request = validUpdate();
        request.setFirstName("");

        assertThrows(
                IllegalArgumentException.class,
                () -> profileService.updateProfile(1L, request)
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void shouldRejectMissingLastName() {

        UpdateProfileRequest request = validUpdate();
        request.setLastName(" ");

        assertThrows(
                IllegalArgumentException.class,
                () -> profileService.updateProfile(1L, request)
        );

        verify(userRepository, never())
                .save(any(User.class));
    }

    @Test
    void shouldRejectNonexistentUser() {

        when(userRepository.findById(99L))
                .thenReturn(Optional.empty());

        assertThrows(
                java.util.NoSuchElementException.class,
                () -> profileService.getProfile(99L)
        );
    }
    @Test
    void shouldSendEmailAfterProfileUpdate() {

        profileService.updateProfile(1L, validUpdate());

        verify(notificationService).sendProfileUpdatedEmail(
                "jane@example.com",
                "Janet"
        );
    }

    @Test
    void shouldNotSendEmailWhenValidationFails() {

        UpdateProfileRequest request = validUpdate();
        request.setFirstName("");

        assertThrows(
                IllegalArgumentException.class,
                () -> profileService.updateProfile(1L, request)
        );

        verifyNoInteractions(notificationService);
    }
}