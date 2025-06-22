#!/bin/bash

echo "Running Login Feature Unit Tests..."
echo "=================================="

# Run all tests
mvn test

echo ""
echo "Test execution completed!"
echo "Check the target/surefire-reports directory for detailed test reports." 