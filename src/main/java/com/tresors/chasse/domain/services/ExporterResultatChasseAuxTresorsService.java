package com.tresors.chasse.domain.services;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tresors.chasse.domain.model.Aventurier;
import com.tresors.chasse.domain.model.CarteAuxTresors;
import com.tresors.chasse.domain.model.Case;
import com.tresors.chasse.domain.model.DonneesChasseAuxTresors;
import com.tresors.chasse.domain.model.TypeDeCase;
import com.tresors.chasse.domain.ports.in.ExporterResulatChasseAuxTresorsUseCase;

/** Service d’export du résultat final de la simulation. */
@Service
public class ExporterResultatChasseAuxTresorsService
    implements ExporterResulatChasseAuxTresorsUseCase {
  @Override
  public void exporter(DonneesChasseAuxTresors resultatDeLaSimulation, Path outputPath)
      throws Exception {
    try (BufferedWriter writer = Files.newBufferedWriter(outputPath)) {
      CarteAuxTresors carteAuxTresors = resultatDeLaSimulation.carteAuxTresors();
      List<Aventurier> aventuriers = resultatDeLaSimulation.aventuriers();

      // Ligne carte
      ecrireLaLigne(
          writer, "C - %d - %d", carteAuxTresors.getLargeur(), carteAuxTresors.getHauteur());

      // Lignes des montagnes
      ecrireLesLignesMontagne(carteAuxTresors, writer);

      // Ligne des trésors restants dans l'ordre de position
      ecrireLesLignesTresorsRestants(carteAuxTresors, writer);

      // Ligne des aventuriers dans l’ordre initial
      ecrireLesLignesAventuriers(aventuriers, writer);
    }
  }

  private void ecrireLesLignesAventuriers(List<Aventurier> aventuriers, BufferedWriter writer) {
    aventuriers.stream()
        .sorted(Comparator.comparingInt(Aventurier::getOrdre))
        .forEach(
            a ->
                ecrireLaLigne(
                    writer,
                    "A - %s - %d - %d - %s - %d",
                    a.getNom(),
                    a.getPosition().x(),
                    a.getPosition().y(),
                    a.getOrientation().name(),
                    a.getTresorsCollectes()));
  }

  private void ecrireLesLignesTresorsRestants(
      CarteAuxTresors carteAuxTresors, BufferedWriter writer) {
    carteAuxTresors.getCases().values().stream()
        .filter(c -> c.getTresors() > 0)
        .sorted(parPosition())
        .forEach(
            c ->
                ecrireLaLigne(
                    writer,
                    "T - %d - %d - %d",
                    c.getPosition().x(),
                    c.getPosition().y(),
                    c.getTresors()));
  }

  private void ecrireLesLignesMontagne(CarteAuxTresors carteAuxTresors, BufferedWriter writer) {
    carteAuxTresors.getCases().values().stream()
        .filter(c -> c.getType() == TypeDeCase.MONTAGNE)
        .sorted(parPosition())
        .forEach(
            c -> ecrireLaLigne(writer, "M - %d - %d", c.getPosition().x(), c.getPosition().y()));
  }

  private Comparator<Case> parPosition() {
    return Comparator.<Case>comparingInt(c -> c.getPosition().y())
        .thenComparingInt(c -> c.getPosition().x());
  }

  private void ecrireLaLigne(BufferedWriter writer, String pattern, Object... args) {
    try {
      writer.write(String.format(pattern, args));
      writer.newLine();
    } catch (IOException e) {
      throw new RuntimeException("Erreur d’écriture du fichier de sortie", e);
    }
  }
}
