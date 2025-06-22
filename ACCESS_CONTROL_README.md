# Access Control Implementation for Simple Bank API

This document explains how access control has been implemented for the POST /users API and other endpoints.

## Overview

The application now uses **JWT (JSON Web Token) authentication** with **RSA key signing** and **role-based access control** to secure the API endpoints.

## Security Features

### 1. JWT Authentication with RSA Keys
- Tokens are signed using RSA private key (RS256 algorithm)
- Tokens are verified using RSA public key
- Tokens expire after 24 hours (configurable)
- Tokens are validated on each protected request

### 2. Role-Based Access Control
- **ADMIN** role: Can create users and access all user data
- **USER** role: Can access their own user data and view other users

### 3. Protected Endpoints

| Endpoint | Method | Access Level | Description |
|----------|--------|--------------|-------------|
| `/auth/login` | POST | Public | Login to get JWT token |
| `/users` | POST | ADMIN only | Create new user |
| `/users` | GET | ADMIN, USER | Get all users |
| `/users/{id}` | GET | ADMIN, USER | Get user by ID |

## How to Use

### 1. Login to Get JWT Token

```bash
curl -X POST http://localhost:3000/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"admin123"}'
```

Response:
```json
{
  "token": "eyJhbGciOiJSUzI1NiJ9...",
  "type": "Bearer"
}
```

### 2. Use JWT Token for Protected Endpoints

```bash
curl -X POST http://localhost:3000/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{"email":"newuser@example.com","password":"password123","role":"USER"}'
```

## Setup Instructions

### 1. Create an Admin User

You need to create an admin user in the database first. You can do this by:

1. **Using the application** (if you have a way to bypass security temporarily)
2. **Direct database insertion** (recommended for initial setup)

Example SQL to create an admin user:
```sql
INSERT INTO users (id, email, password, role, created_date, updated_date) 
VALUES (
    UUID(), 
    'admin@example.com', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa', -- password: admin123
    'ADMIN', 
    NOW(), 
    NOW()
);
```

### 2. RSA Key Configuration

The JWT configuration uses RSA keys instead of a secret. The configuration is in `application.properties`:
```properties
jwt.private-key-path=keys/rsa-private-key.pem
jwt.public-key-path=keys/rsa-public-key.pem
jwt.expiration=86400000
```

**Important**: 
- The application uses RS256 algorithm for JWT signing (RSA + SHA256)
- Private key is used for signing tokens
- Public key is used for verifying tokens
- Keep the private key secure and never commit it to version control
- The public key can be shared with other services that need to verify tokens

### 3. RSA Key Files

The application requires two RSA key files in the `keys/` directory:
- `keys/rsa-private-key.pem` - Private key for signing JWTs
- `keys/rsa-public-key.pem` - Public key for verifying JWTs

These files should be generated using OpenSSL:
```bash
# Create keys directory
mkdir -p keys

# Generate private key
openssl genrsa -out keys/rsa-private-key.pem 2048

# Generate public key from private key
openssl rsa -in keys/rsa-private-key.pem -pubout -out keys/rsa-public-key.pem
```

## Security Considerations

1. **RSA Key Management**: 
   - Keep the private key secure and never commit it to version control
   - Use a key management service in production environments
   - Rotate keys periodically for enhanced security

2. **Token Expiration**: Tokens expire after 24 hours by default

3. **HTTPS**: Use HTTPS in production to protect tokens in transit

4. **Password Hashing**: Passwords are automatically hashed using BCrypt

5. **Role Validation**: Server-side role validation prevents privilege escalation

6. **Asymmetric Cryptography Benefits**:
   - Non-repudiation: Only the holder of the private key can create valid signatures
   - Key Distribution: Public key can be safely shared for verification
   - Scalability: Multiple services can verify tokens using the public key

## Troubleshooting

### Common Issues

1. **401 Unauthorized**: Check if you're including the JWT token in the Authorization header
2. **403 Forbidden**: Check if your user has the required role (ADMIN for creating users)
3. **Token Expired**: Re-login to get a new token
4. **Key Loading Errors**: Ensure RSA key files exist in the `keys/` directory and are readable by the application

### Debug Mode

To see more details about authentication, you can enable debug logging in `application.properties`:
```properties
logging.level.org.springframework.security=DEBUG
```

## API Examples

### Create a User (Admin only)
```bash
curl -X POST http://localhost:3000/users \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -d '{
    "email": "user@example.com",
    "password": "password123",
    "role": "USER"
  }'
```

### Get All Users (Admin or User)
```bash
curl -X GET http://localhost:3000/users \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

### Get User by ID (Admin or User)
```bash
curl -X GET http://localhost:3000/users/USER_ID \
  -H "Authorization: Bearer YOUR_JWT_TOKEN"
```

## Migration from HMAC to RSA

This application has been migrated from HMAC (HS256) to RSA (RS256) JWT signing for enhanced security:

- **Previous**: Used a shared secret for both signing and verification
- **Current**: Uses private key for signing, public key for verification
- **Benefits**: Better security, scalability, and key distribution capabilities 