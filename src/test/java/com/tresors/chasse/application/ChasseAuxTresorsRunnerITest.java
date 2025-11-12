package com.tresors.chasse.application;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.tresors.chasse.domain.ports.in.SimulerChasseAuxTresorsUseCase;
import com.tresors.chasse.infrastructure.adapters.in.LectureFichierAdapter;
import com.tresors.chasse.infrastructure.adapters.out.EcritureFichierAdapter;

@SpringBootTest
class ChasseAuxTresorsRunnerITest {
  @Autowired ChasseAuxTresorsRunner runner;
  @Autowired LectureFichierAdapter lectureAdapter;
  @Autowired SimulerChasseAuxTresorsUseCase simulerService;
  @Autowired EcritureFichierAdapter ecritureAdapter;

  private Path inputFile;
  private Path outputFile;

  @BeforeEach
  void setUp() throws IOException {
    // Crée un fichier d'entrée temporaire
    inputFile = Files.createTempFile("input", ".txt");
    outputFile = Files.createTempFile("output", ".txt");

    String contenu =
        """
        C - 3 - 4
        M - 1 - 0
        T - 0 - 3 - 2
        A - Lara - 1 - 1 - S - AADADAGGA
        """;

    Files.writeString(inputFile, contenu);
  }

  @AfterEach
  void tearDown() throws IOException {
    Files.deleteIfExists(inputFile);
    Files.deleteIfExists(outputFile);
  }

  @Test
  void run_doit_lire_simuler_et_exporter_le_resulat_de_la_simulation() throws Exception {
    // GIVEN + WHEN: on exécute la simulation complète
    runner.run(inputFile.toString(), outputFile.toString());

    // THEN: le fichier de sortie existe et contient des données cohérentes
    assertThat(Files.exists(outputFile)).isTrue();

    String output = Files.readString(outputFile);

    assertThat(output)
        .contains("C - 3 - 4")
        .contains("M - 1 - 0")
        .contains("A - Lara - 0 - 3 - S - 2");
  }
}
