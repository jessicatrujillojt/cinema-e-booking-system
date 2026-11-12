package com.cinema.backend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime;

    // User can be null until login
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // Random ownership token, not the HTTP session ID
    @Column(name = "booking_owner_token",
            nullable = false,
            length = 36)
    private String bookingOwnerToken;

    @Column(name = "checkout_email", length = 255)
    private String checkoutEmail;

    @Column(nullable = false)
    private int adultTickets;

    @Column(nullable = false)
    private int childTickets;

    @Column(nullable = false)
    private int seniorTickets;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.IN_PROGRESS;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    public Booking() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Showtime getShowtime() {
        return showtime;
    }

    public void setShowtime(Showtime showtime) {
        this.showtime = showtime;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getBookingOwnerToken() {
        return bookingOwnerToken;
    }

    public void setBookingOwnerToken(String bookingOwnerToken) {
        this.bookingOwnerToken = bookingOwnerToken;
    }

    public int getAdultTickets() {
        return adultTickets;
    }

    public void setAdultTickets(int adultTickets) {
        this.adultTickets = adultTickets;
    }

    public int getChildTickets() {
        return childTickets;
    }

    public void setChildTickets(int childTickets) {
        this.childTickets = childTickets;
    }

    public int getSeniorTickets() {
        return seniorTickets;
    }

    public void setSeniorTickets(int seniorTickets) {
        this.seniorTickets = seniorTickets;
    }

    public int getTotalTickets() {
        return adultTickets + childTickets + seniorTickets;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public String getCheckoutEmail() {
        return checkoutEmail;
    }

    public void setCheckoutEmail(String checkoutEmail) {
        this.checkoutEmail = checkoutEmail;
    }
}
