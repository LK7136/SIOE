-- ==============================================================================
-- PROJET HELLMETZ : Déploiement de la programmation 2026 (Version MAX DATA)
-- Dates : 3, 4 et 5 Juillet 2026
-- Contenu : Scènes détaillées, Concerts avec balances, Groupes enrichis, Artistes avec biographies et caprices
-- ==============================================================================

SET search_path TO hellmetz;

-- 1. Nettoyage absolu pour éviter les doublons lors des tests
TRUNCATE TABLE artiste, groupe, concert, edition_festival, scene RESTART IDENTITY CASCADE;

-- ==============================================================================
-- 2. CRÉATION DES SCÈNES ET DE L'ÉDITION (Avec descriptions)
-- ==============================================================================

INSERT INTO scene (nom_scene, type_scene, capacite, superficie_m2, description, url_plan_technique) VALUES 
('Main Stage 1', 'Plein air', 35000, 800, 'La scène principale historique du festival. Équipée de murs d''écrans LED géants et d''un système pyrotechnique de pointe.', '/backoffice/documents/plans/mainstage1.pdf'),
('Main Stage 2', 'Plein air', 35000, 800, 'La scène jumelle, collée à la Main Stage 1. Permet d''enchaîner les concerts majeurs en moins de 5 minutes.', '/backoffice/documents/plans/mainstage2.pdf'),
('The Altar', 'Chapiteau', 12000, 400, 'Un immense chapiteau sombre et moite, sanctuaire dédié aux musiques extrêmes. L''acoustique y est massive et brutale.', '/backoffice/documents/plans/altar.pdf');

INSERT INTO edition_festival (annee, nom_edition, date_debut, date_fin, lieu, jauge_maximale, theme, budget_previsionnel) VALUES 
(2026, 'HellMetz 2026 - The Rebirth', '2026-07-03 12:00:00', '2026-07-05 23:59:59', 'Metz Expo', 82000, 'Apocalypse & Résurrection', 15500000.00);

-- ==============================================================================
-- 3. CRÉATION DES 50 CONCERTS (Avec réglages sonores : Balances et Décibels)
-- Règle : Les balances sonores (Soundcheck) durent 45 min et se font le matin.
-- ==============================================================================

