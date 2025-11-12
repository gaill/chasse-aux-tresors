package com.tresors.chasse.domain.model;

public enum Orientation {
  N(0, -1),
  S(0, 1),
  E(1, 0),
  O(-1, 0);

  private final int dx;
  private final int dy;

  Orientation(int dx, int dy) {
    this.dx = dx;
    this.dy = dy;
  }

  public Orientation gauche() {
    return switch (this) {
      case N -> O;
      case O -> S;
      case S -> E;
      case E -> N;
    };
  }

  public Orientation droite() {
    return switch (this) {
      case N -> E;
      case E -> S;
      case S -> O;
      case O -> N;
    };
  }

  public int dx() {
    return dx;
  }

  public int dy() {
    return dy;
  }
}
