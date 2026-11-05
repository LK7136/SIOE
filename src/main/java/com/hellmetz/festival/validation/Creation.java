package com.hellmetz.festival.validation;

import jakarta.validation.groups.Default;

/**
 * Groupe de validation : règles qui ne s'appliquent qu'à la CRÉATION d'un utilisateur
 * (ex. : mot de passe obligatoire). Il hérite de Default : toutes les règles
 * "classiques" s'appliquent donc aussi.
 */
public interface Creation extends Default {}