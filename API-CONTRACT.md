# Sprint 2 API Contract

Defines how frontend communicates with backend for Sprint 2.

FE uses endpoints and formats listend, and BE must notify FE when a change is made. 


## Status Key

- PLANNED = not implemented yet
- IN PROGRESS = backend is currently being built
- READY FOR FE = frontend can safely connect to this endpoint

## Authentication

### Register User

**Status:** IN PROGRESS

**Method:** POST

**Endpoint:**

```text
/api/auth/register

**Frontend Sends:**

{
  "firstName": "Eman",
  "lastName": "Saleh",
  "emailAddress": "eman@gmail.com",
  "phoneNumber": "7065551234",
  "password": "MyPass!20",
  "promotionSubscriptions": true
}

**Successful Response:**

{
  "success": true,
  "message": "Account created successfully. Please verify your account.",
  "userId": 1
}

**Backend Behavior:**

- Account is created.
- Password is hashed before being stored.
- A matching Customer record is created.
- Customer status is set to INACTIVE.
- A unique verification token is generated.
- User is not automatically logged in.
- Verification email sending is still in progress.

**Possible Error:**

{
  "success": false,
  "message": "An account with this email already exists."
}

---

### Verify Account

**Status:** IN PROGRESS

**Method:** GET

**Endpoint:**

/api/auth/verify?token={verificationToken}

**Successful Response:**

{
  "success": true,
  "message": "Account verified successfully."
}

**Backend Behavior:**

- Finds the customer associated with the verification token.
- Changes customer status from INACTIVE to ACTIVE.
- Removes the verification token after successful verification.

**Possible Error:**

{
  "success": false,
  "message": "Invalid verification token."
}

### Login

**Status:** IN PROGRESS

**Method:** POST

**Endpoint:**

/api/auth/login

**Frontend Sends:**

{
  "emailAddress": "eman@gmail.com",
  "password": "MyPass!20"
}

**Successful Response:**

{
  "success": true,
  "message": "Login successful.",
  "userId": 1,
  "role": "CUSTOMER"
}

**Backend Behavior:**

- Looks up the user by email address.
- Compares the entered password to the stored BCrypt hash.
- Rejects invalid email/password combinations.
- Rejects customer accounts that are not ACTIVE.
- Returns the user role as CUSTOMER or ADMIN.
- Creates a server session for the logged-in user.

**Possible Errors:**

{
  "success": false,
  "message": "Invalid email or password."
}

{
  "success": false,
  "message": "Account is not verified. Please verify your account before logging in."
}

---

### Logout

**Status:** IN PROGRESS

**Method:** POST

**Endpoint:**

/api/auth/logout

**Successful Response:**

{
  "success": true,
  "message": "Logout successful."
}

**Backend Behavior:**

- Invalidates the current server session.
- User is no longer considered logged in.

---

### Check Session

**Status:** IN PROGRESS

**Method:** GET

**Endpoint:**

/api/auth/session

**Successful Response When Logged In:**

{
  "loggedIn": true,
  "userId": 1,
  "role": "CUSTOMER"
}

**Successful Response When Logged Out:**

{
  "loggedIn": false
}

**Backend Behavior:**

- Checks the current server session.
- Returns whether a user is logged in.
- Returns the logged-in user's ID and role when a valid session exists.

### Change Password

**Status:** IN PROGRESS

**Method:** POST

**Endpoint:**

/api/auth/change-password

**Frontend Sends:**

{
  "currentPassword": "Password123!",
  "newPassword": "NewPassword456!"
}

**Successful Response:**

{
  "success": true,
  "message": "Password changed successfully."
}

**Possible Errors:**

{
  "success": false,
  "message": "You must be logged in to change your password."
}

{
  "success": false,
  "message": "Current password is incorrect."
}

**Backend Behavior:**

- Requires an active logged-in session.
- Uses the current session's userId.
- Verifies the user's current password using BCrypt.
- Hashes the new password before saving it.
- Old password stops working after the change.