package com.tresors.chasse.application;

import java.nio.file.Path;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.tresors.chasse.domain.ports.in.LectureFichierPort;
import com.tresors.chasse.domain.ports.in.SimulerChasseAuxTresorsUseCase;
import com.tresors.chasse.domain.ports.out.EcritureFichierPort;

import lombok.AllArgsConstructor;

/** Point d’entrée principal de l’application. */
@Component
@AllArgsConstructor
public class ChasseAuxTresorsRunner implements CommandLineRunner {
  private final LectureFichierPort lectureAdapter;
  private final SimulerChasseAuxTresorsUseCase simulerService;
  private final EcritureFichierPort ecritureAdapter;

  @Override
  public void run(String... args) throws Exception {
    if (args.length < 2) {
      System.err.println("Usage: java -jar tresors-carte-1.0.0.jar <inputFile> <outputFile>");
      return;
    }

    Path inputPath = Path.of(args[0]);
    Path outputPath = Path.of(args[1]);

    var donneesChasseAuxTresors = lectureAdapter.recupererDonneesDepuisFichier(inputPath);
    var resultatDeLaSimulation =
        simulerService.simuler(
            donneesChasseAuxTresors.carteAuxTresors(), donneesChasseAuxTresors.aventuriers());
    ecritureAdapter.exporterDansUnFichier(resultatDeLaSimulation, outputPath);

    System.out.println("Simulation terminée, fichier exporté vers " + outputPath);
  }
}
