package com.tresors.chasse.domain.model;

import java.util.List;

public record DonneesChasseAuxTresors(
    CarteAuxTresors carteAuxTresors, List<Aventurier> aventuriers) {}
