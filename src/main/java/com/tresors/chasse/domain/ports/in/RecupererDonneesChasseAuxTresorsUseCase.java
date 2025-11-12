package com.tresors.chasse.domain.ports.in;

import java.util.List;

import com.tresors.chasse.domain.model.DonneesChasseAuxTresors;

public interface RecupererDonneesChasseAuxTresorsUseCase {
  DonneesChasseAuxTresors recupererDonneesDepuisLignes(List<String> lignes) throws Exception;
}
