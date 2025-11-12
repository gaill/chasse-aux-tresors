package com.tresors.chasse.domain.ports.in;

import java.nio.file.Path;

import com.tresors.chasse.domain.model.DonneesChasseAuxTresors;

public interface LectureFichierPort {
  DonneesChasseAuxTresors recupererDonneesDepuisFichier(Path inputPath) throws Exception;
}