INSERT INTO concert (date_heure_debut, date_heure_fin, heure_balance_debut, heure_balance_fin, decibels_max, id_scene, id_edition) VALUES 
-- JOUR 1 : VENDREDI 3 JUILLET 2026
('2026-07-03 14:00:00', '2026-07-03 14:45:00', '2026-07-03 08:00:00', '2026-07-03 08:45:00', 100, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'), 1),
('2026-07-03 15:00:00', '2026-07-03 15:45:00', '2026-07-03 09:00:00', '2026-07-03 09:45:00', 100, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'), 1),
('2026-07-03 15:30:00', '2026-07-03 16:20:00', '2026-07-03 08:30:00', '2026-07-03 09:15:00', 102, (SELECT id_scene FROM scene WHERE nom_scene='The Altar'), 1),
('2026-07-03 16:00:00', '2026-07-03 16:45:00', '2026-07-03 10:00:00', '2026-07-03 10:45:00', 102, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'), 1),
('2026-07-03 17:00:00', '2026-07-03 17:50:00', '2026-07-03 11:00:00', '2026-07-03 11:45:00', 102, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'), 1),
('2026-07-03 17:30:00', '2026-07-03 18:20:00', '2026-07-03 10:30:00', '2026-07-03 11:15:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='The Altar'), 1),
('2026-07-03 18:00:00', '2026-07-03 18:50:00', '2026-07-03 12:00:00', '2026-07-03 12:45:00', 102, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'), 1),
('2026-07-03 19:00:00', '2026-07-03 20:00:00', '2026-07-03 13:00:00', '2026-07-03 13:45:00', 102, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'), 1),
('2026-07-03 19:30:00', '2026-07-03 20:30:00', '2026-07-03 12:30:00', '2026-07-03 13:15:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='The Altar'), 1),
('2026-07-03 20:00:00', '2026-07-03 21:00:00', '2026-07-02 14:00:00', '2026-07-02 15:30:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'), 1),
('2026-07-03 21:00:00', '2026-07-03 22:15:00', '2026-07-02 16:00:00', '2026-07-02 17:30:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'), 1),
('2026-07-03 21:30:00', '2026-07-03 22:30:00', '2026-07-02 18:00:00', '2026-07-02 19:30:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='The Altar'), 1),
('2026-07-03 22:00:00', '2026-07-03 23:15:00', '2026-07-02 20:00:00', '2026-07-02 21:30:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'), 1),
('2026-07-03 23:00:00', '2026-07-04 01:00:00', '2026-07-02 22:00:00', '2026-07-02 23:30:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'), 1),
('2026-07-03 23:30:00', '2026-07-04 00:30:00', '2026-07-03 01:00:00', '2026-07-03 02:00:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='The Altar'), 1),
('2026-07-04 01:00:00', '2026-07-04 02:00:00', '2026-07-03 02:00:00', '2026-07-03 03:00:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='The Altar'), 1),

-- JOUR 2 : SAMEDI 4 JUILLET 2026
('2026-07-04 13:00:00', '2026-07-04 13:45:00', '2026-07-04 08:00:00', '2026-07-04 08:45:00', 100, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'), 1),
('2026-07-04 14:00:00', '2026-07-04 14:45:00', '2026-07-04 09:00:00', '2026-07-04 09:45:00', 100, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'), 1),
('2026-07-04 14:30:00', '2026-07-04 15:20:00', '2026-07-04 08:30:00', '2026-07-04 09:15:00', 102, (SELECT id_scene FROM scene WHERE nom_scene='The Altar'), 1),
('2026-07-04 15:00:00', '2026-07-04 15:50:00', '2026-07-04 10:00:00', '2026-07-04 10:45:00', 102, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'), 1),
('2026-07-04 16:00:00', '2026-07-04 16:50:00', '2026-07-04 11:00:00', '2026-07-04 11:45:00', 102, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'), 1),
('2026-07-04 16:30:00', '2026-07-04 17:20:00', '2026-07-04 10:30:00', '2026-07-04 11:15:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='The Altar'), 1),
('2026-07-04 17:00:00', '2026-07-04 17:50:00', '2026-07-04 12:00:00', '2026-07-04 12:45:00', 102, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'), 1),
('2026-07-04 18:00:00', '2026-07-04 19:00:00', '2026-07-04 13:00:00', '2026-07-04 13:45:00', 102, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'), 1),
('2026-07-04 18:30:00', '2026-07-04 19:30:00', '2026-07-04 12:30:00', '2026-07-04 13:15:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='The Altar'), 1),
('2026-07-04 19:00:00', '2026-07-04 20:00:00', '2026-07-04 14:00:00', '2026-07-04 14:45:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'), 1),
('2026-07-04 20:00:00', '2026-07-04 21:15:00', '2026-07-04 15:00:00', '2026-07-04 15:45:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'), 1),
('2026-07-04 20:30:00', '2026-07-04 21:30:00', '2026-07-04 14:30:00', '2026-07-04 15:15:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='The Altar'), 1),
('2026-07-04 21:00:00', '2026-07-04 22:15:00', '2026-07-04 16:00:00', '2026-07-04 16:45:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'), 1),
('2026-07-04 22:00:00', '2026-07-04 23:30:00', '2026-07-04 17:00:00', '2026-07-04 17:45:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'), 1),
('2026-07-04 22:30:00', '2026-07-04 23:30:00', '2026-07-04 16:30:00', '2026-07-04 17:15:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='The Altar'), 1),
('2026-07-04 23:00:00', '2026-07-05 00:15:00', '2026-07-04 18:00:00', '2026-07-04 18:45:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'), 1),
('2026-07-05 00:00:00', '2026-07-05 02:00:00', '2026-07-04 19:00:00', '2026-07-04 19:45:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'), 1),

-- JOUR 3 : DIMANCHE 5 JUILLET 2026
('2026-07-05 13:00:00', '2026-07-05 13:45:00', '2026-07-05 08:00:00', '2026-07-05 08:45:00', 100, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'), 1),
('2026-07-05 14:00:00', '2026-07-05 14:45:00', '2026-07-05 09:00:00', '2026-07-05 09:45:00', 100, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'), 1),
('2026-07-05 14:30:00', '2026-07-05 15:20:00', '2026-07-05 08:30:00', '2026-07-05 09:15:00', 102, (SELECT id_scene FROM scene WHERE nom_scene='The Altar'), 1),
('2026-07-05 15:00:00', '2026-07-05 15:50:00', '2026-07-05 10:00:00', '2026-07-05 10:45:00', 102, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'), 1),
('2026-07-05 16:00:00', '2026-07-05 16:50:00', '2026-07-05 11:00:00', '2026-07-05 11:45:00', 102, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'), 1),
('2026-07-05 16:30:00', '2026-07-05 17:20:00', '2026-07-05 10:30:00', '2026-07-05 11:15:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='The Altar'), 1),
('2026-07-05 17:00:00', '2026-07-05 17:50:00', '2026-07-05 12:00:00', '2026-07-05 12:45:00', 102, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'), 1),
('2026-07-05 18:00:00', '2026-07-05 19:00:00', '2026-07-05 13:00:00', '2026-07-05 13:45:00', 102, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'), 1),
('2026-07-05 18:30:00', '2026-07-05 19:30:00', '2026-07-05 12:30:00', '2026-07-05 13:15:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='The Altar'), 1),
('2026-07-05 19:00:00', '2026-07-05 20:00:00', '2026-07-05 14:00:00', '2026-07-05 14:45:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'), 1),
('2026-07-05 20:00:00', '2026-07-05 21:15:00', '2026-07-05 15:00:00', '2026-07-05 15:45:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'), 1),
('2026-07-05 20:30:00', '2026-07-05 21:30:00', '2026-07-05 14:30:00', '2026-07-05 15:15:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='The Altar'), 1),
('2026-07-05 21:00:00', '2026-07-05 22:15:00', '2026-07-05 16:00:00', '2026-07-05 16:45:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'), 1),
('2026-07-05 22:00:00', '2026-07-05 23:15:00', '2026-07-05 17:00:00', '2026-07-05 17:45:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'), 1),
('2026-07-05 22:30:00', '2026-07-05 23:30:00', '2026-07-05 16:30:00', '2026-07-05 17:15:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='The Altar'), 1),
('2026-07-05 23:00:00', '2026-07-06 00:30:00', '2026-07-05 18:00:00', '2026-07-05 18:45:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'), 1),
('2026-07-06 00:30:00', '2026-07-06 01:30:00', '2026-07-05 19:00:00', '2026-07-05 19:45:00', 105, (SELECT id_scene FROM scene WHERE nom_scene='The Altar'), 1);

-- ==============================================================================
-- 4. CRÉATION DES 50 GROUPES (AFFECTÉS À LEUR CONCERT AVEC INFOS DÉTAILLÉES)
-- ==============================================================================

INSERT INTO groupe (nom_groupe, description, pays_origine, ville_origine, annee_creation, site_web, email_contact, telephone_contact, id_concert) VALUES 
-- JOUR 1 (Vendredi)
('Landmvrks', 'La nouvelle génération explosive du Metalcore marseillais.', 'France', 'Marseille', 2014, 'www.landmvrks.com', 'booking@landmvrks.fr', '+33-4-123123', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-03 14:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'))),
('Lofofora', 'Pionniers du Metal Fusion en France avec des textes engagés.', 'France', 'Paris', 1989, 'www.lofofora.com', 'tour@lofofora.fr', '+33-1-112233', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-03 15:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'))),
('Igorrr', 'Génie de l''expérimentation : Breakcore, Death Metal et musique baroque.', 'France', 'Paris', 2005, 'www.igorrr.com', 'contact@igorrr.com', '+33-1-998877', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-03 15:30:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='The Altar'))),
('Mass Hysteria', 'Piliers du Metal Industriel français, énergie pure en live.', 'France', 'Paris', 1993, 'www.masshysteria.com', 'contact@masshysteria.fr', '+33-1-987654', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-03 16:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'))),
('Trivium', 'Mélange parfait de Metalcore et de Thrash Metal.', 'États-Unis', 'Orlando', 1999, 'www.trivium.org', 'tour@trivium.org', '+1-555-0303', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-03 17:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'))),
('Alcest', 'Créateurs du Blackgaze, mélange de Black Metal et de Shoegaze.', 'France', 'Bagnols-sur-Cèze', 2000, 'www.alcest-music.com', 'booking@alcest.fr', '+33-4-556677', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-03 17:30:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='The Altar'))),
('Anthrax', 'Le côté fun et punk du Big Four du Thrash américain.', 'États-Unis', 'New York', 1981, 'www.anthrax.com', 'contact@anthrax.com', '+1-555-0104', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-03 18:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'))),
('Megadeth', 'Piliers du Thrash Metal, menés par Dave Mustaine.', 'États-Unis', 'Los Angeles', 1983, 'www.megadeth.com', 'tour@megadeth.com', '+1-555-0102', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-03 19:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'))),
('Meshuggah', 'Inventeurs du Djent, polyrythmies complexes et son lourd.', 'Suède', 'Umeå', 1987, 'www.meshuggah.net', 'contact@meshuggah.se', '+46-8-99999', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-03 19:30:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='The Altar'))),
('Lamb of God', 'Leaders du Groove Metal moderne et de la NWOAHM.', 'États-Unis', 'Richmond', 1994, 'www.lamb-of-god.com', 'booking@lambofgod.com', '+1-555-0302', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-03 20:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'))),
('Slayer', 'Le groupe de Thrash Metal le plus agressif du Big Four.', 'États-Unis', 'Huntington Park', 1981, 'www.slayer.net', 'booking@slayer.net', '+1-555-0103', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-03 21:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'))),
('Death', 'Les pères fondateurs du Death Metal.', 'États-Unis', 'Orlando', 1983, 'www.emptywords.org', 'legacy@death.com', '+1-555-0401', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-03 21:30:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='The Altar'))),
('Pantera', 'Créateurs du Groove Metal, avec les riffs légendaires de Dimebag Darrell.', 'États-Unis', 'Arlington', 1981, 'www.pantera.com', 'legacy@pantera.com', '+1-555-0301', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-03 22:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'))),
('Metallica', 'Pionniers du Thrash Metal, l''un des groupes les plus influents au monde.', 'États-Unis', 'Los Angeles', 1981, 'www.metallica.com', 'booking@metallica.com', '+1-555-0101', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-03 23:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'))),
('Cannibal Corpse', 'Légendes du Brutal Death Metal américain.', 'États-Unis', 'Buffalo', 1988, 'www.cannibalcorpse.net', 'tour@cannibalcorpse.net', '+1-555-0402', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-03 23:30:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='The Altar'))),
('Behemoth', 'Mastodontes du Blackened Death Metal polonais.', 'Pologne', 'Gdańsk', 1991, 'www.behemoth.pl', 'booking@behemoth.pl', '+48-58-1234', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-04 01:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='The Altar'))),

-- JOUR 2 (Samedi)
('Parkway Drive', 'Les rois du Metalcore australien, maîtres des breakdowns.', 'Australie', 'Byron Bay', 2003, 'www.parkwaydriverock.com', 'booking@parkwaydrive.au', '+61-2-12345', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-04 13:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'))),
('Architects', 'Metalcore britannique technique et engagé émotionnellement.', 'Royaume-Uni', 'Brighton', 2004, 'www.architectsofficial.com', 'contact@architects.uk', '+44-20-8888', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-04 14:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'))),
('Bring Me The Horizon', 'Évolution fascinante du Deathcore vers le Rock Alternatif.', 'Royaume-Uni', 'Sheffield', 2004, 'www.bmthofficial.com', 'tour@bmth.uk', '+44-20-7777', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-04 14:30:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='The Altar'))),
('Epica', 'Metal symphonique néerlandais aux accents death et progressifs.', 'Pays-Bas', 'Reuver', 2002, 'www.epica.nl', 'booking@epica.nl', '+31-20-1234', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-04 15:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'))),
('Nightwish', 'Pionniers du Metal Symphonique majestueux.', 'Finlande', 'Kitee', 1996, 'www.nightwish.com', 'tour@nightwish.fi', '+358-9-1234', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-04 16:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'))),
('Powerwolf', 'Power Metal lourd sur le thème des loups-garous et de la religion.', 'Allemagne', 'Sarrebruck', 2003, 'www.powerwolf.net', 'tour@powerwolf.de', '+49-681-1234', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-04 16:30:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='The Altar'))),
('Sabaton', 'Power Metal suédois épique basé sur l''histoire militaire.', 'Suède', 'Falun', 1999, 'www.sabaton.net', 'booking@sabaton.net', '+46-8-44444', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-04 17:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'))),
('Ghost', 'Secte rock théâtrale menée par Papa Emeritus/Tobias Forge.', 'Suède', 'Linköping', 2006, 'www.ghost-official.com', 'clergy@ghost-official.com', '+46-8-55555', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-04 18:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'))),
('Alestorm', 'Inventeurs du "Pirate Metal", pour faire la fête avec du rhum.', 'Royaume-Uni', 'Perth', 2004, 'www.alestorm.net', 'booking@alestorm.net', '+44-20-5555', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-04 18:30:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='The Altar'))),
('Deftones', 'Metal alternatif atmosphérique et planant.', 'États-Unis', 'Sacramento', 1988, 'www.deftones.com', 'tour@deftones.com', '+1-555-0204', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-04 19:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'))),
('Korn', 'Les pionniers absolus du Nu Metal à la basse slappée.', 'États-Unis', 'Bakersfield', 1993, 'www.korn.com', 'booking@korn.com', '+1-555-0203', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-04 20:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'))),
('Blind Guardian', 'Power Metal épique inspiré par l''univers de Tolkien.', 'Allemagne', 'Krefeld', 1984, 'www.blind-guardian.com', 'contact@blind-guardian.de', '+49-2151-1234', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-04 20:30:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='The Altar'))),
('System of a Down', 'Metal alternatif aux influences arméniennes, unique en son genre.', 'États-Unis', 'Glendale', 1994, 'www.systemofadown.com', 'contact@soad.com', '+1-555-0202', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-04 21:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'))),
('Slipknot', 'Nu metal chaotique et visuel emblématique avec leurs masques.', 'États-Unis', 'Des Moines', 1995, 'www.slipknot1.com', 'maggots@slipknot1.com', '+1-555-0201', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-04 22:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'))),
('Amon Amarth', 'Death Metal Mélodique sur le thème épique des Vikings.', 'Suède', 'Tumba', 1992, 'www.amonamarth.com', 'booking@amonamarth.se', '+46-8-12345', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-04 22:30:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='The Altar'))),
('Disturbed', 'Metal alternatif puissant, célèbre pour la voix de David Draiman.', 'États-Unis', 'Chicago', 1994, 'www.disturbed1.com', 'booking@disturbed.com', '+1-555-0205', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-04 23:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'))),
('Rammstein', 'Ténors du Neue Deutsche Härte, shows pyrotechniques monumentaux.', 'Allemagne', 'Berlin', 1994, 'www.rammstein.de', 'booking@rammstein.de', '+49-30-12345', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-05 00:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'))),

-- JOUR 3 (Dimanche)
('Scorpions', 'Hard Rock allemand légendaire, rois des ballades.', 'Allemagne', 'Hanovre', 1965, 'www.the-scorpions.com', 'contact@scorpions.de', '+49-511-1234', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-05 13:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'))),
('Aerosmith', 'Les bad boys de Boston, blues rock survolté.', 'États-Unis', 'Boston', 1970, 'www.aerosmith.com', 'booking@aerosmith.com', '+1-555-0502', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-05 14:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'))),
('Deep Purple', 'Pionniers du Hard Rock avec le riff le plus célèbre du monde.', 'Royaume-Uni', 'Hertford', 1968, 'www.deep-purple.com', 'tour@deep-purple.com', '+44-20-4444', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-05 14:30:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='The Altar'))),
('KISS', 'Les rois du show rock, maquillage et rock ''n'' roll toute la nuit.', 'États-Unis', 'New York', 1973, 'www.kissonline.com', 'tour@kissonline.com', '+1-555-0503', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-05 15:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'))),
('Pink Floyd', 'Maîtres incontestés du Rock Progressif et psychédélique.', 'Royaume-Uni', 'Londres', 1965, 'www.pinkfloyd.com', 'contact@pinkfloyd.com', '+44-20-3333', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-05 16:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'))),
('Led Zeppelin', 'Les pères du Hard Rock et du Rock psychédélique lourd.', 'Royaume-Uni', 'Londres', 1968, 'www.ledzeppelin.com', 'legacy@ledzeppelin.com', '+44-20-2222', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-05 16:30:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='The Altar'))),
('Queen', 'Légende absolue du Rock de stade et de l''opéra rock.', 'Royaume-Uni', 'Londres', 1970, 'www.queenonline.com', 'contact@queenonline.com', '+44-20-1111', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-05 17:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'))),
('Guns N'' Roses', 'Le groupe le plus dangereux du monde dans les années 80.', 'États-Unis', 'Los Angeles', 1985, 'www.gunsnroses.com', 'booking@gnr.com', '+1-555-0501', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-05 18:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'))),
('AC/DC', 'Le Hard Rock le plus pur et efficace de l''histoire.', 'Australie', 'Sydney', 1973, 'www.acdc.com', 'tour@acdc.com', '+61-2-98765', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-05 18:30:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='The Altar'))),
('Judas Priest', 'Les "Metal Gods", inventeurs du look cuir et clous.', 'Royaume-Uni', 'Birmingham', 1969, 'www.judaspriest.com', 'booking@judaspriest.com', '+44-20-5678', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-05 19:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'))),
('Motörhead', 'Groupe mythique de Lemmy Kilmister, un rock ''n'' roll sous stéroïdes.', 'Royaume-Uni', 'Londres', 1975, 'www.imotorhead.com', 'rip@imotorhead.com', '+44-20-9999', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-05 20:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'))),
('Black Sabbath', 'Les inventeurs du Heavy Metal, portés par Ozzy Osbourne.', 'Royaume-Uni', 'Birmingham', 1968, 'www.blacksabbath.com', 'management@blacksabbath.com', '+44-20-4321', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-05 20:30:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='The Altar'))),
('Iron Maiden', 'Légendes de la New Wave of British Heavy Metal (NWOBHM).', 'Royaume-Uni', 'Londres', 1975, 'www.ironmaiden.com', 'contact@ironmaiden.com', '+44-20-1234', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-05 21:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'))),
('Arch Enemy', 'Death Metal Mélodique suédois avec un chant féminin surpuissant.', 'Suède', 'Halmstad', 1995, 'www.archenemy.net', 'contact@archenemy.se', '+46-8-67890', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-05 22:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 2'))),
('In Flames', 'Pionniers du "Gothenburg sound" (Melodeath).', 'Suède', 'Göteborg', 1990, 'www.inflames.com', 'tour@inflames.se', '+46-8-11223', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-05 22:30:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='The Altar'))),
('Gojira', 'Le mastodonte français du Death Metal Progressif écologique.', 'France', 'Ondres', 1996, 'www.gojira-music.com', 'booking@gojira.fr', '+33-1-234567', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-05 23:00:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='Main Stage 1'))),
('Linkin Park', 'Mélange révolutionnaire de Nu Metal, rap et musique électronique.', 'États-Unis', 'Agoura Hills', 1996, 'www.linkinpark.com', 'contact@linkinpark.com', '+1-555-0206', (SELECT id_concert FROM concert WHERE date_heure_debut='2026-07-06 00:30:00' AND id_scene=(SELECT id_scene FROM scene WHERE nom_scene='The Altar')));

-- ==============================================================================
-- 5. CRÉATION DES ARTISTES (Membres des groupes, avec cachets, bios et caprices)
-- ==============================================================================

INSERT INTO artiste (nom, prenom, nom_scene, nationalite, biographie, cachet, exigences_catering, id_groupe) VALUES 
-- Metallica
('Hetfield', 'James', 'Papa Het', 'Américaine', 'Chanteur et guitariste rythmique, célèbre pour son attaque féroce au médiator.', 120000.00, 'Uniquement de l''eau minérale de source volcanique et des cigares cubains.', (SELECT id_groupe FROM groupe WHERE nom_groupe = 'Metallica')),
('Ulrich', 'Lars', 'Lars', 'Danoise', 'Batteur fondateur, réputé pour son jeu très visuel et parfois approximatif.', 120000.00, 'Collection de thés rares, température de la loge à exactement 21°C.', (SELECT id_groupe FROM groupe WHERE nom_groupe = 'Metallica')),
('Hammett', 'Kirk', 'The Ripper', 'Américaine', 'Guitariste soliste, maître absolu de la pédale Wah-Wah.', 100000.00, 'Des planches de surf dans la loge pour se détendre avant le show.', (SELECT id_groupe FROM groupe WHERE nom_groupe = 'Metallica')),

-- Iron Maiden
('Dickinson', 'Bruce', 'The Air Raid Siren', 'Britannique', 'Chanteur emblématique, également pilote de ligne, escrimeur et écrivain.', 90000.00, 'Du thé anglais Earl Grey et des bières Trooper obligatoires.', (SELECT id_groupe FROM groupe WHERE nom_groupe = 'Iron Maiden')),
('Harris', 'Steve', 'Harry', 'Britannique', 'Bassiste et principal compositeur, connu pour son jeu à deux doigts façon "galop".', 95000.00, 'Un terrain de football miniature installé derrière la scène.', (SELECT id_groupe FROM groupe WHERE nom_groupe = 'Iron Maiden')),

-- Black Sabbath
('Osbourne', 'John Michael', 'Ozzy Osbourne', 'Britannique', 'Le Prince des Ténèbres, figure légendaire et excentrique du heavy metal.', 150000.00, 'Interdiction absolue d''avoir des chauve-souris volantes à proximité de la scène.', (SELECT id_groupe FROM groupe WHERE nom_groupe = 'Black Sabbath')),
('Iommi', 'Tony', 'The Riffmaster', 'Britannique', 'L''homme qui a inventé les riffs lourds suite à un accident d''usine.', 110000.00, 'Un accordeur de guitare personnel disponible 24h/24.', (SELECT id_groupe FROM groupe WHERE nom_groupe = 'Black Sabbath')),

-- Rammstein
('Lindemann', 'Till', 'Till', 'Allemande', 'Chanteur à la voix de baryton, pyrotechnicien certifié aimant cracher du feu.', 180000.00, 'Des extincteurs de couleur noire pour ne pas gâcher la décoration de la loge.', (SELECT id_groupe FROM groupe WHERE nom_groupe = 'Rammstein')),
('Kruspe', 'Richard', 'Z. Kruspe', 'Allemande', 'Guitariste soliste et fondateur du groupe, créateur du son indus de Rammstein.', 130000.00, 'Un tapis roulant dans la loge pour s''échauffer en jouant.', (SELECT id_groupe FROM groupe WHERE nom_groupe = 'Rammstein')),

-- Gojira
('Duplantier', 'Joseph', 'Joe', 'Française', 'Chanteur et guitariste engagé pour la défense de l''environnement.', 60000.00, 'Buffet 100% vegan, produits locaux, zéro déchet plastique toléré.', (SELECT id_groupe FROM groupe WHERE nom_groupe = 'Gojira')),
('Duplantier', 'Mario', 'Mario', 'Française', 'Considéré comme l''un des meilleurs batteurs de metal au monde, puissant et technique.', 60000.00, 'Un chevalet de peinture dans la loge (Mario est aussi peintre).', (SELECT id_groupe FROM groupe WHERE nom_groupe = 'Gojira')),

-- Slipknot
('Taylor', 'Corey', 'Number 8', 'Américaine', 'Chanteur charismatique à la voix surpuissante, leader sans masque de Stone Sour.', 95000.00, 'Une réserve infinie de café glacé et des serviettes noires.', (SELECT id_groupe FROM groupe WHERE nom_groupe = 'Slipknot')),
('Crahan', 'Shawn', 'Clown (Number 6)', 'Américaine', 'Percussionniste et tête pensante visuelle du groupe. Frappe sur des fûts de bière en acier.', 80000.00, 'Des battes de baseball en aluminium de rechange pour détruire la scène.', (SELECT id_groupe FROM groupe WHERE nom_groupe = 'Slipknot')),

-- AC/DC
('Young', 'Angus', 'Angus', 'Australienne', 'L''écolier le plus célèbre du rock, connu pour son duckwalk frénétique.', 200000.00, 'Trois uniformes d''écolier propres et repassés par jour.', (SELECT id_groupe FROM groupe WHERE nom_groupe = 'AC/DC')),
('Johnson', 'Brian', 'Brian', 'Britannique', 'Chanteur à la voix éraillée légendaire, reconnaissable à sa casquette de gavroche.', 180000.00, 'Un assortiment de fromages français et un billard américain.', (SELECT id_groupe FROM groupe WHERE nom_groupe = 'AC/DC')),

-- System Of A Down
('Tankian', 'Serj', 'Serj', 'Arméno-Américaine', 'Chanteur aux multiples octaves, poète et militant politique actif.', 85000.00, 'De l''encens, du houmous maison et un silence absolu 1h avant le concert.', (SELECT id_groupe FROM groupe WHERE nom_groupe = 'System of a Down')),

-- Ghost
('Forge', 'Tobias', 'Papa Emeritus', 'Suédoise', 'Le cerveau derrière le groupe, incarnant différents "Papes" démoniaques selon les albums.', 105000.00, 'Un miroir sur pied de 2 mètres, du vin rouge italien millésimé et du maquillage pro.', (SELECT id_groupe FROM groupe WHERE nom_groupe = 'Ghost')),

-- Queen (Légendes)
('Bulsara', 'Farrokh', 'Freddie Mercury', 'Britannique', 'L''un des plus grands chanteurs de l''histoire de la musique rock. Showman absolu.', 300000.00, 'Du champagne Moët & Chandon millésimé, et des fleurs jaunes fraîches.', (SELECT id_groupe FROM groupe WHERE nom_groupe = 'Queen')),
('May', 'Brian', 'Brian May', 'Britannique', 'Guitariste de génie et Docteur en astrophysique, joue avec une pièce de monnaie.', 180000.00, 'Un télescope installé en coulisses si le ciel est dégagé.', (SELECT id_groupe FROM groupe WHERE nom_groupe = 'Queen')),

-- Motörhead
('Kilmister', 'Ian', 'Lemmy', 'Britannique', 'L''incarnation même du Rock ''n'' Roll, jouant de sa basse comme d''une guitare rythmique.', 100000.00, 'Une bouteille de Jack Daniels, du Coca-Cola et deux paquets de Marlboro Red.', (SELECT id_groupe FROM groupe WHERE nom_groupe = 'Motörhead')),

-- Guns N' Roses
('Rose', 'William', 'Axl Rose', 'Américaine', 'Frontman rebelle, célèbre pour son registre vocal extrêmement étendu et son caractère explosif.', 175000.00, 'Des roses blanches sans épines, des pizzas carrées (et non rondes).', (SELECT id_groupe FROM groupe WHERE nom_groupe = 'Guns N'' Roses')),
('Hudson', 'Saul', 'Slash', 'Américano-Britannique', 'Le guitariste au haut-de-forme, auteur de solos ayant marqué des générations.', 175000.00, 'Des bonbons Skittles, mais en retirant tous les verts.', (SELECT id_groupe FROM groupe WHERE nom_groupe = 'Guns N'' Roses'));