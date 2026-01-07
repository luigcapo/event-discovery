ALTER TABLE adresse ADD COLUMN numero VARCHAR(10);
ALTER TABLE adresse RENAME COLUMN intitule_adresse TO rue;
ALTER TABLE lien_adresse_event RENAME TO lien_event_adresse;