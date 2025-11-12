package com.tresors.chasse.infrastructure.adapters.in;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tresors.chasse.domain.model.Aventurier;
import com.tresors.chasse.domain.model.CarteAuxTresors;
import com.tresors.chasse.domain.model.DonneesChasseAuxTresors;
import com.tresors.chasse.domain.model.Orientation;
import com.tresors.chasse.domain.model.Position;
import com.tresors.chasse.domain.ports.in.RecupererDonneesChasseAuxTresorsUseCase;

@ExtendWith(MockitoExtension.class)
class LectureFichierAdapterUTest {
  @InjectMocks private LectureFichierAdapter lectureFichierAdapter;
  @Mock private RecupererDonneesChasseAuxTresorsUseCase recupererDonneesChasseAuxTresorsUseCase;

  @TempDir Path tempDir;

  @Captor ArgumentCaptor<List<String>> captor;

  @Test
  void doit_appeler_recupererDonneesChasseAuxTresorsUseCase_avec_les_lignes_nettoyees()
      throws Exception {
    // GIVEN
    Path inputFile = tempDir.resolve("input.txt");
    Files.writeString(
        inputFile,
        """
                # Commentaire à ignorer
                C - 3 - 4
                M - 1 - 1

                A - Lara - 1 - 1 - N - AADADAGGA
                """);

    CarteAuxTresors carte = new CarteAuxTresors(3, 4);
    Aventurier aventurier =
        new Aventurier("Lara", new Position(1, 1), Orientation.N, new ArrayDeque<>(), 0);
    DonneesChasseAuxTresors donneesMock = new DonneesChasseAuxTresors(carte, List.of(aventurier));

    when(recupererDonneesChasseAuxTresorsUseCase.recupererDonneesDepuisLignes(anyList()))
        .thenReturn(donneesMock);

    // WHEN
    DonneesChasseAuxTresors resultat =
        lectureFichierAdapter.recupererDonneesDepuisFichier(inputFile);

    // THEN
    verify(recupererDonneesChasseAuxTresorsUseCase).recupererDonneesDepuisLignes(captor.capture());

    List<String> lignesEnvoyees = captor.getValue();
    assertEquals(
        List.of("C - 3 - 4", "M - 1 - 1", "A - Lara - 1 - 1 - N - AADADAGGA"),
        lignesEnvoyees,
        "Les lignes doivent être nettoyées et sans commentaires");

    assertEquals(carte, resultat.carteAuxTresors());
    assertEquals(1, resultat.aventuriers().size());
    assertEquals("Lara", resultat.aventuriers().getFirst().getNom());
  }

  @Test
  void doit_lever_IOException_si_fichier_inaccessible() {
    // GIVEN
    Path inexistant = tempDir.resolve("fichier-inexistant.txt");

    // WHEN + THEN
    assertThrows(
        IOException.class, () -> lectureFichierAdapter.recupererDonneesDepuisFichier(inexistant));
    verifyNoInteractions(recupererDonneesChasseAuxTresorsUseCase);
  }

  @Test
  void doit_propager_exception_si_le_useCase_echoue() throws Exception {
    // GIVEN
    Path inputFile = tempDir.resolve("input.txt");
    Files.writeString(inputFile, "C - 3 - 4");

    when(recupererDonneesChasseAuxTresorsUseCase.recupererDonneesDepuisLignes(anyList()))
        .thenThrow(new RuntimeException("Erreur dans le use case"));

    // WHEN + THEN
    Exception ex =
        assertThrows(
            Exception.class, () -> lectureFichierAdapter.recupererDonneesDepuisFichier(inputFile));
    assertTrue(ex.getMessage().contains("Erreur dans le use case"));
  }
}
