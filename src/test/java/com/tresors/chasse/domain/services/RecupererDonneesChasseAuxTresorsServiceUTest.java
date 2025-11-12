package com.tresors.chasse.domain.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tresors.chasse.domain.model.Aventurier;
import com.tresors.chasse.domain.model.DonneesChasseAuxTresors;
import com.tresors.chasse.domain.model.Mouvement;
import com.tresors.chasse.domain.model.Orientation;

class RecupererDonneesChasseAuxTresorsServiceUTest {
  private RecupererDonneesChasseAuxTresorsService service;

  @BeforeEach
  void setUp() {
    service = new RecupererDonneesChasseAuxTresorsService();
  }

  @Test
  void doit_parser_une_carte_complete_avec_aventurier() {
    // GIVEN
    List<String> lignes =
        List.of("C - 3 - 4", "M - 1 - 1", "T - 0 - 3 - 2", "A - Lara - 1 - 1 - S - AADADAGGA");

    // WHEN
    DonneesChasseAuxTresors resultat = service.recupererDonneesDepuisLignes(lignes);

    // THEN
    assertThat(resultat).isNotNull();
    assertThat(resultat.carteAuxTresors().getLargeur()).isEqualTo(3);
    assertThat(resultat.carteAuxTresors().getHauteur()).isEqualTo(4);

    assertThat(resultat.aventuriers()).hasSize(1);
    Aventurier lara = resultat.aventuriers().getFirst();
    assertThat(lara.getNom()).isEqualTo("Lara");
    assertThat(lara.getPosition().x()).isOne();
    assertThat(lara.getPosition().y()).isOne();
    assertThat(lara.getOrientation()).isEqualTo(Orientation.S);
    assertThat(lara.getMouvements())
        .containsExactly(
            Mouvement.A,
            Mouvement.A,
            Mouvement.D,
            Mouvement.A,
            Mouvement.D,
            Mouvement.A,
            Mouvement.G,
            Mouvement.G,
            Mouvement.A);
  }

  @Test
  void doit_lancer_exception_si_aucune_carte() {
    // GIVEN
    List<String> lignes = List.of("A - Indy - 1 - 1 - N - AADA");

    // WHEN + THEN
    assertThatThrownBy(() -> service.recupererDonneesDepuisLignes(lignes))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("carte doit être définie");
  }

  @Test
  void doit_lancer_exception_si_ligne_carte_invalide() {
    // GIVEN
    List<String> lignes = List.of("C - 3");

    // WHEN + THEN
    assertThatThrownBy(() -> service.recupererDonneesDepuisLignes(lignes))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Ligne C invalide");
  }

  @Test
  void doit_lancer_exception_si_ligne_montagne_invalide() {
    // GIVEN
    List<String> lignes = List.of("C - 3 - 4", "M - 1");

    // WHEN + THEN
    assertThatThrownBy(() -> service.recupererDonneesDepuisLignes(lignes))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Ligne M invalide");
  }

  @Test
  void doit_lancer_exception_si_ligne_tresor_invalide() {
    // GIVEN
    List<String> lignes = List.of("C - 3 - 4", "T - 0 - 3");

    // WHEN + THEN
    assertThatThrownBy(() -> service.recupererDonneesDepuisLignes(lignes))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Ligne T invalide");
  }

  @Test
  void doit_lancer_exception_si_ligne_aventurier_invalide() {
    // GIVEN
    List<String> lignes = List.of("C - 3 - 4", "A - Lara - 1 - 1 - S");

    // WHEN + THEN
    assertThatThrownBy(() -> service.recupererDonneesDepuisLignes(lignes))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Ligne A invalide");
  }

  @Test
  void doit_lancer_exception_si_type_inconnu() {
    // GIVEN
    List<String> lignes = List.of("C - 3 - 4", "X - truc - bidule");

    // WHEN + THEN
    assertThatThrownBy(() -> service.recupererDonneesDepuisLignes(lignes))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Type de ligne inconnu");
  }

  @Test
  void doit_parser_les_orientations_correctement() {
    // GIVEN
    List<String> lignes =
        List.of("C - 2 - 2", "A - Test - 0 - 0 - E - A", "A - T2 - 1 - 1 - O - A");

    // WHEN
    DonneesChasseAuxTresors resultat = service.recupererDonneesDepuisLignes(lignes);

    // THEN
    assertThat(resultat.aventuriers()).hasSize(2);
    assertThat(resultat.aventuriers().get(0).getOrientation()).isEqualTo(Orientation.E);
    assertThat(resultat.aventuriers().get(1).getOrientation()).isEqualTo(Orientation.O);
  }

  @Test
  void doit_lancer_exception_si_orientation_inconnue() {
    // GIVEN
    List<String> lignes = List.of("C - 3 - 3", "A - Bob - 0 - 0 - X - A");

    // WHEN + THEN
    assertThatThrownBy(() -> service.recupererDonneesDepuisLignes(lignes))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Orientation inconnue");
  }

  @Test
  void doit_lancer_exception_si_aucune_carte_definie() {
    // GIVEN
    List<String> lignes = List.of("M - 1 - 1");

    // WHEN + THEN
    assertThatThrownBy(() -> service.recupererDonneesDepuisLignes(lignes))
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("carte doit être définie");
  }
}
