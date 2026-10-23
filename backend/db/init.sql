-- ============================================================
-- Music Genre Encyclopedia — database bootstrap
-- ============================================================
-- Run this ONCE after installing PostgreSQL, as the "postgres"
-- superuser, to create the database and application user.
--
-- How to run:
--   Option A (psql):    psql -U postgres -f init.sql
--   Option B (SQL Shell / pgAdmin): open this file and execute it.
--
-- This script only PROVISIONS the database. The tables and sample
-- data are created automatically by the Spring Boot app on first
-- startup (spring.jpa.hibernate.ddl-auto = update + DataInitializer).
--
-- The credentials below match the defaults in
-- src/main/resources/application.yml. If you change them here,
-- also set DB_USERNAME / DB_PASSWORD when running the backend.
-- ============================================================

CREATE DATABASE music_encyclopedia;

CREATE USER encyclopedia WITH PASSWORD 'encyclopedia';

GRANT ALL PRIVILEGES ON DATABASE music_encyclopedia TO encyclopedia;

-- PostgreSQL 15+ restricts the public schema, so grant it explicitly.
-- (Must be run while connected to the new database.)
\connect music_encyclopedia
GRANT ALL ON SCHEMA public TO encyclopedia;
ALTER SCHEMA public OWNER TO encyclopedia;
