-- Research Paper Management System - Database Schema
-- MySQL 8.0+

CREATE DATABASE IF NOT EXISTS research_papers
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE research_papers;

-- ─────────────────────────────────────────────
-- DROP existing tables (children first)
-- ─────────────────────────────────────────────
DROP TABLE IF EXISTS reading_progress;
DROP TABLE IF EXISTS notes;
DROP TABLE IF EXISTS collection_papers;
DROP TABLE IF EXISTS papers;
DROP TABLE IF EXISTS collections;
DROP TABLE IF EXISTS users;

-- ─────────────────────────────────────────────
-- USERS
-- ─────────────────────────────────────────────
CREATE TABLE users (
    id          INT          NOT NULL AUTO_INCREMENT,
    username    VARCHAR(100) NOT NULL,
    full_name   VARCHAR(200) DEFAULT NULL,
    email       VARCHAR(100) NOT NULL,
    password    VARCHAR(100) NOT NULL,
    role        VARCHAR(20)  NOT NULL DEFAULT 'USER',
    status      VARCHAR(20)  DEFAULT 'Active',
    PRIMARY KEY (id),
    UNIQUE KEY email (email),
    UNIQUE KEY username (username)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ─────────────────────────────────────────────
-- PAPERS
-- ─────────────────────────────────────────────
CREATE TABLE papers (
    id                INT          NOT NULL AUTO_INCREMENT,
    title             VARCHAR(500) NOT NULL,
    authors           VARCHAR(500) DEFAULT NULL,
    abstract_text     TEXT,
    publication_venue VARCHAR(200) DEFAULT NULL,
    publication_year  INT          DEFAULT NULL,
    doi               VARCHAR(200) DEFAULT NULL,
    file_path         VARCHAR(500) DEFAULT NULL,
    user_id           INT          NOT NULL,
    category          VARCHAR(50)  DEFAULT 'General',
    rating            INT          DEFAULT 0,
    PRIMARY KEY (id),
    KEY user_id (user_id),
    CONSTRAINT papers_ibfk_1 FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ─────────────────────────────────────────────
-- NOTES
-- ─────────────────────────────────────────────
CREATE TABLE notes (
    id         INT      NOT NULL AUTO_INCREMENT,
    paper_id   INT      NOT NULL,
    user_id    INT      NOT NULL,
    content    TEXT     NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY paper_id (paper_id),
    KEY user_id (user_id),
    CONSTRAINT notes_ibfk_1 FOREIGN KEY (paper_id) REFERENCES papers (id) ON DELETE CASCADE,
    CONSTRAINT notes_ibfk_2 FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ─────────────────────────────────────────────
-- COLLECTIONS
-- ─────────────────────────────────────────────
CREATE TABLE collections (
    id          INT          NOT NULL AUTO_INCREMENT,
    name        VARCHAR(200) NOT NULL,
    description TEXT,
    user_id     INT          NOT NULL,
    PRIMARY KEY (id),
    KEY user_id (user_id),
    CONSTRAINT collections_ibfk_1 FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ─────────────────────────────────────────────
-- COLLECTION-PAPER MAPPING
-- ─────────────────────────────────────────────
CREATE TABLE collection_papers (
    collection_id INT NOT NULL,
    paper_id      INT NOT NULL,
    PRIMARY KEY (collection_id, paper_id),
    KEY paper_id (paper_id),
    CONSTRAINT collection_papers_ibfk_1 FOREIGN KEY (collection_id) REFERENCES collections (id) ON DELETE CASCADE,
    CONSTRAINT collection_papers_ibfk_2 FOREIGN KEY (paper_id) REFERENCES papers (id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ─────────────────────────────────────────────
-- READING PROGRESS
-- ─────────────────────────────────────────────
CREATE TABLE reading_progress (
    id            INT      NOT NULL AUTO_INCREMENT,
    paper_id      INT      NOT NULL,
    user_id       INT      NOT NULL,
    status        VARCHAR(20) DEFAULT 'Not Started',
    current_page  INT      DEFAULT 0,
    total_pages   INT      DEFAULT 0,
    last_read_at  DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    KEY paper_id (paper_id),
    KEY user_id (user_id),
    CONSTRAINT reading_progress_ibfk_1 FOREIGN KEY (paper_id) REFERENCES papers (id) ON DELETE CASCADE,
    CONSTRAINT reading_progress_ibfk_2 FOREIGN KEY (user_id) REFERENCES users (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

-- ─────────────────────────────────────────────
-- SEED DATA
-- ─────────────────────────────────────────────

-- Test user (password: 111) — plain text for now, hashing added later
INSERT INTO users (id, username, full_name, email, password, role, status) VALUES
(1, 'doeuser', 'Doe User', '111', '111', 'USER', 'Active'),
(2, 'aaa', 'Admin User', 'aaa@gmail.com', '111', 'ADMIN', 'Active');

-- Sample papers
INSERT INTO papers (id, title, authors, abstract_text, publication_venue, publication_year, doi, file_path, user_id, category, rating) VALUES
(1, 'Deep Learning for Natural Language Processing', 'Johnson, M., Smith, K.', 'This paper presents a comprehensive survey of deep learning techniques applied to NLP tasks including text classification, named entity recognition, and machine translation.', 'Journal of Artificial Intelligence Research', 2024, '10.1234/jair.2024.001', NULL, 1, 'AI/ML', 4),
(2, 'Quantum Computing: A Survey', 'Patel, R., Chen, L., Williams, T.', 'A thorough survey of quantum computing paradigms, algorithms, and their potential applications in cryptography and optimization.', 'ACM Computing Surveys', 2023, '10.1234/csur.2023.002', NULL, 1, 'Quantum', 5),
(3, 'Advances in Reinforcement Learning', 'Zhang, Y., Kumar, A., Brown, S.', 'Recent advances in deep reinforcement learning algorithms including PPO, SAC, and model-based approaches with real-world applications.', 'Neural Information Processing Systems', 2024, '10.1234/nips.2024.003', NULL, 1, 'AI/ML', 3),
(4, 'Blockchain Technology in Healthcare', 'Lee, J., Garcia, M., Thompson, D.', 'Exploring blockchain applications for secure health data management, interoperability, and patient privacy.', 'IEEE Journal of Biomedical and Health Informatics', 2023, '10.1234/jbhi.2024.004', NULL, 1, 'Healthcare', 4),
(5, 'Sustainable Energy Systems', 'Anderson, P., Wilson, E., Davis, R.', 'Analysis of renewable energy integration strategies and smart grid technologies for sustainable power systems.', 'Renewable and Sustainable Energy Reviews', 2024, '10.1234/rser.2024.005', NULL, 1, 'General', 3),
(6, 'Cybersecurity Trends and Challenges', 'Martinez, A., Taylor, J., White, P.', 'A comprehensive review of emerging cybersecurity threats, defense mechanisms, and future research directions.', 'Computers and Security', 2023, '10.1234/cose.2023.006', NULL, 1, 'Blockchain', 5),
(7, 'Machine Learning in Drug Discovery', 'Kim, S., Liu, X., Wang, H.', 'Application of machine learning models for virtual screening, molecular docking, and drug repurposing.', 'Nature Computational Science', 2024, '10.1234/ncs.2024.007', NULL, 1, 'AI/ML', 2);

-- Sample collections
INSERT INTO collections (id, name, description, user_id) VALUES
(1, 'Machine Learning', 'Papers about ML and AI', 1),
(2, 'Computer Science', 'General CS papers', 1),
(3, 'Favorites', 'My favorite papers', 1);

-- Collection-paper mappings
INSERT INTO collection_papers (collection_id, paper_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 7),
(2, 2), (2, 4), (2, 6),
(3, 5), (3, 7);

-- Sample notes
INSERT INTO notes (id, paper_id, user_id, content) VALUES
(1, 1, 1, 'Important reference for my research on NLP methods.'),
(2, 2, 1, 'Need to follow up on quantum gates section.'),
(3, 3, 1, 'Good overview of RL algorithms.');

-- Reading progress
INSERT INTO reading_progress (id, paper_id, user_id, status, current_page, total_pages) VALUES
(1, 1, 1, 'Reading',    45, 120),
(2, 2, 1, 'Completed',   0,   0),
(3, 3, 1, 'Not Started', 0,   0),
(4, 4, 1, 'Reading',    30,  85),
(5, 5, 1, 'Completed',   0,   0),
(6, 6, 1, 'Not Started', 0,   0),
(7, 7, 1, 'Favorite',    0,   0);
