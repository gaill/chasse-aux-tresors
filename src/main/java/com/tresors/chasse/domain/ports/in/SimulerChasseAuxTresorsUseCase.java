package com.tresors.chasse.domain.ports.in;

import java.util.List;

import com.tresors.chasse.domain.model.Aventurier;
import com.tresors.chasse.domain.model.CarteAuxTresors;
import com.tresors.chasse.domain.model.DonneesChasseAuxTresors;

public interface SimulerChasseAuxTresorsUseCase {
  DonneesChasseAuxTresors simuler(CarteAuxTresors carteAuxTresors, List<Aventurier> aventuriers);
}
