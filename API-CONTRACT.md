\# Sprint 2 API Contract

Defines how frontend communicates with backend for Sprint 2.

FE uses endpoints and formats listed below, and BE must notify FE when a change is made.

\## Status Key

\- PLANNED = not implemented yet  
\- IN PROGRESS = backend is currently being built  
\- READY FOR FE = frontend can safely connect to this endpoint

\---

\## Authentication

\### Register User

\*\*Status:\*\* READY FOR FE

\*\*Method:\*\* POST

\*\*Endpoint:\*\*

\`\`\`text
/api/auth/register
\`\`\`

\*\*Frontend Sends:\*\*

\`\`\`json
{
  "firstName": "Eman",
  "lastName": "Saleh",
  "emailAddress": "eman@gmail.com",
  "phoneNumber": "7065551234",
  "password": "MyPass!20",
  "promotionSubscriptions": true
}
\`\`\`

\*\*Successful Response:\*\*

\`\`\`json
{
  "success": true,
  "message": "Account created successfully. Please verify your account.",
  "userId": 1
}
\`\`\`

\*\*Backend Behavior:\*\*

\- Account is created.  
\- Password is hashed with BCrypt before being stored.  
\- A matching Customer record is created.  
\- Customer status is set to INACTIVE.  
\- A unique verification token is generated.  
\- User is not automatically logged in.  
\- Automatic verification email delivery is still in progress.

\*\*Possible Error:\*\*

\`\`\`json
{
  "success": false,
  "message": "An account with this email already exists."
}
\`\`\`

\---

\### Verify Account

\*\*Status:\*\* READY FOR FE

\*\*Method:\*\* GET

\*\*Endpoint:\*\*

\`\`\`text
/api/auth/verify?token={verificationToken}
\`\`\`

\*\*Successful Response:\*\*

\`\`\`json
{
  "success": true,
  "message": "Account verified successfully."
}
\`\`\`

\*\*Backend Behavior:\*\*

\- Finds the customer associated with the verification token.  
\- Changes customer status from INACTIVE to ACTIVE.  
\- Removes the verification token after successful verification.  
\- User can log in after successful verification.  
\- Automatic verification email delivery is still in progress.

\*\*Possible Error:\*\*

\`\`\`json
{
  "success": false,
  "message": "Invalid verification token."
}
\`\`\`

\---

\### Login

\*\*Status:\*\* READY FOR FE

\*\*Method:\*\* POST

\*\*Endpoint:\*\*

\`\`\`text
/api/auth/login
\`\`\`

\*\*Frontend Sends:\*\*

\`\`\`json
{
  "emailAddress": "eman@gmail.com",
  "password": "MyPass!20"
}
\`\`\`

\*\*Successful Response:\*\*

\`\`\`json
{
  "success": true,
  "message": "Login successful.",
  "userId": 1,
  "role": "CUSTOMER"
}
\`\`\`

\*\*Backend Behavior:\*\*

\- Looks up the user by email address.  
\- Compares the entered password to the stored BCrypt hash.  
\- Rejects invalid email/password combinations.  
\- Rejects customer accounts that are not ACTIVE.  
\- Returns the user role as CUSTOMER or ADMIN.  
\- Creates a server session for the logged-in user.  
\- Stores userId, emailAddress, and role in the session.

\*\*Frontend Requirement:\*\*

Requests that depend on the logged-in session should include:

\`\`\`javascript
credentials: "include"
\`\`\`

\*\*Possible Errors:\*\*

\`\`\`json
{
  "success": false,
  "message": "Invalid email or password."
}
\`\`\`

\`\`\`json
{
  "success": false,
  "message": "Account is not verified. Please verify your account before logging in."
}
\`\`\`

\---

\### Logout

\*\*Status:\*\* READY FOR FE

\*\*Method:\*\* POST

\*\*Endpoint:\*\*

\`\`\`text
/api/auth/logout
\`\`\`

\*\*Successful Response:\*\*

\`\`\`json
{
  "success": true,
  "message": "Logout successful."
}
\`\`\`

\*\*Backend Behavior:\*\*

\- Invalidates the current server session.  
\- User is no longer considered logged in.  
\- Protected endpoints return 401 Unauthorized after logout.

\*\*Frontend Requirement:\*\*

\`\`\`javascript
credentials: "include"
\`\`\`

\---

\### Check Session

\*\*Status:\*\* READY FOR FE

\*\*Method:\*\* GET

\*\*Endpoint:\*\*

\`\`\`text
/api/auth/session
\`\`\`

\*\*Successful Response When Logged In:\*\*

\`\`\`json
{
  "loggedIn": true,
  "userId": 1,
  "role": "CUSTOMER"
}
\`\`\`

\*\*Successful Response When Logged Out:\*\*

\`\`\`json
{
  "loggedIn": false
}
\`\`\`

\*\*Backend Behavior:\*\*

\- Checks the current server session.  
\- Returns whether a user is logged in.  
\- Returns the logged-in user's ID and role when a valid session exists.

\*\*Frontend Requirement:\*\*

\`\`\`javascript
credentials: "include"
\`\`\`

\---

\### Forgot Password

\*\*Status:\*\* READY FOR FE

\*\*Method:\*\* POST

\*\*Endpoint:\*\*

\`\`\`text
/api/auth/forgot-password
\`\`\`

\*\*Frontend Sends:\*\*

\`\`\`json
{
  "emailAddress": "eman@gmail.com"
}
\`\`\`

\*\*Backend Behavior:\*\*

\- Finds the user by email address.  
\- Generates a unique password reset token.  
\- Stores the reset token with the user.  
\- Password reset email delivery is still in progress.  
\- Reset token may currently be returned during development/testing.

\*\*Possible Error:\*\*

\`\`\`json
{
  "success": false,
  "message": "User not found."
}
\`\`\`

\---

\### Reset Password

\*\*Status:\*\* READY FOR FE

\*\*Method:\*\* POST

\*\*Endpoint:\*\*

\`\`\`text
/api/auth/reset-password
\`\`\`

\*\*Frontend Sends:\*\*

\`\`\`json
{
  "token": "reset-token-value",
  "newPassword": "NewPassword456!"
}
\`\`\`

\*\*Successful Response:\*\*

\`\`\`json
{
  "success": true,
  "message": "Password reset successfully."
}
\`\`\`

\*\*Backend Behavior:\*\*

\- Finds the user using the password reset token.  
\- Hashes the new password using BCrypt.  
\- Replaces the existing password hash.  
\- Removes the reset token after a successful reset.

\*\*Possible Error:\*\*

\`\`\`json
{
  "success": false,
  "message": "Invalid reset token."
}
\`\`\`

\---

\### Change Password

\*\*Status:\*\* READY FOR FE

\*\*Method:\*\* POST

\*\*Endpoint:\*\*

\`\`\`text
/api/auth/change-password
\`\`\`

\*\*Frontend Sends:\*\*

\`\`\`json
{
  "currentPassword": "Password123!",
  "newPassword": "NewPassword456!"
}
\`\`\`

\*\*Successful Response:\*\*

\`\`\`json
{
  "success": true,
  "message": "Password changed successfully."
}
\`\`\`

\*\*Possible Errors:\*\*

\`\`\`json
{
  "success": false,
  "message": "You must be logged in to change your password."
}
\`\`\`

\`\`\`json
{
  "success": false,
  "message": "Current password is incorrect."
}
\`\`\`

\*\*Backend Behavior:\*\*

\- Requires an active logged-in session.  
\- Uses the current session's userId.  
\- Verifies the user's current password using BCrypt.  
\- Hashes the new password before saving it.  
\- Old password stops working after the change.

\*\*Frontend Requirement:\*\*

\`\`\`javascript
credentials: "include"
\`\`\`

\---

\## Profile

\### View Profile

\*\*Status:\*\* READY FOR FE

\*\*Method:\*\* GET

\*\*Endpoint:\*\*

\`\`\`text
/api/profile
\`\`\`

\*\*Authentication Required:\*\* Yes

\*\*Successful Response:\*\*

\`\`\`json
{
  "id": 1,
  "firstName": "Eman",
  "lastName": "Saleh",
  "email": "eman@gmail.com",
  "phoneNumber": "7065551234",
  "address": "123 Main Street",
  "city": "Athens",
  "state": "GA",
  "zipCode": "30601",
  "subscribedToPromotions": true
}
\`\`\`

\*\*Backend Behavior:\*\*

\- Uses the userId stored in the logged-in session.  
\- Retrieves the User profile.  
\- Retrieves promotion subscription status from the matching Customer record.  
\- Returns the logged-in user's profile.

\*\*Frontend Requirement:\*\*

\`\`\`javascript
credentials: "include"
\`\`\`

\*\*Possible Error:\*\*

\`\`\`text
401 Unauthorized
\`\`\`

\---

\### Update Profile

\*\*Status:\*\* READY FOR FE

\*\*Method:\*\* PUT

\*\*Endpoint:\*\*

\`\`\`text
/api/profile
\`\`\`

\*\*Authentication Required:\*\* Yes

\*\*Frontend Sends:\*\*

\`\`\`json
{
  "firstName": "Eman",
  "lastName": "Saleh",
  "phoneNumber": "7065554321",
  "address": "456 College Avenue",
  "city": "Athens",
  "state": "GA",
  "zipCode": "30601",
  "subscribedToPromotions": true
}
\`\`\`

\*\*Successful Response:\*\*

\`\`\`json
{
  "id": 1,
  "firstName": "Eman",
  "lastName": "Saleh",
  "email": "eman@gmail.com",
  "phoneNumber": "7065554321",
  "address": "456 College Avenue",
  "city": "Athens",
  "state": "GA",
  "zipCode": "30601",
  "subscribedToPromotions": true
}
\`\`\`

\*\*Backend Behavior:\*\*

\- Requires a logged-in session.  
\- Updates first name, last name, phone number, and address information.  
\- Updates promotion subscription status in the Customer record.  
\- Email address is not changed by this endpoint.  
\- Password is not changed by this endpoint.  
\- Role and account status are not changed.  
\- Profile update confirmation email is sent when email delivery is configured.

\*\*Frontend Requirement:\*\*

\`\`\`javascript
credentials: "include"
\`\`\`

\*\*Possible Errors:\*\*

\`\`\`text
401 Unauthorized
\`\`\`

\`\`\`text
First name is required
\`\`\`

\`\`\`text
Last name is required
\`\`\`

\---

\## Payment Cards

\### View Saved Payment Cards

\*\*Status:\*\* READY FOR FE

\*\*Method:\*\* GET

\*\*Endpoint:\*\*

\`\`\`text
/api/payment-cards
\`\`\`

\*\*Authentication Required:\*\* Yes

\*\*Backend Behavior:\*\*

\- Uses the userId stored in the logged-in session.  
\- Returns only payment cards belonging to the logged-in user.  
\- Full card numbers are never returned to the frontend.  
\- Safe card information such as the last four digits is returned.

\*\*Frontend Requirement:\*\*

\`\`\`javascript
credentials: "include"
\`\`\`

\*\*Possible Error:\*\*

\`\`\`text
401 Unauthorized
\`\`\`

\---

\### Add Payment Card

\*\*Status:\*\* READY FOR FE

\*\*Method:\*\* POST

\*\*Endpoint:\*\*

\`\`\`text
/api/payment-cards
\`\`\`

\*\*Authentication Required:\*\* Yes

\*\*Frontend Sends:\*\*

\`\`\`json
{
  "cardholderName": "Eman Saleh",
  "cardNumber": "4111111111111111",
  "expirationMonth": 12,
  "expirationYear": 2028
}
\`\`\`

\*\*Backend Behavior:\*\*

\- Requires a logged-in session.  
\- Users may save a maximum of 3 payment cards.  
\- Validates the card number.  
\- Validates the expiration date.  
\- Encrypts the full card number before saving it.  
\- Stores the last four digits separately for display.  
\- Associates the card with the logged-in user.

\*\*Frontend Requirement:\*\*

\`\`\`javascript
credentials: "include"
\`\`\`

\*\*Possible Errors:\*\*

\`\`\`text
401 Unauthorized
\`\`\`

\`\`\`text
You cannot save more than 3 payment cards
\`\`\`

\`\`\`text
Card number must contain 13 to 19 digits
\`\`\`

\`\`\`text
Payment card has expired
\`\`\`

\---

\### Update Payment Card

\*\*Status:\*\* READY FOR FE

\*\*Method:\*\* PUT

\*\*Endpoint:\*\*

\`\`\`text
/api/payment-cards/{cardId}
\`\`\`

\*\*Authentication Required:\*\* Yes

\*\*Frontend Sends:\*\*

\`\`\`json
{
  "cardholderName": "Eman Saleh",
  "cardNumber": "4111111111111111",
  "expirationMonth": 10,
  "expirationYear": 2029
}
\`\`\`

\*\*Backend Behavior:\*\*

\- Requires a logged-in session.  
\- User may only update a payment card belonging to their own account.  
\- Validates the new card information.  
\- Encrypts the card number before storage.  
\- Updates the last four digits used for display.

\*\*Frontend Requirement:\*\*

\`\`\`javascript
credentials: "include"
\`\`\`

\*\*Possible Errors:\*\*

\`\`\`text
401 Unauthorized
\`\`\`

\`\`\`text
Card not found
\`\`\`

\---

\### Delete Payment Card

\*\*Status:\*\* READY FOR FE

\*\*Method:\*\* DELETE

\*\*Endpoint:\*\*

\`\`\`text
/api/payment-cards/{cardId}
\`\`\`

\*\*Authentication Required:\*\* Yes

\*\*Successful Response:\*\*

\`\`\`text
204 No Content
\`\`\`

\*\*Backend Behavior:\*\*

\- Requires a logged-in session.  
\- User may only delete a card belonging to their own account.  
\- Deletes the selected payment card.

\*\*Frontend Requirement:\*\*

\`\`\`javascript
credentials: "include"
\`\`\`

\---

\## Favorites

\### View Favorites

\*\*Status:\*\* READY FOR FE

\*\*Method:\*\* GET

\*\*Endpoint:\*\*

\`\`\`text
/api/favorites
\`\`\`

\*\*Authentication Required:\*\* Yes

\*\*Backend Behavior:\*\*

\- Uses the userId stored in the logged-in session.  
\- Retrieves all favorite movies belonging to the logged-in user.  
\- Returns the Movie objects associated with the user's favorites.

\*\*Frontend Requirement:\*\*

\`\`\`javascript
credentials: "include"
\`\`\`

\*\*Possible Error:\*\*

\`\`\`text
401 Unauthorized
\`\`\`

\---

\### Add Favorite

\*\*Status:\*\* READY FOR FE

\*\*Method:\*\* POST

\*\*Endpoint:\*\*

\`\`\`text
/api/favorites/{movieId}
\`\`\`

\*\*Authentication Required:\*\* Yes

\*\*Successful Response:\*\*

\`\`\`text
201 Created
\`\`\`

\*\*Backend Behavior:\*\*

\- Requires a logged-in session.  
\- Finds the requested movie.  
\- Associates the movie with the logged-in user.  
\- Prevents duplicate favorite records.

\*\*Frontend Requirement:\*\*

\`\`\`javascript
credentials: "include"
\`\`\`

\*\*Possible Errors:\*\*

\`\`\`text
401 Unauthorized
\`\`\`

\`\`\`text
Movie not found
\`\`\`

\---

\### Remove Favorite

\*\*Status:\*\* READY FOR FE

\*\*Method:\*\* DELETE

\*\*Endpoint:\*\*

\`\`\`text
/api/favorites/{movieId}
\`\`\`

\*\*Authentication Required:\*\* Yes

\*\*Successful Response:\*\*

\`\`\`text
204 No Content
\`\`\`

\*\*Backend Behavior:\*\*

\- Requires a logged-in session.  
\- Removes the selected movie from the logged-in user's favorites.

\*\*Frontend Requirement:\*\*

\`\`\`javascript
credentials: "include"
\`\`\`

\*\*Possible Errors:\*\*

\`\`\`text
401 Unauthorized
\`\`\`

\`\`\`text
Movie is not in favorites
\`\`\`

\---

\## Movies

\### View Movies

\*\*Status:\*\* READY FOR FE

\*\*Method:\*\* GET

\*\*Endpoint:\*\*

\`\`\`text
/api/movies
\`\`\`

\*\*Authentication Required:\*\* No

\*\*Backend Behavior:\*\*

\- Returns available movie data.  
\- Movie browsing is public.  
\- Users do not need to be logged in to view movies.  
\- Movie endpoint is allowed through the Spring Security configuration.

\*\*Successful Response Example:\*\*

\`\`\`json
[
  {
    "id": 1,
    "title": "Resident Evil",
    "genre": "Action",
    "rating": "R",
    "description": "Movie description",
    "posterUrl": "poster-url",
    "trailerUrl": "trailer-url",
    "status": "Currently Running",
    "showtimes": [
      "2:00 PM",
      "5:00 PM",
      "8:00 PM"
    ]
  }
]
\`\`\`

\---

\## Admin

\### Check Admin Access

\*\*Status:\*\* READY FOR FE

\*\*Method:\*\* GET

\*\*Endpoint:\*\*

\`\`\`text
/api/admin/check
\`\`\`

\*\*Authentication Required:\*\* Yes

\*\*Backend Behavior:\*\*

\- Checks the current server session.  
\- Checks the role stored in the session.  
\- Admin access is allowed only when the logged-in user's role is ADMIN.  
\- Customer users should not be treated as administrators.

\*\*Frontend Requirement:\*\*

\`\`\`javascript
credentials: "include"
\`\`\`

\---

\## Session and CORS Requirements

Frontend and backend run on different local ports during development.

Backend:

\`\`\`text
http://localhost:8080
\`\`\`

Frontend Live Server:

\`\`\`text
http://localhost:5501
\`\`\`

or

\`\`\`text
http://127.0.0.1:5501
\`\`\`

The backend SecurityConfig allows these frontend origins.

For endpoints requiring authentication, frontend fetch requests must include:

\`\`\`javascript
credentials: "include"
\`\`\`

This allows the browser to send the JSESSIONID cookie created by the backend login session.

Public movie endpoints do not require an authenticated session.

Protected endpoints such as the following require a valid session:

\- `/api/profile`  
\- `/api/payment-cards`  
\- `/api/favorites`  
\- `/api/admin/check`

Authentication endpoints such as registration, login, verification, forgot password, and reset password are publicly accessible so users can complete the authentication process.