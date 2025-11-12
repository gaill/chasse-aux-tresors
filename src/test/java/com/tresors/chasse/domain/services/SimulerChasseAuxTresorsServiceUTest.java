package com.tresors.chasse.domain.services;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tresors.chasse.domain.model.Aventurier;
import com.tresors.chasse.domain.model.CarteAuxTresors;
import com.tresors.chasse.domain.model.DonneesChasseAuxTresors;
import com.tresors.chasse.domain.model.Mouvement;
import com.tresors.chasse.domain.model.Orientation;
import com.tresors.chasse.domain.model.Position;

class SimulerChasseAuxTresorsServiceUTest {
  private SimulerChasseAuxTresorsService service;

  @BeforeEach
  void setUp() {
    service = new SimulerChasseAuxTresorsService();
  }

  @Test
  void doit_deplacer_aventurier_et_collecter_tresors() {
    // GIVEN
    CarteAuxTresors carte = new CarteAuxTresors(3, 3);
    carte.mettreTresorsALaPosition(new Position(1, 1), 2);

    Deque<Mouvement> mouvements = new ArrayDeque<>();
    mouvements.add(Mouvement.A);
    mouvements.add(Mouvement.A);
    Aventurier aventurier =
        new Aventurier("Lara", new Position(0, 1), Orientation.E, mouvements, 0);

    List<Aventurier> aventuriers = List.of(aventurier);

    // WHEN
    DonneesChasseAuxTresors resultat = service.simuler(carte, aventuriers);

    // THEN
    assertThat(resultat.aventuriers().getFirst().getPosition()).isEqualTo(new Position(2, 1));
    assertThat(resultat.aventuriers().getFirst().getTresorsCollectes()).isOne();
    assertThat(resultat.carteAuxTresors().caseALaPosition(new Position(1, 1)).get().getTresors())
        .isOne();
  }

  @Test
  void doit_tourner_correctement_aventurier() {
    // GIVEN
    CarteAuxTresors carte = new CarteAuxTresors(2, 2);
    Deque<Mouvement> mouvements = new ArrayDeque<>();
    mouvements.add(Mouvement.G);
    mouvements.add(Mouvement.D);
    Aventurier aventurier =
        new Aventurier("Indy", new Position(0, 0), Orientation.N, mouvements, 0);

    List<Aventurier> aventuriers = List.of(aventurier);

    // WHEN
    service.simuler(carte, aventuriers);

    // THEN
    assertThat(aventurier.getOrientation()).isEqualTo(Orientation.N);
  }

  @Test
  void doit_empecher_aventurier_de_traverser_montagne() {
    // GIVEN
    CarteAuxTresors carte = new CarteAuxTresors(3, 3);
    carte.mettreUneMontagneALaPosition(new Position(1, 1));

    Deque<Mouvement> mouvements = new ArrayDeque<>();
    mouvements.add(Mouvement.A);
    Aventurier aventurier = new Aventurier("Bob", new Position(0, 1), Orientation.E, mouvements, 0);

    List<Aventurier> aventuriers = List.of(aventurier);

    // WHEN
    service.simuler(carte, aventuriers);

    // THEN
    assertThat(aventurier.getPosition()).isEqualTo(new Position(0, 1));
  }

  @Test
  void doit_empecher_aventurier_de_sortir_de_la_carte() {
    // GIVEN
    CarteAuxTresors carte = new CarteAuxTresors(2, 2);

    Deque<Mouvement> mouvements = new ArrayDeque<>();
    mouvements.add(Mouvement.A);
    mouvements.add(Mouvement.A);
    Aventurier aventurier = new Aventurier("Max", new Position(1, 1), Orientation.E, mouvements, 0);

    List<Aventurier> aventuriers = List.of(aventurier);

    // WHEN
    service.simuler(carte, aventuriers);

    // THEN
    assertThat(aventurier.getPosition()).isEqualTo(new Position(1, 1));
  }

  @Test
  void doit_empecher_deux_aventuriers_d_occuper_la_meme_case() {
    // GIVEN
    CarteAuxTresors carte = new CarteAuxTresors(3, 3);

    Aventurier a1 =
        new Aventurier(
            "A1", new Position(0, 1), Orientation.E, new ArrayDeque<>(List.of(Mouvement.A)), 0);
    Aventurier a2 =
        new Aventurier(
            "A2", new Position(2, 1), Orientation.O, new ArrayDeque<>(List.of(Mouvement.A)), 1);

    List<Aventurier> aventuriers = List.of(a1, a2);

    // WHEN
    service.simuler(carte, aventuriers);

    // THEN
    assertThat(a1.getPosition()).isEqualTo(new Position(1, 1));
    assertThat(a2.getPosition()).isEqualTo(new Position(2, 1));
  }

  @Test
  void doit_collecter_un_tresor_par_visite_sur_une_case() {
    // GIVEN
    CarteAuxTresors carte = new CarteAuxTresors(3, 3);
    Position posTresor = new Position(1, 1);
    carte.mettreTresorsALaPosition(posTresor, 2);

    Deque<Mouvement> mouvements = new ArrayDeque<>(List.of(Mouvement.A, Mouvement.A));
    Aventurier aventurier =
        new Aventurier("Lara", new Position(0, 1), Orientation.E, mouvements, 0);

    List<Aventurier> aventuriers = List.of(aventurier);

    // WHEN
    service.simuler(carte, aventuriers);

    // THEN
    assertThat(aventurier.getTresorsCollectes()).isOne();
    assertThat(carte.getCases().get(posTresor).getTresors()).isOne();

    // Déplacer l’aventurier à gauche et revenir pour ramasser 2ème
    aventurier.seDeplaceVers(new Position(0, 1));
    aventurier.getMouvements().addAll(List.of(Mouvement.A, Mouvement.A));
    service.simuler(carte, List.of(aventurier));

    assertThat(aventurier.getTresorsCollectes()).isEqualTo(2);
    assertThat(carte.getCases().get(posTresor).getTresors()).isZero();
  }
}
