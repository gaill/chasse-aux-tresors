package com.tresors.chasse.infrastructure.adapters.out;

import java.nio.file.Path;

import org.springframework.stereotype.Component;

import com.tresors.chasse.domain.model.DonneesChasseAuxTresors;
import com.tresors.chasse.domain.ports.in.ExporterResulatChasseAuxTresorsUseCase;
import com.tresors.chasse.domain.ports.out.EcritureFichierPort;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Component
public class EcritureFichierAdapter implements EcritureFichierPort {
  private final ExporterResulatChasseAuxTresorsUseCase exporterResulatChasseAuxTresorsUseCase;

  public void exporterDansUnFichier(DonneesChasseAuxTresors resultat, Path outputPath)
      throws Exception {
    exporterResulatChasseAuxTresorsUseCase.exporter(resultat, outputPath);
  }
}
