package com.tresors.chasse.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.tresors.chasse.domain.model.Aventurier;
import com.tresors.chasse.domain.model.CarteAuxTresors;
import com.tresors.chasse.domain.model.DonneesChasseAuxTresors;
import com.tresors.chasse.domain.model.Orientation;
import com.tresors.chasse.domain.model.Position;

class ExporterResultatChasseAuxTresorsServiceUTest {

  private ExporterResultatChasseAuxTresorsService service;

  @TempDir Path tempDir;

  @BeforeEach
  void setUp() {
    service = new ExporterResultatChasseAuxTresorsService();
  }

  @Test
  void doit_ecrire_un_fichier_de_sortie_complet() throws Exception {
    // GIVEN
    CarteAuxTresors carte = new CarteAuxTresors(3, 4);
    // Montagnes
    carte.mettreUneMontagneALaPosition(new Position(1, 0));
    carte.mettreUneMontagneALaPosition(new Position(2, 1));
    // Trésors restants
    carte.mettreTresorsALaPosition(new Position(1, 3), 2);

    // Aventuriers
    Aventurier lara =
        new Aventurier("Lara", new Position(0, 3), Orientation.S, new ArrayDeque<>(), 0);
    lara.collecteUnTresor();
    lara.collecteUnTresor();
    lara.collecteUnTresor();
    Aventurier indy =
        new Aventurier("Indy", new Position(1, 2), Orientation.N, new ArrayDeque<>(), 1);

    DonneesChasseAuxTresors donnees = new DonneesChasseAuxTresors(carte, List.of(lara, indy));

    Path outputPath = tempDir.resolve("resultat.txt");

    // WHEN
    service.exporter(donnees, outputPath);

    // THEN
    List<String> lignes = Files.readAllLines(outputPath);

    assertThat(lignes)
        .containsExactly(
            "C - 3 - 4",
            "M - 1 - 0",
            "M - 2 - 1",
            "T - 1 - 3 - 2",
            "A - Lara - 0 - 3 - S - 3",
            "A - Indy - 1 - 2 - N - 0");
  }

  @Test
  void ne_doit_ecrire_aucune_ligne_tresor_si_aucun_tresor_restant() throws Exception {
    // GIVEN
    CarteAuxTresors carte = new CarteAuxTresors(2, 2);
    carte.mettreUneMontagneALaPosition(new Position(0, 0));

    Aventurier a1 = new Aventurier("A1", new Position(1, 1), Orientation.E, new ArrayDeque<>(), 0);
    DonneesChasseAuxTresors donnees = new DonneesChasseAuxTresors(carte, List.of(a1));

    Path outputPath = tempDir.resolve("vide.txt");

    // WHEN
    service.exporter(donnees, outputPath);

    // THEN
    List<String> lignes = Files.readAllLines(outputPath);
    assertThat(lignes).contains("C - 2 - 2", "M - 0 - 0", "A - A1 - 1 - 1 - E - 0");
    assertThat(lignes.stream().anyMatch(l -> l.startsWith("T -"))).isFalse();
  }

  @Test
  void doit_trier_les_montagnes_et_tresors_par_y_puis_x() throws Exception {
    // GIVEN
    CarteAuxTresors carte = new CarteAuxTresors(3, 3);
    carte.mettreUneMontagneALaPosition(new Position(2, 2));
    carte.mettreUneMontagneALaPosition(new Position(0, 0));
    carte.mettreTresorsALaPosition(new Position(2, 0), 1);
    carte.mettreTresorsALaPosition(new Position(0, 2), 2);

    DonneesChasseAuxTresors donnees = new DonneesChasseAuxTresors(carte, List.of());

    Path outputPath = tempDir.resolve("sorted.txt");

    // WHEN
    service.exporter(donnees, outputPath);

    // THEN
    List<String> lignes = Files.readAllLines(outputPath);

    // Les lignes M et T doivent être triées d’abord par Y puis par X
    assertThat(lignes)
        .containsSubsequence(
            "C - 3 - 3", "M - 0 - 0", "M - 2 - 2", "T - 2 - 0 - 1", "T - 0 - 2 - 2");
  }

  @Test
  void leve_exception_si_erreur_ecriture() {
    // GIVEN
    DonneesChasseAuxTresors donnees =
        new DonneesChasseAuxTresors(new CarteAuxTresors(1, 1), List.of());

    Path cheminInvalide = Path.of("/repertoire/inexistant/result.txt");

    // WHEN + THEN
    assertThatThrownBy(() -> service.exporter(donnees, cheminInvalide))
        .isInstanceOf(Exception.class);
  }
}
