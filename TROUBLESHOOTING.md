# Troubleshooting Login Issues

## HTTP 400 Error - Common Causes & Solutions

### 1. **Server Not Running**
**Check if your application is running:**
```bash
# Check if port 3000 is in use
lsof -i :3000

# Or try to connect
curl -I http://localhost:3000/actuator/health
```

**Start the application:**
```bash
mvn spring-boot:run
```

### 2. **Database Connection Issues**
**Check if MySQL is running:**
```bash
# Check MySQL status
mysql -u simplebank -p -e "SELECT 1;"
```

**Verify database exists:**
```sql
SHOW DATABASES;
USE simplebank;
SHOW TABLES;
```

### 3. **Missing Admin User**
**Create admin user in database:**
```sql
-- Connect to your MySQL database and run:
INSERT INTO users (id, email, password, role, created_date, updated_date) 
VALUES (
    UUID(), 
    'admin@example.com', 
    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa',
    'ADMIN', 
    NOW(), 
    NOW()
);
```

### 4. **Test Step by Step**

#### Step 1: Check Application Logs
```bash
# Start application and watch logs
mvn spring-boot:run
```

#### Step 2: Test Basic Connectivity
```bash
# Test if server responds
curl -I http://localhost:3000

# Test if auth endpoint exists
curl -X GET http://localhost:3000/auth/login
```

#### Step 3: Test Login with Debug
```bash
# Run the debug script
./debug-login.sh
```

### 5. **Expected Responses**

#### Successful Login (200 OK):
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer"
}
```

#### Authentication Failed (401 Unauthorized):
```
HTTP/1.1 401 Unauthorized
```

#### Bad Request (400):
- Invalid JSON format
- Missing required fields
- Validation errors

### 6. **Common Issues & Fixes**

#### Issue: "User not found"
**Solution:** Create the admin user in database

#### Issue: "Bad credentials"
**Solution:** Check password - should be "admin123"

#### Issue: "Connection refused"
**Solution:** Start the application with `mvn spring-boot:run`

#### Issue: "Database connection failed"
**Solution:** Check MySQL is running and credentials are correct

### 7. **Debug Commands**

```bash
# Check if application is running
ps aux | grep java

# Check port usage
netstat -an | grep 3000

# Test database connection
mysql -u simplebank -p simplebank -e "SELECT COUNT(*) FROM users;"

# Check application logs
tail -f logs/spring.log  # if logging to file
```

### 8. **Quick Fix Checklist**

- [ ] Application is running (`mvn spring-boot:run`)
- [ ] MySQL is running and accessible
- [ ] Database `simplebank` exists
- [ ] Table `users` exists
- [ ] Admin user exists in database
- [ ] Password is correct ("admin123")
- [ ] No firewall blocking port 3000

### 9. **Alternative Test**

If the debug script doesn't work, try this simple test:

```bash
# Simple curl test
curl -X POST http://localhost:3000/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"admin@example.com","password":"admin123"}' \
  -v
```

### 10. **Get Help**

If you're still getting HTTP 400:

1. **Check application logs** for detailed error messages
2. **Verify database connection** and user existence
3. **Test with the debug script** to see exact error response
4. **Ensure all dependencies** are properly installed

The most common cause of HTTP 400 is either:
- Server not running
- Missing admin user in database
- Database connection issues 