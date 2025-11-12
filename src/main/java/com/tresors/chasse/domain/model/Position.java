package com.tresors.chasse.domain.model;

public record Position(int x, int y) {
  public boolean compriseEntre(int width, int height) {
    return x >= 0 && y >= 0 && x < width && y < height;
  }
}
