INSERT INTO users (first_name, last_name, username, password, email, phone, balance, isAdmin, isBlocked)
VALUES
    ('John', 'Doe', 'johndoe', 'johndoe', 'john.doe@example.com', '1234567890', 1000, true, false),
    ('Alice', 'Smith','alicesmith' , 'alicesmith', 'alice.smith@example.com', '2345678901', 1000, false, false),
    ('Bob', 'Johnson', 'bobjohnson', 'bobjohnson', 'bob.johnson@example.com', '3456789012', 1000, false, false),
    ('Emma', 'Brown', 'emmabrownN@!2', 'emmabrownN@!2', 'emma.brown@example.com', '4567890123', 1000, false, true),
    ('Michael', 'Davis', 'mikedavis', 'mikedavis', 'mike.davis@example.com', '5678901234', 1500, false, false),
    ('Sophia', 'Wilson', 'sophiawilson', 'sophiawilson', 'sophia.wilson@example.com', '6789012345', 2000, false, false),
    ('James', 'Miller', 'jamesmiller', 'jamesmiller', 'james.miller@example.com', '7890123456', 500, true, false),
    ('Olivia', 'Taylor', 'oliviataylor', 'oliviataylor', 'olivia.taylor@example.com', '8901234567', 3000, false, false),
    ('William', 'Anderson', 'williamanderson', 'williamanderson', 'william.anderson@example.com', '9012345678', 2500, false, true),
    ('Ava', 'Thomas', 'avathomas', 'avathomas', 'ava.thomas@example.com', '0123456789', 1200, false, false);

INSERT INTO transactions (user_id, amount, status, created_at)
VALUES
    (1, 100, 'PENDING', CURRENT_TIMESTAMP),
    (2, 200, 'PENDING', CURRENT_TIMESTAMP),
    (3, 400, 'COMPLETED', CURRENT_TIMESTAMP),
    (1, 10.10, 'FAILED', CURRENT_TIMESTAMP),
    (4, 50, 'COMPLETED', CURRENT_TIMESTAMP),
    (5, 150, 'PENDING', CURRENT_TIMESTAMP),
    (6, 300, 'COMPLETED', CURRENT_TIMESTAMP),
    (7, 200, 'FAILED', CURRENT_TIMESTAMP),
    (8, 1000, 'COMPLETED', CURRENT_TIMESTAMP),
    (9, 500, 'PENDING', CURRENT_TIMESTAMP);