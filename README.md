# Simple Bank API Documentation

A RESTful banking API built with Spring Boot that provides user management, account operations, and transaction processing capabilities.

## Running via Docker Compose

```sh
docker compose up -d
```

## Running Test

```sh
mvn test
```

## Base URL
```
http://localhost:3000
```

## User Roles
The API supports two user roles:
- **ADMIN**: Full access to all operations including creating accounts, deposits, and managing all users
- **USER**: Limited access to their own accounts and transactions, can perform transfers and request bank statements

## Authentication
Most endpoints require JWT authentication. Include the JWT token in the Authorization header:
```
Authorization: Bearer <your-jwt-token>
```

---

## API Endpoints

### 1. Register User
**POST** `/users`

Creates a new user account.

#### Request
```bash
curl -X POST http://localhost:3000/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "password123",
    "role": "USER",
    "cid": "1234567890123",
    "nameTh": "จอห์น โด",
    "nameEn": "John Doe",
    "pin": "123456"
  }'
```

**Example for ADMIN user (customer fields optional):**
```bash
curl -X POST http://localhost:3000/users \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@example.com",
    "password": "password123",
    "role": "ADMIN"
  }'
```

#### Request Fields
- `email` (string, required): User's email address
- `password` (string, required): User's password
- `role` (string, optional): User role - "ADMIN" or "USER" (defaults to "USER")
- `cid` (string, optional): Customer ID - exactly 13 numeric digits (required for role=USER, optional for role=ADMIN)
- `nameTh` (string, optional): Customer name in Thai (required for role=USER, optional for role=ADMIN)
- `nameEn` (string, optional): Customer name in English (required for role=USER, optional for role=ADMIN)
- `pin` (string, optional): 6-digit PIN for transactions (required for role=USER, optional for role=ADMIN)

**Note:** For `role=USER`, all customer fields (`cid`, `nameTh`, `nameEn`, `pin`) are required. For `role=ADMIN`, these fields are optional but will be validated if provided.

#### Response
```json
{
  "id": "user123"
}
```

#### Response Fields
- `id` (string): Unique user identifier

---

### 2. Login
**POST** `/auth/login`

Authenticates a user and returns a JWT token.

#### Request
```bash
curl -X POST http://localhost:3000/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "john.doe@example.com",
    "password": "password123"
  }'
```

#### Request Fields
- `email` (string, required): User's email address
- `password` (string, required): User's password

#### Response
```json
{
  "token": "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}
```

#### Response Fields
- `token` (string): JWT token for authentication
- `type` (string): Token type (always "Bearer")

---

### 3. Create Account
**POST** `/accounts`

Creates a new bank account. **ADMIN only**.

#### Request
```bash
curl -X POST http://localhost:3000/accounts \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <admin-jwt-token>" \
  -d '{
    "cid": "1234567890123",
    "nameTh": "จอห์น โด",
    "nameEn": "John Doe",
    "amount": 1000
  }'
```

#### Request Fields
- `cid` (string, required): Customer ID - exactly 13 numeric digits
- `nameTh` (string, required): Customer name in Thai
- `nameEn` (string, required): Customer name in English
- `amount` (integer, optional): Initial deposit amount in stang (minimum 100)

#### Response
```json
{
  "id": "0000001"
}
```

#### Response Fields
- `id` (string): 7-digit zero-padded account number

---

### 4. Get Account Information
**GET** `/accounts/{id}`

Retrieves account information by account ID.

#### Request
```bash
curl -X GET http://localhost:3000/accounts/0000001 \
  -H "Authorization: Bearer <jwt-token>"
```

#### Path Parameters
- `id` (string, required): 7-digit account number

#### Response
```json
{
  "id": "0000001",
  "cid": "1234567890123",
  "nameTh": "จอห์น โด",
  "nameEn": "John Doe",
  "createdDate": "2024-01-15 10:30:00",
  "updatedDate": "2024-01-15 10:30:00"
}
```

#### Response Fields
- `id` (string): 7-digit zero-padded account number
- `cid` (string): Customer ID
- `nameTh` (string): Customer name in Thai
- `nameEn` (string): Customer name in English
- `createdDate` (datetime): Account creation timestamp
- `updatedDate` (datetime): Last update timestamp

---

### 5. Deposit Money
**POST** `/tx/deposit`

Processes a deposit transaction. **ADMIN only**.

#### Request
```bash
curl -X POST http://localhost:3000/tx/deposit \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <admin-jwt-token>" \
  -d '{
    "id": "deposit123",
    "accountNo": "0000001",
    "amount": 500
  }'
```

#### Request Fields
- `id` (string, required): Unique transaction identifier
- `accountNo` (string, required): 7-digit account number
- `amount` (integer, required): Deposit amount in stang (minimum 100)

#### Response
```json
{
  "success": true,
  "id": "12345"
}
```

#### Response Fields
- `success` (boolean): Transaction success status
- `id` (string): Transaction ID

---

### 6. Transfer Money
**POST** `/tx/transfer`

Processes a transfer between accounts. **USER only**.

#### Request
```bash
curl -X POST http://localhost:3000/tx/transfer \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <user-jwt-token>" \
  -d '{
    "senderAccountNo": "0000001",
    "receiverAccountNo": "0000002",
    "amount": 100,
    "pin": "123456"
  }'
```

#### Request Fields
- `senderAccountNo` (string, required): 7-digit sender account number
- `receiverAccountNo` (string, required): 7-digit receiver account number
- `amount` (integer, required): Transfer amount in stang (minimum 1)
- `pin` (string, required): 6-digit PIN for verification

#### Response
```json
{
  "success": true,
  "id": "12346"
}
```

#### Response Fields
- `success` (boolean): Transaction success status
- `id` (string): Transaction ID of the sender's transaction

---

### 7. Request Bank Statement
**POST** `/accounts/{id}/statement`

Generates a bank statement in CSV format. **USER only**.

#### Request
```bash
curl -X POST http://localhost:3000/accounts/0000001/statement \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <user-jwt-token>" \
  -d '{
    "pin": "123456",
    "since": 1705312200000,
    "until": 1705398600000
  }' \
  --output statement.csv
```

#### Path Parameters
- `id` (string, required): 7-digit account number

#### Request Fields
- `pin` (string, required): 6-digit PIN for verification
- `since` (long, required): Start timestamp in milliseconds
- `until` (long, required): End timestamp in milliseconds

#### Response
The response is a CSV file download containing transaction details.

#### CSV Format
```csv
Date,Time,Code,Channel,Debit/Credit,Balance,Remark
23/06/25,01:48,A3,ATS,+50.00,50.00,Receive from x0001 name
23/06/25,01:51,A3,ATS,+10.00,60.00,Receive from x0001 name
23/06/25,10:00,A3,ATS,+10.00,70.00,Receive from x0001 name
```

---

## Error Responses

### Common HTTP Status Codes
- `200 OK`: Request successful
- `201 Created`: Resource created successfully
- `400 Bad Request`: Invalid request data
- `401 Unauthorized`: Authentication required or failed
- `403 Forbidden`: Access denied (insufficient permissions)
- `404 Not Found`: Resource not found
- `409 Conflict`: Resource already exists
- `500 Internal Server Error`: Server error

## Running the Application

1. Start the MySQL database
2. Run the application with: `mvn spring-boot:run`
3. The API will be available at `http://localhost:3000`

## Database Setup

The application uses MySQL with the following configuration:
- Database: `simplebank`
- Username: `simplebank`
- Password: `simplebank123`
- Port: `3306`

Database initialization scripts are located in `src/main/resources/db/`. 
