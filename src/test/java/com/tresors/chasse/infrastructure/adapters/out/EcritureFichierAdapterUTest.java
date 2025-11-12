package com.tresors.chasse.infrastructure.adapters.out;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tresors.chasse.domain.model.Aventurier;
import com.tresors.chasse.domain.model.CarteAuxTresors;
import com.tresors.chasse.domain.model.DonneesChasseAuxTresors;
import com.tresors.chasse.domain.model.Orientation;
import com.tresors.chasse.domain.model.Position;
import com.tresors.chasse.domain.ports.in.ExporterResulatChasseAuxTresorsUseCase;

@ExtendWith({MockitoExtension.class})
class EcritureFichierAdapterUTest {
  @InjectMocks private EcritureFichierAdapter ecritureFichierAdapter;

  @Mock private ExporterResulatChasseAuxTresorsUseCase exporterResulatChasseAuxTresorsUseCase;

  @Test
  void doit_appeler_exporter_avec_les_bons_arguments() throws Exception {
    // GIVEN
    var carte = new CarteAuxTresors(3, 4);
    Aventurier aventurier =
        new Aventurier("Lara", new Position(1, 1), Orientation.N, new ArrayDeque<>(), 0);
    DonneesChasseAuxTresors donnees = new DonneesChasseAuxTresors(carte, List.of(aventurier));
    Path path = Path.of("output.txt");

    // WHEN
    ecritureFichierAdapter.exporterDansUnFichier(donnees, path);

    // THEN
    ArgumentCaptor<DonneesChasseAuxTresors> donneesCaptor =
        ArgumentCaptor.forClass(DonneesChasseAuxTresors.class);
    ArgumentCaptor<Path> pathCaptor = ArgumentCaptor.forClass(Path.class);

    verify(exporterResulatChasseAuxTresorsUseCase)
        .exporter(donneesCaptor.capture(), pathCaptor.capture());
    assertEquals(donnees, donneesCaptor.getValue());
    assertEquals(path, pathCaptor.getValue());
  }

  @Test
  void doit_lancer_exception_si_export_echoue() throws Exception {
    // GIVEN
    DonneesChasseAuxTresors donnees = mock(DonneesChasseAuxTresors.class);
    Path path = Path.of("out.txt");
    doThrow(new RuntimeException("Erreur d'export"))
        .when(exporterResulatChasseAuxTresorsUseCase)
        .exporter(any(), any());

    // WHEN + THEN
    Exception ex =
        assertThrows(
            Exception.class, () -> ecritureFichierAdapter.exporterDansUnFichier(donnees, path));
    assertTrue(ex.getMessage().contains("Erreur d'export"));
  }
}
