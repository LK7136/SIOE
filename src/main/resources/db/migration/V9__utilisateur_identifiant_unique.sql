-- V9 : garantit l'unicité de l'identifiant des utilisateurs.
-- La colonne est déjà UNIQUE dans 01-Base_HellMetz.sql : on ne crée la contrainte
-- que si aucun index unique n'existe déjà sur "identifiant" (donc sans risque de doublon).
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_index i
        JOIN pg_attribute a ON a.attrelid = i.indrelid AND a.attnum = ANY (i.indkey)
        WHERE i.indrelid = 'utilisateur'::regclass
          AND i.indisunique
          AND i.indnatts = 1
          AND a.attname = 'identifiant'
    ) THEN
ALTER TABLE utilisateur
    ADD CONSTRAINT uk_utilisateur_identifiant UNIQUE (identifiant);
END IF;
END $$;