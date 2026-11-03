
-- on ajoute les perm a l' organisatuer car il pouvait pas modifier les groupes
INSERT INTO role_permission (id_permission, id_role) VALUES (49, 4); -- ADD_GRP
INSERT INTO role_permission (id_permission, id_role) VALUES (50, 4); -- DEL_GRP
INSERT INTO role_permission (id_permission, id_role) VALUES (51, 4); -- UPD_GRP
INSERT INTO role_permission (id_permission, id_role) VALUES (52, 4); -- READ_GRP