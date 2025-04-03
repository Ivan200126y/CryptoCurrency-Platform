CREATE TABLE users
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(16)  NOT NULL,
    last_name  VARCHAR(16)  NOT NULL,
    username   VARCHAR(20)  NOT NULL UNIQUE,
    password   VARCHAR(500) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    phone      VARCHAR(10)  NOT NULL UNIQUE,
    balance    VARCHAR(255) NOT NULL,
    isAdmin    BOOLEAN      NOT NULL,
    isBlocked  BOOLEAN      NOT NULL
);

CREATE TABLE transactions
(
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id    BIGINT                        NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (id) ON DELETE CASCADE,
    amount     DECIMAL(10, 2)                NOT NULL,
    status     ENUM ('INCOMING', 'OUTGOING') NOT NULL DEFAULT 'INCOMING',
    created_at TIMESTAMP                              DEFAULT CURRENT_TIMESTAMP,
    currency   VARCHAR(20)                   NOT NULL
);

