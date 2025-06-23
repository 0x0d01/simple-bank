# Making the Banking Application Robust and Scalable

This document outlines architectural decisions and implementation strategies to improve robustness, scalability, and maintainability of the banking system.

---

## Microservice Architecture

The application is divided into three stateless microservices:

### 1. User Management Service
- Handles user registration and login.
- Provides user information to other services (e.g., via REST or gRPC).
- Uses a dedicated user management database.
- Stateless and horizontally scalable.

### 2. Account Management Service
- Responsible for:
  - Creating bank accounts
  - Retrieving account details
  - Fetching bank statements
- Sends initial deposit requests to the Transaction Management Service upon account creation.
- Uses its own account database.
- Exposes APIs to the Transaction Management Service for account lookup.

### 3. Transaction Management Service
- Handles:
  - Deposits
  - Transfers between accounts
- Receives deposit and transfer requests via message queues.
- Uses a separate transaction database.
- Interacts with the Account Management Service to validate and update account balances.

---

## Stateless Design

All services are stateless:
- No session data stored in memory
- Load-balanced instances can run independently
- Enables autoscaling and high availability

---

## Database Per Service

Each microservice uses its own isolated database:
- Prevents tight coupling between services
- Enables independent scaling, backups, and maintenance
- Promotes domain-driven design

| Microservice            | Database                |
|-------------------------|-------------------------|
| User Management         | user_db                 |
| Account Management      | account_db              |
| Transaction Management  | transaction_db          |

---

## Asynchronous Transaction Processing

### Queue-Based Transaction Flow

For deposit and transfer operations:
- Requests are sent to a message queue (e.g., Kafka, RabbitMQ)
- Each message includes the account number as a filter key

### Worker Thread Processing

- A pool of worker threads in the Transaction Management Service consumes and processes messages
- Workers are stateless and handle one message at a time

### Distributed Locking

To avoid race conditions:
- A distributed lock (e.g., using Redis) is acquired per account number
- Ensures that:
  - Only one thread globally can process transactions for a given account at a time
  - Prevents inconsistent balances due to concurrent updates

---

## Summary

| Strategy                          | Benefit                                       |
|----------------------------------|-----------------------------------------------|
| Microservices per domain         | Clear boundaries, team autonomy, scalability  |
| Stateless services               | Easy load balancing and failover              |
| Separate databases               | Loose coupling, resilience                    |
| Message queue for transactions   | Decoupled, resilient to spikes/failures       |
| Distributed locking per account  | Guarantees consistency in concurrent flows    |

---

## Future Enhancements

- Add circuit breakers and retries using Resilience4j
- Implement distributed tracing (e.g., OpenTelemetry)
- Integrate audit logging and monitoring dashboards