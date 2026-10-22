package com.cinema.backend;

import com.cinema.backend.dto.UpdateProfileRequest;
import com.cinema.backend.model.Customer;
import com.cinema.backend.model.User;
import com.cinema.backend.repository.CustomerRepository;
import com.cinema.backend.repository.UserRepository;
import com.cinema.backend.service.ProfileNotificationService;
import com.cinema.backend.service.ProfileService;

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
    private CustomerRepository customerRepository;

    @Autowired
    private ProfileService profileService;

    private User testUser;
    private Customer testCustomer;

    @MockitoBean
    private ProfileNotificationService notificationService;

    @BeforeEach
    void setUp() {

        testUser = new User();
        testUser.setFirstName("Jane");
        testUser.setLastName("Doe");
        testUser.setEmailAddress("profile-test@example.com");
        testUser.setPassword("test-password");
        testUser.setPhoneNumber("5551234567");
        testUser.setAddress("123 Main Street");
        testUser.setCity("Athens");
        testUser.setState("GA");
        testUser.setZipCode("30601");

        testUser = userRepository.saveAndFlush(testUser);

        testCustomer = new Customer();
        testCustomer.setUserId(testUser.getUserId());
        testCustomer.setStatus("ACTIVE");
        testCustomer.setPromotionSubscriptions(false);

        testCustomer = customerRepository.saveAndFlush(testCustomer);
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

        User user =
                profileService.getProfile(
                        testUser.getUserId());

        assertEquals(
                "Jane",
                user.getFirstName());

        assertEquals(
                "Doe",
                user.getLastName());

        assertEquals(
                "profile-test@example.com",
                user.getEmailAddress());

        assertEquals(
                "Athens",
                user.getCity());
    }

    @Test
    void shouldSaveProfileChangesInH2() {

        profileService.updateProfile(
                testUser.getUserId(),
                validUpdate());

        userRepository.flush();
        customerRepository.flush();

        User saved =
                userRepository
                        .findById(testUser.getUserId())
                        .orElseThrow();

        Customer savedCustomer =
                customerRepository
                        .findById(testUser.getUserId())
                        .orElseThrow();

        assertEquals(
                "Janet",
                saved.getFirstName());

        assertEquals(
                "Smith",
                saved.getLastName());

        assertEquals(
                "456 Oak Street",
                saved.getAddress());

        assertEquals(
                "Atlanta",
                saved.getCity());

        assertEquals(
                "30301",
                saved.getZipCode());

        assertTrue(
                savedCustomer.isPromotionSubscriptions());
    }

    @Test
    void shouldKeepEmailUnchanged() {

        profileService.updateProfile(
                testUser.getUserId(),
                validUpdate());

        userRepository.flush();

        User saved =
                userRepository
                        .findById(testUser.getUserId())
                        .orElseThrow();

        assertEquals(
                "profile-test@example.com",
                saved.getEmailAddress());
    }

    @Test
    void shouldRejectInvalidProfileChanges() {

        UpdateProfileRequest request =
                validUpdate();

        request.setFirstName("");

        assertThrows(
                IllegalArgumentException.class,
                () ->
                        profileService.updateProfile(
                                testUser.getUserId(),
                                request));

        User saved =
                userRepository
                        .findById(testUser.getUserId())
                        .orElseThrow();

        assertEquals(
                "Jane",
                saved.getFirstName());
    }
}