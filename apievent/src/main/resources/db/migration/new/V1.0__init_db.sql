-- Drop tables if they exist
DROP TABLE IF EXISTS adresse CASCADE;
DROP TABLE IF EXISTS event CASCADE;

-- Create table for Adresse
CREATE TABLE adresse (
                         id BIGSERIAL PRIMARY KEY,
                         ville VARCHAR(255) NOT NULL,
                         code_postal VARCHAR(7) NOT NULL,
                         intitule_adresse VARCHAR(255) NOT NULL,
                         pays VARCHAR(255) NOT NULL
);

-- Create table for Event
CREATE TABLE event (
        id BIGSERIAL PRIMARY KEY,
        name VARCHAR(255) NOT NULL,
        description TEXT, -- @Lob et columnDefinition = "TEXT"
        start_date TIMESTAMP,  -- LocalDateTime est généralement mappé à TIMESTAMP
        end_date TIMESTAMP
    );


CREATE TABLE IF NOT EXISTS lien_adresse_event (
                                                  event_id BIGINT NOT NULL,
                                                  adresse_id BIGINT NOT NULL,
                                                  PRIMARY KEY (event_id, adresse_id),
    CONSTRAINT fk_event FOREIGN KEY(event_id) REFERENCES event(id)
    ON DELETE CASCADE,
    CONSTRAINT fk_adresse FOREIGN KEY(adresse_id) REFERENCES adresse(id)
    ON DELETE RESTRICT
    );