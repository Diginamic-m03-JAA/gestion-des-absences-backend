package fr.digi.absences.service;

import fr.digi.absences.consts.StatutAbsence;
import fr.digi.absences.consts.TypeConge;
import fr.digi.absences.dto.AbsenceDto;
import fr.digi.absences.entity.Absence;
import fr.digi.absences.entity.JourFerie;
import fr.digi.absences.exception.BrokenRuleException;
import fr.digi.absences.mapper.AbsenceMap;
import fr.digi.absences.repository.AbsenceRepo;
import fr.digi.absences.repository.EmployeeRepo;
import fr.digi.absences.utils.DateUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@AllArgsConstructor
public class AbsenceSrvc {

    private AbsenceRepo absenceRepo;
    private JourFeriesService jourFeriesService;
    private AbsenceMap absenceMap;

    public AbsenceDto getAbsence(long id) {
        return absenceMap.toAbsenceDto(absenceRepo.findById(id).orElseThrow(EntityNotFoundException::new));
    }

    public Absence createAbsence(AbsenceDto absenceDto) {
        applyCreationLogic(absenceDto);
        // CREATION DE l'ABSENCE AVEC LE MAPPER ABSENCE
        Absence absence = absenceMap.toAbsence(absenceDto);
        return absenceRepo.save(absence);
    }

    public void updateAbsence(long id, AbsenceDto absenceDto) {
        applyModificationLogic(absenceDto);
        Absence absence = this.absenceRepo.findById(id).orElseThrow(EntityNotFoundException::new);
        absenceMap.modifyAbsence(absence, absenceDto);
        this.absenceRepo.save(absence);
    }

    public void deleteAbsence(long id) {
        Absence absence = this.absenceRepo.findById(id).orElse(null);
        if (absence == null) {
            throw new EntityNotFoundException();
        }
        this.absenceRepo.delete(absence);
    }

    private void applyModificationLogic(AbsenceDto absenceDto){
       applyCreationLogic(absenceDto);
       if(!(absenceDto.getStatus() ==  StatutAbsence.INITIALE || absenceDto.getStatus() == StatutAbsence.REJETEE)){
           throw new BrokenRuleException("Vous ne pouvez modifier une absence qu'au status initiale ou rejetée");
       }
       absenceDto.setStatus(StatutAbsence.INITIALE);
    }

    private void applyCreationLogic(AbsenceDto absenceDto) {
        if (!LocalDate.now().isBefore(absenceDto.getDateDebut())) {
            throw new BrokenRuleException("La Date du début de l'absence n'est pas supérieure à la date du jour");
        }
        if (absenceDto.getDateFin().isBefore(absenceDto.getDateDebut())) {
            throw new BrokenRuleException("La date de fin doit être superieure ou égale à la date de début");
        }
        if (absenceDto.getTypeConge().equals(TypeConge.SANS_SOLDE) && absenceDto.getMotif().isBlank()) {
            throw new BrokenRuleException("Il faut renseigner le motif du congé");
        }
        if (DateUtils.isOnJourFerie(absenceDto.getDateDebut(), absenceDto.getDateFin(), jourFeriesService.joursFeries(absenceDto.getDateDebut().getYear()))){
            throw new BrokenRuleException("L'absence ne peut chevaucher un jour férié. Si vous souhaitez créer une absence chevauchant un jour férié, créez une absence avant et après le jour férié");
        };
        int nbAbsences = absenceRepo.getNbAbsencesBetweenDateDebutAndDateFin(absenceDto.getDateDebut(), absenceDto.getDateFin(), absenceDto.getEmail());
        if(nbAbsences > 0){
            throw new BrokenRuleException("Il y a " + nbAbsences + " absences qui sont dans le créneau de l'absence que vous souhaitez créer");
        }
    }

    public List<AbsenceDto> getAbsences(int annee) {
        return absenceRepo.findAllByAnnee(annee).stream()
                .map(absenceMap::toAbsenceDto)
                .toList();
    }
}
