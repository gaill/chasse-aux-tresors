package com.tresors.chasse.application;

import static com.tresors.chasse.domain.model.Mouvement.A;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tresors.chasse.domain.model.Aventurier;
import com.tresors.chasse.domain.model.CarteAuxTresors;
import com.tresors.chasse.domain.model.DonneesChasseAuxTresors;
import com.tresors.chasse.domain.model.Orientation;
import com.tresors.chasse.domain.model.Position;
import com.tresors.chasse.domain.ports.in.SimulerChasseAuxTresorsUseCase;
import com.tresors.chasse.infrastructure.adapters.in.LectureFichierAdapter;
import com.tresors.chasse.infrastructure.adapters.out.EcritureFichierAdapter;

@ExtendWith(MockitoExtension.class)
class ChasseAuxTresorsRunnerUTest {

  @Mock private LectureFichierAdapter lectureAdapter;
  @Mock private SimulerChasseAuxTresorsUseCase simulerService;
  @Mock private EcritureFichierAdapter ecritureAdapter;

  @InjectMocks private ChasseAuxTresorsRunner runner;

  private DonneesChasseAuxTresors donnees;
  private DonneesChasseAuxTresors resultat;

  @BeforeEach
  void setUp() {
    var carte = new CarteAuxTresors(3, 4);
    var aventuriers =
        List.of(
            new Aventurier(
                "Lara", new Position(1, 1), Orientation.S, new ArrayDeque<>(List.of(A, A, A)), 0));
    donnees = new DonneesChasseAuxTresors(carte, aventuriers);
    resultat = new DonneesChasseAuxTresors(carte, aventuriers);
  }

  @Test
  void run_doit_lire_simuler_et_exporter_le_resultat_de_la_simulation() throws Exception {
    // GIVEN
    Path input = Path.of("input.txt");
    Path output = Path.of("output.txt");

    when(lectureAdapter.recupererDonneesDepuisFichier(input)).thenReturn(donnees);
    when(simulerService.simuler(donnees.carteAuxTresors(), donnees.aventuriers()))
        .thenReturn(resultat);

    // WHEN
    runner.run(input.toString(), output.toString());

    // THEN
    verify(lectureAdapter).recupererDonneesDepuisFichier(input);
    verify(simulerService).simuler(donnees.carteAuxTresors(), donnees.aventuriers());
    verify(ecritureAdapter).exporterDansUnFichier(resultat, output);
  }

  @Test
  void run_ne_doit_pas_lire_simuler_et_exporter_si_il_ne_recoit_pas_d_arguments_en_entre()
      throws Exception {
    // WHEN
    runner.run(); // aucun argument

    // THEN
    verifyNoInteractions(lectureAdapter, simulerService, ecritureAdapter);
  }
}
