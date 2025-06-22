#!/bin/bash

# This script ensures the test database exists and the user has privileges.
# Usage: ./setup-test-db.sh

set -e

CONTAINER_NAME="simple-bank-mysql-1"
DB_NAME="simplebank_test"
DB_USER="simplebank"
DB_PASS="simplebank123"
ROOT_PASS="rootpassword"

# Create the test database and grant privileges

docker exec -i "$CONTAINER_NAME" mysql -u root -p"$ROOT_PASS" -e "\
CREATE DATABASE IF NOT EXISTS $DB_NAME; \
GRANT ALL PRIVILEGES ON $DB_NAME.* TO '$DB_USER'@'%'; \
FLUSH PRIVILEGES;\
"

echo "Test database '$DB_NAME' is ready and privileges are set." 