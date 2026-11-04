package com.hellmetz.festival.service;

import com.hellmetz.festival.model.Concert;
import com.hellmetz.festival.model.Edition;
import com.hellmetz.festival.model.Groupe;
import com.hellmetz.festival.model.Parametre;
import com.hellmetz.festival.repository.ConcertRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Service
@Transactional
public class ConcertService {

    private final ConcertRepository concertRepository;
    private final ParametreService parametreService;
    private static final Sort TRI_CONCERT = Sort.by("id").ascending();

    /** Heure proposée par défaut à la création d'un concert. */
    public static final LocalTime HEURE_DEBUT_PAR_DEFAUT = LocalTime.of(18, 0);

    /** Format attendu par les champs HTML datetime-local (min / max / value). */
    public static final DateTimeFormatter FMT_DATETIME_LOCAL =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");

    private static final DateTimeFormatter FMT_AFFICHAGE =
            DateTimeFormatter.ofPattern("dd/MM/yyyy 'à' HH:mm");

    /** Fenêtre autorisée pour les concerts : [début 00:00 ; fin 23:59]. */
    public record FenetreFestival(LocalDateTime debut, LocalDateTime fin) {
        public String debutHtml() { return debut.format(FMT_DATETIME_LOCAL); }
        public String finHtml()   { return fin.format(FMT_DATETIME_LOCAL); }
    }

    public ConcertService(ConcertRepository concertRepository, ParametreService parametreService) {
        this.concertRepository = concertRepository;
        this.parametreService = parametreService;
    }

    @Transactional(readOnly = true)
    public Page<Concert> findPage(int page, int taille) {
        return concertRepository.findAll(PageRequest.of(page, taille, TRI_CONCERT));
    }

    @Transactional(readOnly = true)
    public Page<Concert> findTout() {
        return concertRepository.findAll(Pageable.unpaged(TRI_CONCERT));
    }

    @Transactional(readOnly = true)
    public List<Concert> findAll() {
        return concertRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Concert findById(Long id) {
        return concertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Concert introuvable : " + id));
    }

    @Transactional(readOnly = true)
    public List<Concert> findByGroupeId(Long groupeId) {
        return concertRepository.findByGroupeId(groupeId);
    }


    @Transactional(readOnly = true)
    public Page<Concert> findDisponiblesOuAnnulesOuGroupe(Long groupeId, int page, int taille) {
        return concertRepository.findDisponiblesOuAnnulesOuGroupe(groupeId, PageRequest.of(page, taille, TRI_CONCERT));
    }

    @Transactional(readOnly = true)
    public Page<Concert> findDisponiblesOuAnnulesOuGroupeTout(Long groupeId) {
        return concertRepository.findDisponiblesOuAnnulesOuGroupe(groupeId, Pageable.unpaged(TRI_CONCERT));
    }

    @Transactional(readOnly = true)
    public Page<Concert> findDisponiblesOuAnnules(int page, int taille) {
        return concertRepository.findDisponiblesOuAnnules(PageRequest.of(page, taille, TRI_CONCERT));
    }

    @Transactional(readOnly = true)
    public Page<Concert> findDisponiblesOuAnnulesTout() {
        return concertRepository.findDisponiblesOuAnnules(Pageable.unpaged(TRI_CONCERT));
    }

    // sauvegarde des concerts cochés pour un groupe
    public void updateGroupeConcerts(Long groupeId, List<Long> concertIds, List<Long> visibleConcertIds, Groupe groupe) {
        for (Long visibleId : visibleConcertIds) {
            concertRepository.findById(visibleId).ifPresent(c -> {
                if (concertIds.contains(visibleId)) {
                    c.setGroupe(groupe);
                } else if (c.getGroupe() != null && c.getGroupe().getId().equals(groupeId)) {
                    c.setGroupe(null);
                }
                concertRepository.save(c);
            });
        }
    }

    public void save(Concert concert) {
        concertRepository.save(concert);
    }

    public void deleteById(Long id) {
        concertRepository.deleteById(id);
    }

    // ------------------------------------------------------------------
    // Fenêtre de dates du festival
    // ------------------------------------------------------------------

    /**
     * Fenêtre autorisée : dates de l'édition si elle est fournie et complète,
     * sinon paramètres festival.date_debut / festival.date_fin.
     * Lu en base à chaque appel : une modification des Paramètres est prise en compte sans redémarrage.
     * Retourne vide si aucune date exploitable n'est trouvée (aucun contrôle de fenêtre dans ce cas).
     */
    @Transactional(readOnly = true)
    public Optional<FenetreFestival> getFenetre(Edition edition) {
        LocalDate debut = null;
        LocalDate fin = null;

        if (edition != null) {
            debut = toLocalDate(edition.getDateDebut());
            fin = toLocalDate(edition.getDateFin());
        }
        if (debut == null || fin == null) {
            debut = lireDateParametre("festival.date_debut");
            fin = lireDateParametre("festival.date_fin");
        }
        if (debut == null || fin == null) {
            return Optional.empty();
        }
        return Optional.of(new FenetreFestival(debut.atStartOfDay(), fin.atTime(23, 59)));
    }

    /**
     * Valide dateHeureDebut / dateHeureFin d'un concert.
     * @return map champ -> message d'erreur (vide si tout est valide)
     */
    @Transactional(readOnly = true)
    public Map<String, String> validerDates(Concert concert) {
        Map<String, String> erreurs = new LinkedHashMap<>();
        LocalDateTime debut = concert.getDateHeureDebut();
        LocalDateTime fin = concert.getDateHeureFin();

        getFenetre(concert.getEdition()).ifPresent(f -> {
            String plage = "comprise entre le " + f.debut().format(FMT_AFFICHAGE)
                    + " et le " + f.fin().format(FMT_AFFICHAGE);
            if (debut != null && horsFenetre(debut, f)) {
                erreurs.put("dateHeureDebut", "La date de début doit être " + plage + ".");
            }
            if (fin != null && horsFenetre(fin, f)) {
                erreurs.put("dateHeureFin", "La date de fin doit être " + plage + ".");
            }
        });

        if (debut != null && fin != null && !fin.isAfter(debut)) {
            erreurs.putIfAbsent("dateHeureFin", "La date de fin doit être postérieure à la date de début.");
        }
        return erreurs;
    }

    private static boolean horsFenetre(LocalDateTime d, FenetreFestival f) {
        LocalDateTime minute = d.truncatedTo(ChronoUnit.MINUTES);
        return minute.isBefore(f.debut()) || minute.isAfter(f.fin());
    }

    private LocalDate lireDateParametre(String code) {
        Parametre p = parametreService.findByCode(code);
        if (p == null || p.getValeurParametre() == null || p.getValeurParametre().isBlank()) {
            return null;
        }
        try {
            return LocalDate.parse(p.getValeurParametre().trim());
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    private static LocalDate toLocalDate(Date d) {
        if (d == null) return null;
        // java.sql.Date.toInstant() lève une exception : cas à traiter à part
        if (d instanceof java.sql.Date sd) return sd.toLocalDate();
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }
}