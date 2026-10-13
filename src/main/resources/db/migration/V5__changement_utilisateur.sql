ALTER TABLE utilisateur
    ADD COLUMN id_role INT;

ALTER TABLE utilisateur
    ADD CONSTRAINT fk_utilisateur_role
        FOREIGN KEY (id_role)
            REFERENCES role(id_role);

UPDATE utilisateur u
SET id_role = r.id_role
FROM role r
WHERE r.code_role = 'ADMIN';

UPDATE utilisateur u
SET id_role = r.id_role
FROM role r
WHERE r.code_role = 'BENEVOLE';

UPDATE utilisateur u
SET id_role = r.id_role
FROM role r
WHERE r.code_role = 'ORGANISATEUR';

UPDATE utilisateur u
SET id_role = r.id_role
FROM role r
WHERE r.code_role = 'ADMIN'
  AND u.id_utilisateur = 1;