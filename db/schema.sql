-- Localink schema (PostgreSQL)
-- Create the database separately if needed:
--   createdb localink

CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(120) NOT NULL,
    email VARCHAR(160) NOT NULL UNIQUE,
    password_hash VARCHAR(100) NOT NULL,
    role VARCHAR(16) NOT NULL DEFAULT 'CUSTOMER',
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_user_role CHECK (role IN ('CUSTOMER','PROVIDER','ADMIN'))
);

CREATE TABLE IF NOT EXISTS services (
    id BIGSERIAL PRIMARY KEY,
    provider_id BIGINT NOT NULL,
    title VARCHAR(160) NOT NULL,
    category VARCHAR(80) NOT NULL,
    description TEXT,
    hourly_rate NUMERIC(10,2) NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (provider_id) REFERENCES users(id)
);

-- Prevent duplicate service titles per provider
DO $$ BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM pg_indexes WHERE schemaname = 'public' AND indexname = 'ux_service_provider_title'
    ) THEN
        CREATE UNIQUE INDEX ux_service_provider_title ON services(provider_id, title);
    END IF;
END $$;

CREATE TABLE IF NOT EXISTS bookings (
    id BIGSERIAL PRIMARY KEY,
    customer_id BIGINT NOT NULL,
    service_id BIGINT NOT NULL,
    status VARCHAR(16) NOT NULL DEFAULT 'PENDING',
    scheduled_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (customer_id) REFERENCES users(id),
    FOREIGN KEY (service_id) REFERENCES services(id),
    CONSTRAINT chk_booking_status CHECK (status IN ('PENDING','ACCEPTED','REJECTED','IN_PROGRESS','COMPLETED','CANCELLED'))
);

CREATE TABLE IF NOT EXISTS reviews (
    id BIGSERIAL PRIMARY KEY,
    booking_id BIGINT NOT NULL,
    rating SMALLINT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment VARCHAR(1000),
    created_at TIMESTAMPTZ DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (booking_id),
    FOREIGN KEY (booking_id) REFERENCES bookings(id)
);

-- Seed baseline users
INSERT INTO users (name, email, password_hash, role)
VALUES
    ('Alice Customer', 'alice@example.com', '$2a$10$abcdefghijklmnopqrstuv', 'CUSTOMER'),
    ('Bob Provider',   'bob@example.com',   '$2a$10$abcdefghijklmnopqrstuv', 'PROVIDER'),
    ('Cara Provider',  'cara@example.com',  '$2a$10$abcdefghijklmnopqrstuv', 'PROVIDER'),
    ('Dan Provider',   'dan@example.com',   '$2a$10$abcdefghijklmnopqrstuv', 'PROVIDER')
ON CONFLICT (email) DO NOTHING;

-- Seed 20+ services across categories for Bob, Cara, Dan
WITH p AS (
    SELECT id FROM users WHERE email = 'bob@example.com'
), c AS (
    SELECT id FROM users WHERE email = 'cara@example.com'
), d AS (
    SELECT id FROM users WHERE email = 'dan@example.com'
)
INSERT INTO services (provider_id, title, category, description, hourly_rate, is_active)
VALUES
    ((SELECT id FROM p), 'Electrician - General Repairs', 'Electrician', 'Wiring, fixtures, troubleshooting', 30.00, TRUE),
    ((SELECT id FROM p), 'Plumber - Pipe & Leak Fix',     'Plumber',     'Pipes, drains, leak repairs',      28.00, TRUE),
    ((SELECT id FROM p), 'Carpenter - Furniture Repair',  'Carpenter',   'Custom builds and repairs',        27.00, TRUE),
    ((SELECT id FROM p), 'Painter - Interior Walls',      'Painter',     'Interior painting and touch-ups',  25.00, TRUE),
    ((SELECT id FROM p), 'Cleaner - Deep Cleaning',       'Cleaner',     'Home/office deep cleaning',        22.00, TRUE),
    ((SELECT id FROM p), 'Gardener - Lawn Care',          'Gardener',    'Mowing, trimming, maintenance',    20.00, TRUE),
    ((SELECT id FROM c), 'Tutor - Math (High School)',    'Tutor',       'Algebra, calculus tutoring',       24.00, TRUE),
    ((SELECT id FROM c), 'Mechanic - Basic Service',      'Mechanic',    'Oil change, brakes, diagnostics',  35.00, TRUE),
    ((SELECT id FROM c), 'IT Support - Home Network',     'IT Support',  'WiFi setup, PC troubleshooting',   26.00, TRUE),
    ((SELECT id FROM c), 'Babysitter - Evening Care',     'Babysitter',  'Evening/weekend babysitting',      18.00, TRUE),
    ((SELECT id FROM c), 'Pet Sitter - Dog Walking',      'Pet Sitter',  'Dog walking and pet care',         16.00, TRUE),
    ((SELECT id FROM d), 'Chef - Home Cooking',           'Chef',        'Meal prep, small events',          32.00, TRUE),
    ((SELECT id FROM d), 'Fitness Trainer - Personal',    'Fitness',     'Personal training sessions',       30.00, TRUE),
    ((SELECT id FROM d), 'Massage Therapist - Relaxation','Wellness',    'Relaxation massage',               40.00, TRUE),
    ((SELECT id FROM d), 'Photographer - Portraits',      'Photographer','Portrait sessions',                45.00, TRUE),
    ((SELECT id FROM d), 'Videographer - Events',         'Videographer','Event videography',                50.00, TRUE),
    ((SELECT id FROM d), 'Music Teacher - Guitar',        'Music Teacher','Beginner to intermediate',        25.00, TRUE),
    ((SELECT id FROM d), 'Language Tutor - English',      'Language Tutor','Spoken & written English',      23.00, TRUE),
    ((SELECT id FROM d), 'Handyman - Odd Jobs',           'Handyman',    'General household fixes',          26.00, TRUE),
    ((SELECT id FROM d), 'Roofer - Minor Repairs',        'Roofer',      'Leak fixes, shingles',             38.00, TRUE),
    ((SELECT id FROM d), 'Pest Control - Inspection',     'Pest Control','Inspection & treatment',          34.00, TRUE),
    ((SELECT id FROM d), 'HVAC Technician - Service',     'HVAC',        'AC/Heater servicing',              42.00, TRUE),
    ((SELECT id FROM d), 'Locksmith - Emergency',         'Locksmith',   'Lockouts and rekeying',            36.00, TRUE),
    ((SELECT id FROM d), 'Makeup Artist - Events',        'Makeup',      'Event makeup services',            28.00, TRUE),
    ((SELECT id FROM d), 'Event Planner - Small Events',  'Event Planner','Planning & coordination',        44.00, TRUE)
ON CONFLICT (provider_id, title) DO NOTHING;
