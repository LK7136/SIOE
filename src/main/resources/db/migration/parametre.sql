CREATE TABLE parametre (
                           id_parametre      BIGSERIAL    PRIMARY KEY,
                           code_parametre    VARCHAR(100) NOT NULL UNIQUE,
                           libelle_parametre VARCHAR(100) NOT NULL,
                           valeur_parametre  VARCHAR(100)
);

INSERT INTO parametre (code_parametre, libelle_parametre, valeur_parametre) VALUES
                                                                                ('app.nom',                          'Nom de l''application',         'HellMetz'),
                                                                                ('festival.edition',                 'Édition du festival',           'HellMetz 2026'),
                                                                                ('festival.date_debut',              'Date de début',                 '2026-07-10'),
                                                                                ('festival.date_fin',                'Date de fin',                   '2026-07-12'),
                                                                                ('programmation.temps_entre_concerts','Temps entre deux concerts',    '30'),
                                                                                ('programmation.temps_balance',      'Temps de balance',              '45'),
                                                                                ('contact.email',                    'E-mail de contact',             'contact@hellmetz.fr'),
                                                                                ('contact.telephone',                'Téléphone',                     '+33387000000'),
                                                                                ('affichage.elements_par_page',      'Éléments par page',             '10'),
                                                                                ('affichage.theme',                  'Thème par défaut',              'Sombre');