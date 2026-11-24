CREATE DATABASE IF NOT EXISTS reviews_analyzer_db
CHARACTER SET utf8mb4;

USE reviews_analyzer_db;

CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    name VARCHAR(255) NULL,
    tier ENUM ('FREE', 'PRO', 'VIP') NOT NULL DEFAULT 'FREE',
    active BIT(1) NOT NULL DEFAULT b'1',
    created_at DATETIME(6) NULL,
    updated_at DATETIME(6) NULL,
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_users_email (email)
    ) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS authorities (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NOT NULL,
    user_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    KEY idx_authorities_user (user_id),
    CONSTRAINT fk_authorities_user
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
    ) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS project (
    project_id BIGINT NOT NULL AUTO_INCREMENT,
    marketplace_url VARCHAR(1000) NOT NULL,
    marketplace ENUM ('WILDBERRIES', 'OZON') NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    image_url VARCHAR(1000) NULL,
    current_rating DOUBLE NOT NULL DEFAULT 0,
    user_id BIGINT NOT NULL,
    created_at DATETIME(6) NULL,
    updated_at DATETIME(6) NULL,
    PRIMARY KEY (project_id),
    KEY idx_project_user_url (user_id, marketplace_url(191)),
    CONSTRAINT fk_project_user
    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE
    ) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS analysis (
    analysis_id BIGINT NOT NULL AUTO_INCREMENT,
    status ENUM ('PENDING', 'PROCESSING', 'COMPLETED', 'FAILED', 'NO_DATA')
    NOT NULL DEFAULT 'PENDING',
    created_at DATETIME(6) NULL,
    started_at DATETIME(6) NULL,
    finished_at DATETIME(6) NULL,
    total_reviews INT NOT NULL DEFAULT 0,
    average_rating DOUBLE NOT NULL DEFAULT 0,
    positive_reviews INT NOT NULL DEFAULT 0,
    negative_reviews INT NOT NULL DEFAULT 0,
    neutral_reviews INT NOT NULL DEFAULT 0,
    summary TEXT NULL,
    recommendations TEXT NULL,
    error_message TEXT NULL,
    project_id BIGINT NOT NULL,
    PRIMARY KEY (analysis_id),
    KEY idx_analysis_project_status (project_id, status),
    KEY idx_analysis_project_created (project_id, created_at),
    CONSTRAINT fk_analysis_project
    FOREIGN KEY (project_id) REFERENCES project (project_id) ON DELETE CASCADE
    ) ENGINE = InnoDB;

CREATE TABLE IF NOT EXISTS analysis_aspect (
    aspect_id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(255) NULL,
    category VARCHAR(255) NULL,
    mention_count INT NOT NULL DEFAULT 0,
    positive_mentions INT NOT NULL DEFAULT 0,
    negative_mentions INT NOT NULL DEFAULT 0,
    analysis_id BIGINT NOT NULL,
    PRIMARY KEY (aspect_id),
    KEY idx_aspect_analysis (analysis_id),
    CONSTRAINT fk_aspect_analysis
    FOREIGN KEY (analysis_id) REFERENCES analysis (analysis_id) ON DELETE CASCADE
    ) ENGINE = InnoDB;