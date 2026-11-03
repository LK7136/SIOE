
-- on ajoute les perm a l' organisatuer car il pouvait pas modifier les groupes
INSERT INTO role_permission (id_permission, id_role) VALUES (49, 4) ON CONFLICT DO NOTHING; -- ADD_GRP
INSERT INTO role_permission (id_permission, id_role) VALUES (50, 4) ON CONFLICT DO NOTHING; -- DEL_GRP
INSERT INTO role_permission (id_permission, id_role) VALUES (51, 4) ON CONFLICT DO NOTHING; -- UPD_GRP
INSERT INTO role_permission (id_permission, id_role) VALUES (52, 4) ON CONFLICT DO NOTHING; -- READ_GRP