INSERT INTO users (email, password_hash, role)
VALUES ('demo@elearn.local', '$2a$10$4P1iZqvQe7xY9vO9v5o4Oe7a8Ue1e0l5QbVJdpgb1Qq6jS8v9N1Gi', 'ADMIN')
ON CONFLICT (email) DO NOTHING;
-- password for this demo user is: demo1234 (bcrypt)
