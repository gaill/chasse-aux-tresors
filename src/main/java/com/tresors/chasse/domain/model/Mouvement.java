package com.tresors.chasse.domain.model;

public enum Mouvement {
  A, // Avancer
  G, // Tourner à gauche
  D; // Tourner à droite

  public static Mouvement fromChar(char c) {
    return switch (c) {
      case 'A' -> A;
      case 'G' -> G;
      case 'D' -> D;
      default -> throw new IllegalArgumentException("Déplacement inconnue: " + c);
    };
  }
}
