package com.tresors.chasse.domain.ports.in;

import java.nio.file.Path;

import com.tresors.chasse.domain.model.DonneesChasseAuxTresors;

public interface ExporterResulatChasseAuxTresorsUseCase {
  void exporter(DonneesChasseAuxTresors resultat, Path outputPath) throws Exception;
}
