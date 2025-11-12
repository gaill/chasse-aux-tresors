package com.tresors.chasse.infrastructure.adapters.in;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.tresors.chasse.domain.model.DonneesChasseAuxTresors;
import com.tresors.chasse.domain.ports.in.LectureFichierPort;
import com.tresors.chasse.domain.ports.in.RecupererDonneesChasseAuxTresorsUseCase;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class LectureFichierAdapter implements LectureFichierPort {
  private final RecupererDonneesChasseAuxTresorsUseCase recupererDonneesChasseAuxTresorsUseCase;

  public DonneesChasseAuxTresors recupererDonneesDepuisFichier(Path inputPath) throws Exception {
    List<String> lignes =
        Files.readAllLines(inputPath).stream()
            .map(String::trim)
            .filter(l -> !l.isEmpty() && !l.startsWith("#"))
            .collect(Collectors.toList());

    var donnees = recupererDonneesChasseAuxTresorsUseCase.recupererDonneesDepuisLignes(lignes);
    return new DonneesChasseAuxTresors(donnees.carteAuxTresors(), donnees.aventuriers());
  }
}
