# Sprint 2 API Contract

Defines how frontend communicates with backend for Sprint 2.

FE uses endpoints and formats listend, and BE must notify FE when a change is made. 


## Status Key

- PLANNED = not implemented yet
- IN PROGRESS = backend is currently being built
- READY FOR FE = frontend can safely connect to this endpoint

## Authentication

### Register User

**Status:** PLANNED

**Method:** POST

**Endpoint:**

```text
/api/auth/register

**Frontend Sends:**

```json
{
  "firstName": "Eman",
  "lastName": "Saleh",
  "email": "eman@gmail.my",
  "password": "MyPass!20",
  "promotionSubscribed": true
}

If sucessful: 
{
  "success": true,
  "message": "Account created. Please verify your email."
}
- Account is created
- User status is set to INACTIVE
- User is not automatically logged in
- Verification email is sent

Possible error:
{
  "success": false,
  "message": "Email is already registered."
}

Those expected behaviors match the Sprint 2 registration test case: account created, user remains unverified/not logged in, confirmation email sent, and status set to Inactive.

```markdown
### Login