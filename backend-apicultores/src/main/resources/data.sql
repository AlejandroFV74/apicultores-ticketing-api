-- =========================================================================
-- 1. CREACIÓN DE USUARIOS (ADMIN, ORGANIZER, BUYER)
-- =========================================================================
INSERT INTO users (user_id, email, full_name, password_hash, role, enabled, account_non_locked, created_at)
VALUES
    (gen_random_uuid(), 'admin@pnc.com', 'Diego Iraheta (Admin)', 'hash_secure_admin_123', 'ADMIN', true, true, NOW()),
    (gen_random_uuid(), 'organizador.rock@pnc.com', 'Carlos Gómez (Organizador Rock)', 'hash_secure_org_1', 'ORGANIZER', true, true, NOW()),
    (gen_random_uuid(), 'comprador.test@pnc.com', 'Juan Pérez (Cliente)', 'hash_secure_buyer_1', 'BUYER', true, true, NOW())
    ON CONFLICT (email) DO NOTHING;

-- =========================================================================
-- 2. CREACIÓN DE 3 EVENTOS DIFERENTES
-- =========================================================================

-- Evento 1: Concierto de Rock
INSERT INTO event (event_id, organizer_id, title, description, venue, start_date, end_date, status, max_tickets_per_user, created_at)
SELECT
    gen_random_uuid(), user_id, 'Concierto de Rock Clasico', 'Una noche de puros clasicos de los 80s y 90s.', 'Estadio Cuscatlan', NOW() + INTERVAL '5 days', NOW() + INTERVAL '5 days 4 hours', 'ACTIVE', 4, NOW()
FROM users WHERE email = 'organizador.rock@pnc.com'
             AND NOT EXISTS (SELECT 1 FROM event WHERE title = 'Concierto de Rock Clasico');

-- Evento 2: Conferencia Tecnica (Organizado por el Admin)
INSERT INTO event (event_id, organizer_id, title, description, venue, start_date, end_date, status, max_tickets_per_user, created_at)
SELECT
    gen_random_uuid(), user_id, 'PNC Tech Summit 2026', 'Conferencia sobre desarrollo backend, cloud y arquitectura.', 'Hotel Hilton', NOW() + INTERVAL '12 days', NOW() + INTERVAL '12 days 8 hours', 'ACTIVE', 2, NOW()
FROM users WHERE email = 'admin@pnc.com'
             AND NOT EXISTS (SELECT 1 FROM event WHERE title = 'PNC Tech Summit 2026');

-- Evento 3: Festival Gastronomico
INSERT INTO event (event_id, organizer_id, title, description, venue, start_date, end_date, status, max_tickets_per_user, created_at)
SELECT
    gen_random_uuid(), user_id, 'Festival del Pan y Miel', 'Exposicion gastronomica regional de apicultores.', 'Plaza Central', NOW() + INTERVAL '20 days', NOW() + INTERVAL '20 days 6 hours', 'ACTIVE', 5, NOW()
FROM users WHERE email = 'organizador.rock@pnc.com'
             AND NOT EXISTS (SELECT 1 FROM event WHERE title = 'Festival del Pan y Miel');


-- =========================================================================
-- 3. CREACIÓN DE 15 ASIENTOS PARA CADA EVENTO (45 EN TOTAL)
-- =========================================================================

-- Asientos para Evento 1 (Concierto de Rock Clasico)
INSERT INTO seat (seat_id, event_id, seat_number, seat_type, price, status, created_at)
SELECT gen_random_uuid(), event_id, s.num, 'VIP', 120.00, 'AVAILABLE', NOW()
FROM event CROSS JOIN (VALUES ('A-1'),('A-2'),('A-3'),('A-4'),('A-5'),('A-6'),('A-7'),('A-8'),('A-9'),('A-10'),('A-11'),('A-12'),('A-13'),('A-14'),('A-15')) AS s(num)
WHERE title = 'Concierto de Rock Clasico'
  AND NOT EXISTS (SELECT 1 FROM seat se JOIN event e ON se.event_id = e.event_id WHERE e.title = 'Concierto de Rock Clasico');

-- Asientos para Evento 2 (PNC Tech Summit 2026)
INSERT INTO seat (seat_id, event_id, seat_number, seat_type, price, status, created_at)
SELECT gen_random_uuid(), event_id, s.num, 'GENERAL', 45.00, 'AVAILABLE', NOW()
FROM event CROSS JOIN (VALUES ('A-1'),('A-2'),('A-3'),('A-4'),('A-5'),('A-6'),('A-7'),('A-8'),('A-9'),('A-10'),('A-11'),('A-12'),('A-13'),('A-14'),('A-15')) AS s(num)
WHERE title = 'PNC Tech Summit 2026'
  AND NOT EXISTS (SELECT 1 FROM seat se JOIN event e ON se.event_id = e.event_id WHERE e.title = 'PNC Tech Summit 2026');

-- Asientos para Evento 3 (Festival del Pan y Miel)
INSERT INTO seat (seat_id, event_id, seat_number, seat_type, price, status, created_at)
SELECT gen_random_uuid(), event_id, s.num, 'PREMIUM', 15.00, 'AVAILABLE', NOW()
FROM event CROSS JOIN (VALUES ('A-1'),('A-2'),('A-3'),('A-4'),('A-5'),('A-6'),('A-7'),('A-8'),('A-9'),('A-10'),('A-11'),('A-12'),('A-13'),('A-14'),('A-15')) AS s(num)
WHERE title = 'Festival del Pan y Miel'
  AND NOT EXISTS (SELECT 1 FROM seat se JOIN event e ON se.event_id = e.event_id WHERE e.title = 'Festival del Pan y Miel');


-- =========================================================================
-- 4. DESCUENTOS OPCIONALES PARA EL ENTORNOS DE PRUEBA
-- =========================================================================
INSERT INTO discount (discount_id, event_id, code, description, category, discount_type, value, min_tickets, valid_until)
SELECT gen_random_uuid(), event_id, 'ROCK20', '20% descuento Rock', 'CODE', 'PERCENTAGE', 20.00, 1, NOW() + INTERVAL '10 days'
FROM event WHERE title = 'Concierto de Rock Clasico' ON CONFLICT (code) DO NOTHING;

INSERT INTO discount (discount_id, event_id, code, description, category, discount_type, value, min_tickets, valid_until)
SELECT gen_random_uuid(), event_id, 'TECH10', '10% descuento Tech', 'CODE', 'PERCENTAGE', 10.00, 1, NOW() + INTERVAL '10 days'
FROM event WHERE title = 'PNC Tech Summit 2026' ON CONFLICT (code) DO NOTHING;