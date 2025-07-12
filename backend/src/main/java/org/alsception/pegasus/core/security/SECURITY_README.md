# 🛡️ Security Module - Pegasus Shop Backend

This module handles JWT-based authentication and authorization using  with Spring Security **JWT (JSON Web Tokens)**.

## 🏗️ Architecture Overview

This security package implements a **stateless JWT authentication system** with the following flow:
1. Users register/login to get a JWT token
2. The token is included in subsequent requests
3. A filter validates the token on each request
4. Valid tokens allow access to protected endpoints

## 📋 Component Breakdown

### **AuthController.java** - Authentication Endpoints
This REST controller handles user registration and login:

- **Registration (`/api/auth/register`)**:
  - Checks if username already exists
  - Encrypts password using BCrypt
  - Creates new `PGSUser` and saves to database
  - Returns success/error message

- **Login (`/api/auth/login`)**:
  - Authenticates credentials using `AuthenticationManager`
  - Loads user details and generates JWT token
  - Returns the token to the client

### **JwtAuthenticationFilter.java** - Request Interceptor
This filter runs on every HTTP request:

- Extracts JWT from `Authorization: Bearer <token>` header
- Validates the token using `JwtUtils`
- If valid, loads user details and sets authentication in Spring Security context
- If invalid/missing, logs unauthorized request details
- Continues filter chain for further processing

### **SecurityConfig.java** - Main Security Configuration
This configures the entire security framework:

- **CORS Configuration**: Dynamically allows different origins based on active profile (dev vs production)
- **Security Filter Chain**: 
  - Disables CSRF (not needed for stateless JWT)
  - Sets session management to stateless
  - Defines public endpoints (`/api/auth/**`, static files)
  - Requires authentication for all other endpoints
- **Authentication Manager**: Configures password encoding and user details service

### **JwtUtils.java** - Token Management
Handles all JWT operations:

- **Token Generation**: Creates JWT with username, issued date, and expiration
- **Token Validation**: Verifies token signature and expiration
- **Username Extraction**: Parses username from valid tokens
- Uses HMAC-SHA512 signing algorithm with a secret key

### **CustomUserDetailsService.java** - User Loading
Implements Spring Security's `UserDetailsService`:

- Loads user from database by username
- Converts `PGSUser` entity to Spring Security `UserDetails`
- Provides `userExists()` method for registration validation
- Maps user roles for authorization

### **AuthRequest.java** - Data Transfer Object
Simple DTO for authentication requests:

- Contains username, password, and role fields
- Uses Lombok for getters/setters
- Password is write-only (not returned in responses)

### **PasswordConfig.java** - Password Encoding
Configures BCrypt password encoder as a Spring bean for dependency injection.

## 🔄 Complete Authentication Flow

1. **User Registration**:
   ```
   POST /api/auth/register
   → Check username availability
   → Encrypt password
   → Save user to database
   ```

2. **User Login**:
   ```
   POST /api/auth/login
   → Authenticate credentials
   → Generate JWT token
   → Return token to client
   ```

3. **Protected Request**:
   ```
   GET /api/protected-endpoint
   Authorization: Bearer <jwt-token>
   → JwtAuthenticationFilter extracts token
   → Validates token
   → Loads user details
   → Sets authentication context
   → Proceeds to controller
   ```

## 🔧 Key Features

- **Stateless Authentication**: No server-side sessions
- **Role-Based Access Control**: Ready for different user roles
- **Dynamic CORS**: Different allowed origins for dev/production
- **Comprehensive Logging**: Detailed logs for debugging
- **Error Handling**: Proper error responses for invalid tokens
- **Password Security**: BCrypt encryption for passwords

## 💡 Security Considerations

The implementation includes several security best practices:
- JWT tokens have configurable expiration times
- Passwords are hashed with BCrypt
- CORS is configured based on environment
- Unauthorized requests are logged for monitoring
- Tokens are validated on every request

## ⚙️ Required Configuration

In a Spring Boot application, these values would typically be defined in src/main/resources/application.properties:

```
jwt.secret=yourVerySecretKeyThatShouldBeLongAndComplex
jwt.expirationMs=3600000 # 1 hour in milliseconds
```

---

## Example token:
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```
