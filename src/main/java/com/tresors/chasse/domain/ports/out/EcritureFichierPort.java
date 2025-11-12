package com.tresors.chasse.domain.ports.out;

import java.nio.file.Path;

import com.tresors.chasse.domain.model.DonneesChasseAuxTresors;

public interface EcritureFichierPort {
  void exporterDansUnFichier(DonneesChasseAuxTresors result, Path outputPath) throws Exception;
}
