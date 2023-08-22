package fr.digi.absences.controller;

import fr.digi.absences.dto.AbsenceDto;
import fr.digi.absences.entity.Absence;
import fr.digi.absences.service.AbsenceSrvc;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping(("/api/v1/absence"))
public class AbsenceCtrl {

    private AbsenceSrvc absenceSrvc;

    @GetMapping
    public ResponseEntity<List<AbsenceDto>> displayAbsences(@RequestParam int annee){
        return ResponseEntity.status(200).body(this.absenceSrvc.getAbsences(annee));
    }
    @GetMapping("/{id}")
    public ResponseEntity<AbsenceDto> displayAbsence(@PathVariable long id){
        return ResponseEntity.status(200).body(absenceSrvc.getAbsence(id));
    }

    @PostMapping
    public ResponseEntity<AbsenceDto> createAbsence(@RequestBody AbsenceDto absenceDto){
        // APPLICATION DES LOGIQUES METIERS
        AbsenceDto absence = this.absenceSrvc.createAbsence(absenceDto);
        return ResponseEntity.status(201).body(absence);
    }

    @PutMapping
    public ResponseEntity<AbsenceDto> updateAbsence(@RequestBody AbsenceDto absenceDto){
        return ResponseEntity.ok(this.absenceSrvc.updateAbsence(absenceDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAbsence(@PathVariable long id){
        this.absenceSrvc.deleteAbsence(id);
        return new ResponseEntity<>("L'absence a été supprimée avec Succès", HttpStatus.OK);
    }
}
