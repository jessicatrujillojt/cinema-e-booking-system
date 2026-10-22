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

**Note:** The verification endpoint is working, but the verification email is not being sent automatically yet.