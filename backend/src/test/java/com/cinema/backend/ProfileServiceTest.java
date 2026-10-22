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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    private UserRepository userRepository;
    private CustomerRepository customerRepository;
    private ProfileService profileService;
    private ProfileNotificationService notificationService;

    private User testUser;
    private Customer testCustomer;

    @BeforeEach
    void setUp() {

        userRepository = mock(UserRepository.class);
        customerRepository = mock(CustomerRepository.class);
        notificationService = mock(ProfileNotificationService.class);

        profileService = new ProfileService(
                userRepository,
                customerRepository,
                notificationService
        );

        testUser = new User();
        testUser.setUserId(1L);
        testUser.setFirstName("Jane");
        testUser.setLastName("Doe");
        testUser.setEmailAddress("jane@example.com");
        testUser.setPhoneNumber("5551234567");
        testUser.setAddress("123 Main Street");
        testUser.setCity("Athens");
        testUser.setState("GA");
        testUser.setZipCode("30601");

        testCustomer = new Customer();
        testCustomer.setUserId(1L);
        testCustomer.setPromotionSubscriptions(false);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(testUser));

        when(customerRepository.findById(1L))
                .thenReturn(Optional.of(testCustomer));

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0));

        when(customerRepository.save(any(Customer.class)))
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
        assertEquals(
                "jane@example.com",
                user.getEmailAddress()
        );
        assertEquals(
                "123 Main Street",
                user.getAddress()
        );
    }

    @Test
    void shouldUpdatePersonalInformation() {

        User updated =
                profileService.updateProfile(
                        1L,
                        validUpdate()
                );

        assertEquals("Janet", updated.getFirstName());
        assertEquals("Smith", updated.getLastName());
        assertEquals("5559876543", updated.getPhoneNumber());
        assertEquals("456 Oak Street", updated.getAddress());
        assertEquals("Atlanta", updated.getCity());
        assertEquals("30301", updated.getZipCode());

        assertTrue(testCustomer.isPromotionSubscriptions());

        verify(userRepository).save(testUser);
        verify(customerRepository).save(testCustomer);
    }

    @Test
    void shouldNotChangeEmailAddress() {

        String originalEmail =
                testUser.getEmailAddress();

        profileService.updateProfile(
                1L,
                validUpdate()
        );

        assertEquals(
                originalEmail,
                testUser.getEmailAddress()
        );
    }

    @Test
    void shouldRejectMissingFirstName() {

        UpdateProfileRequest request =
                validUpdate();

        request.setFirstName("");

        assertThrows(
                IllegalArgumentException.class,
                () -> profileService.updateProfile(
                        1L,
                        request
                )
        );

        verify(
                userRepository,
                never()
        ).save(any(User.class));
    }

    @Test
    void shouldRejectMissingLastName() {

        UpdateProfileRequest request =
                validUpdate();

        request.setLastName(" ");

        assertThrows(
                IllegalArgumentException.class,
                () -> profileService.updateProfile(
                        1L,
                        request
                )
        );

        verify(
                userRepository,
                never()
        ).save(any(User.class));
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

        profileService.updateProfile(
                1L,
                validUpdate()
        );

        verify(notificationService)
                .sendProfileUpdatedEmail(
                        "jane@example.com",
                        "Janet"
                );
    }

    @Test
    void shouldNotSendEmailWhenValidationFails() {

        UpdateProfileRequest request =
                validUpdate();

        request.setFirstName("");

        assertThrows(
                IllegalArgumentException.class,
                () -> profileService.updateProfile(
                        1L,
                        request
                )
        );

        verifyNoInteractions(notificationService);
    }
}