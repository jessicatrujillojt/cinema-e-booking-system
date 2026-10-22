package com.cinema.backend;

import com.cinema.backend.dto.UpdateProfileRequest;
import com.cinema.backend.model.User;
import com.cinema.backend.repository.UserRepository;
import com.cinema.backend.service.ProfileService;
import com.cinema.backend.service.ProfileNotificationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(ProfileService.class)
class ProfileIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProfileService profileService;

    private User testUser;

    @MockitoBean
    private ProfileNotificationService notificationService;

    @BeforeEach
    void setUp() {

        testUser = new User();
        testUser.setFirstName("Jane");
        testUser.setLastName("Doe");
        testUser.setEmail("profile-test@example.com");
        testUser.setPhoneNumber("5551234567");
        testUser.setAddress("123 Main Street");
        testUser.setCity("Athens");
        testUser.setState("GA");
        testUser.setZipCode("30601");
        testUser.setSubscribedToPromotions(false);

        testUser = userRepository.saveAndFlush(testUser);
    }

    private UpdateProfileRequest validUpdate() {

        UpdateProfileRequest request = new UpdateProfileRequest();

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
    void shouldRetrieveProfileFromH2() {

        User user = profileService.getProfile(testUser.getId());

        assertEquals("Jane", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("profile-test@example.com", user.getEmail());
        assertEquals("Athens", user.getCity());
    }

    @Test
    void shouldSaveProfileChangesInH2() {

        profileService.updateProfile(
                testUser.getId(), validUpdate());

        userRepository.flush();

        User saved = userRepository.findById(testUser.getId())
                .orElseThrow();

        assertEquals("Janet", saved.getFirstName());
        assertEquals("Smith", saved.getLastName());
        assertEquals("456 Oak Street", saved.getAddress());
        assertEquals("Atlanta", saved.getCity());
        assertEquals("30301", saved.getZipCode());
        assertTrue(saved.isSubscribedToPromotions());
    }

    @Test
    void shouldKeepEmailUnchanged() {

        profileService.updateProfile(
                testUser.getId(), validUpdate());

        userRepository.flush();

        User saved = userRepository.findById(testUser.getId())
                .orElseThrow();

        assertEquals(
                "profile-test@example.com",
                saved.getEmail());
    }

    @Test
    void shouldRejectInvalidProfileChanges() {

        UpdateProfileRequest request = validUpdate();
        request.setFirstName("");

        assertThrows(
                IllegalArgumentException.class,
                () -> profileService.updateProfile(
                        testUser.getId(), request));

        User saved = userRepository.findById(testUser.getId())
                .orElseThrow();

        assertEquals("Jane", saved.getFirstName());
    }
}