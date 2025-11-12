package com.tresors.chasse.domain.model;

import java.util.Deque;

import lombok.Getter;

@Getter
public class Aventurier {
  private final String nom;
  private Position position;
  private Orientation orientation;
  private final Deque<Mouvement> mouvements;
  private int tresorsCollectes = 0;
  private final int ordre;

  public Aventurier(
      String nom,
      Position position,
      Orientation orientation,
      Deque<Mouvement> mouvements,
      int ordre) {
    this.nom = nom;
    this.position = position;
    this.orientation = orientation;
    this.mouvements = mouvements;
    this.ordre = ordre;
  }

  public boolean aUnProchainMouvement() {
    return !mouvements.isEmpty();
  }

  public Mouvement prochainMouvement() {
    return mouvements.pollFirst();
  }

  public void tourneVersLaGauche() {
    this.orientation = this.orientation.gauche();
  }

  public void tourneVersLaDroite() {
    this.orientation = this.orientation.droite();
  }

  public Position prochainePosition() {
    return new Position(position.x() + orientation.dx(), position.y() + orientation.dy());
  }

  public void seDeplaceVers(Position p) {
    this.position = p;
  }

  public void collecteUnTresor() {
    this.tresorsCollectes++;
  }
}
