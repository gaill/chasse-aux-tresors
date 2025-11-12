package com.tresors.chasse.domain.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Case {
  private Position position;
  private TypeDeCase type;
  private int tresors;
  private Aventurier occupant; // null si vide

  public Case(Position position, TypeDeCase type, int tresors) {
    this.position = position;
    this.type = type;
    this.tresors = tresors;
  }

  public boolean tresorRecupere() {
    if (tresors > 0) {
      tresors--;
      return true;
    }
    return false;
  }

  public void enleverOccupant() {
    this.occupant = null;
  }
}
