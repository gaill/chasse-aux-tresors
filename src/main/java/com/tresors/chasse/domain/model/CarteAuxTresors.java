package com.tresors.chasse.domain.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import lombok.Getter;

@Getter
public class CarteAuxTresors {
  private int largeur;
  private int hauteur;
  private Map<Position, Case> cases = new HashMap<>();

  //    Par défaut, toutes les cases de la carte sont des plaines que les aventuriers peuvent
  // traverser sans
  //    encombre. Les cases sont numérotées d’ouest en est, de nord en sud, en commençant par zéro.
  public CarteAuxTresors(int largeur, int hauteur) {
    this.largeur = largeur;
    this.hauteur = hauteur;
    for (int y = 0; y < hauteur; y++) {
      for (int x = 0; x < largeur; x++) {
        cases.put(new Position(x, y), new Case(new Position(x, y), TypeDeCase.PLAINE, 0));
      }
    }
  }

  public Optional<Case> caseALaPosition(Position p) {
    return Optional.ofNullable(cases.get(p));
  }

  public void mettreUneMontagneALaPosition(Position p) {
    cases.put(p, new Case(p, TypeDeCase.MONTAGNE, 0));
  }

  public void mettreTresorsALaPosition(Position p, int count) {
    Case c = cases.get(p);
    if (c == null) {
      c = new Case(p, TypeDeCase.PLAINE, count);
    } else {
      cases.put(p, new Case(p, c.getType(), count));
      return;
    }
    cases.put(p, c);
  }
}
