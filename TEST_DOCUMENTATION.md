# Login Feature Unit Tests Documentation

This document describes the comprehensive unit tests implemented for the login features of the Simple Bank API.

## Test Structure

The tests are organized into the following categories:

### 1. Controller Tests (`AuthControllerTest.java`)
Tests the REST API endpoints for authentication.

**Test Coverage:**
- ✅ Valid login credentials → Returns JWT token
- ✅ Invalid credentials → Returns 401 Unauthorized
- ✅ Empty email → Returns 400 Bad Request
- ✅ Invalid email format → Returns 400 Bad Request
- ✅ Empty password → Returns 400 Bad Request
- ✅ Null email → Returns 400 Bad Request
- ✅ Null password → Returns 400 Bad Request
- ✅ Authentication exception → Returns 500 Internal Server Error
- ✅ JWT generation exception → Returns 500 Internal Server Error
- ✅ Malformed JSON → Returns 400 Bad Request

### 2. JWT Token Provider Tests (`JwtTokenProviderTest.java`)
Tests the JWT token generation, validation, and parsing functionality.

**Test Coverage:**
- ✅ Generate token with valid email → Returns valid JWT
- ✅ Generate token with null email → Throws exception
- ✅ Generate token with empty email → Throws exception
- ✅ Extract email from valid token → Returns correct email
- ✅ Extract email from invalid token → Throws exception
- ✅ Extract email from null token → Throws exception
- ✅ Extract email from empty token → Throws exception
- ✅ Validate valid token → Returns true
- ✅ Validate invalid token → Returns false
- ✅ Validate null token → Returns false
- ✅ Validate empty token → Returns false
- ✅ Validate expired token → Returns false
- ✅ Token includes correct claims
- ✅ Token has correct expiration
- ✅ Different emails generate different tokens
- ✅ Same email generates different tokens (due to timestamps)

### 3. DTO Tests

#### LoginRequest Tests (`LoginRequestTest.java`)
Tests the LoginRequest data transfer object.

**Test Coverage:**
- ✅ Default constructor
- ✅ Parameterized constructor
- ✅ Setters and getters
- ✅ Set email to null/empty
- ✅ Set password to null/empty
- ✅ Constructor with null values
- ✅ Constructor with empty strings
- ✅ Multiple set operations

#### LoginResponse Tests (`LoginResponseTest.java`)
Tests the LoginResponse data transfer object.

**Test Coverage:**
- ✅ Default constructor
- ✅ Parameterized constructor
- ✅ Setters and getters
- ✅ Set token to null/empty
- ✅ Set type to null/empty
- ✅ Constructor with null values
- ✅ Constructor with empty strings
- ✅ Multiple set operations
- ✅ Long token handling
- ✅ Special characters in token/type

### 4. Integration Tests (`AuthIntegrationTest.java`)
Tests the complete authentication flow with database integration.

**Test Coverage:**
- ✅ Valid credentials → Returns JWT token
- ✅ Invalid credentials → Returns 401 Unauthorized
- ✅ Non-existent user → Returns 401 Unauthorized
- ✅ Empty email → Returns 400 Bad Request
- ✅ Invalid email format → Returns 400 Bad Request
- ✅ Empty password → Returns 400 Bad Request
- ✅ Null email → Returns 400 Bad Request
- ✅ Null password → Returns 400 Bad Request
- ✅ Admin user login → Returns JWT token
- ✅ Malformed JSON → Returns 400 Bad Request
- ✅ Multiple users → Returns correct tokens for each

## Test Configuration

### Test Properties (`application-test.properties`)
- Uses H2 in-memory database for testing
- Configures JWT with test RSA keys
- Enables H2 console for debugging
- Uses create-drop schema for clean test runs

### Test Dependencies
- **Spring Boot Test**: For integration testing
- **Spring Security Test**: For security testing
- **H2 Database**: In-memory database for tests
- **Mockito**: For mocking dependencies
- **JUnit 5**: Testing framework

## Running Tests

### Run All Tests
```bash
./run-tests.sh
```

### Run Specific Test Classes
```bash
# Run only controller tests
mvn test -Dtest=AuthControllerTest

# Run only JWT tests
mvn test -Dtest=JwtTokenProviderTest

# Run only integration tests
mvn test -Dtest=AuthIntegrationTest
```

### Run Tests with Coverage
```bash
mvn test jacoco:report
```

## Test Data

### Test Users
- **Regular User**: `test@example.com` / `password123`
- **Admin User**: `admin@example.com` / `admin123`
- **Multiple Users**: `user1@example.com`, `user2@example.com`

### Test JWT Tokens
- Valid tokens are generated using RSA keys
- Tokens include email as subject claim
- Tokens have 1-hour expiration for tests

## Test Scenarios

### Happy Path
1. User provides valid credentials
2. Authentication succeeds
3. JWT token is generated
4. Token contains correct email
5. Token is valid and can be verified

### Error Scenarios
1. **Invalid Credentials**: Wrong password or non-existent user
2. **Validation Errors**: Empty/null email or password
3. **System Errors**: Database connection issues, JWT generation failures
4. **Malformed Requests**: Invalid JSON, missing fields

### Edge Cases
1. **Multiple Users**: Different users get different tokens
2. **Same User Multiple Logins**: Each login generates unique token
3. **Special Characters**: Tokens with special characters
4. **Long Tokens**: Very long JWT tokens
5. **Null/Empty Values**: Handling of null and empty strings

## Security Testing

### JWT Security
- ✅ Tokens are signed with RSA private key
- ✅ Tokens are verified with RSA public key
- ✅ Invalid tokens are rejected
- ✅ Expired tokens are rejected
- ✅ Tokens contain correct user information

### Authentication Security
- ✅ Invalid credentials are rejected
- ✅ Non-existent users are rejected
- ✅ Password hashing is used
- ✅ Role-based access is maintained

## Test Reports

After running tests, detailed reports are available in:
- `target/surefire-reports/` - Test execution reports
- `target/site/jacoco/` - Code coverage reports (if using JaCoCo)

## Best Practices Implemented

1. **Arrange-Act-Assert Pattern**: Clear test structure
2. **Descriptive Test Names**: Self-documenting test methods
3. **Isolated Tests**: Each test is independent
4. **Mocking**: External dependencies are mocked
5. **Integration Testing**: End-to-end flow testing
6. **Error Scenarios**: Comprehensive error handling tests
7. **Edge Cases**: Boundary condition testing
8. **Security Focus**: Authentication and authorization testing

## Maintenance

### Adding New Tests
1. Follow the existing naming convention
2. Use the Arrange-Act-Assert pattern
3. Add appropriate assertions
4. Update this documentation

### Updating Tests
1. Ensure tests remain isolated
2. Update test data if needed
3. Verify all scenarios are covered
4. Run the full test suite

### Test Data Management
- Tests use in-memory database
- Data is cleaned between tests
- No external dependencies
- Consistent test data across runs 