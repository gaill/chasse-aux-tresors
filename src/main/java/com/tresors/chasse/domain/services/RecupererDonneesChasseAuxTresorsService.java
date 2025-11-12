package com.tresors.chasse.domain.services;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tresors.chasse.domain.model.Aventurier;
import com.tresors.chasse.domain.model.CarteAuxTresors;
import com.tresors.chasse.domain.model.DonneesChasseAuxTresors;
import com.tresors.chasse.domain.model.Mouvement;
import com.tresors.chasse.domain.model.Orientation;
import com.tresors.chasse.domain.model.Position;
import com.tresors.chasse.domain.ports.in.RecupererDonneesChasseAuxTresorsUseCase;

/** Service de récupération des données nécessaires à la simulation. */
@Service
public class RecupererDonneesChasseAuxTresorsService
    implements RecupererDonneesChasseAuxTresorsUseCase {

  @Override
  public DonneesChasseAuxTresors recupererDonneesDepuisLignes(List<String> lignes) {
    CarteAuxTresors carteAuxTresors = null;
    List<Aventurier> aventuriers = new ArrayList<>();
    int ordreDePassage = 0;

    for (String ligne : lignes) {
      // Découper sur les tirets
      String[] parts = ligne.split("-");
      parts = Arrays.stream(parts).map(String::trim).toArray(String[]::new);

      String type = parts[0];
      switch (type) {
        case "C" -> {
          // Carte
          if (parts.length < 3) {
            throw new IllegalArgumentException("Ligne C invalide : " + ligne);
          }
          int largeur = Integer.parseInt(parts[1]);
          int hauteur = Integer.parseInt(parts[2]);
          carteAuxTresors = new CarteAuxTresors(largeur, hauteur);
        }
        case "M" -> {
          // Montagne
          if (carteAuxTresors == null) {
            throw new IllegalStateException("La carte doit être définie avant les montagnes");
          }
          if (parts.length < 3) {
            throw new IllegalArgumentException("Ligne M invalide : " + ligne);
          }
          int x = Integer.parseInt(parts[1]);
          int y = Integer.parseInt(parts[2]);
          carteAuxTresors.mettreUneMontagneALaPosition(new Position(x, y));
        }
        case "T" -> {
          // Trésor
          if (carteAuxTresors == null) {
            throw new IllegalStateException("La carte doit être définie avant les trésors");
          }
          if (parts.length < 4) {
            throw new IllegalArgumentException("Ligne T invalide : " + ligne);
          }
          int x = Integer.parseInt(parts[1]);
          int y = Integer.parseInt(parts[2]);
          int nbTresor = Integer.parseInt(parts[3]);
          carteAuxTresors.mettreTresorsALaPosition(new Position(x, y), nbTresor);
        }
        case "A" -> {
          // Aventurier
          if (carteAuxTresors == null) {
            throw new IllegalStateException("La carte doit être définie avant les aventuriers");
          }
          if (parts.length < 6) {
            throw new IllegalArgumentException("Ligne A invalide : " + ligne);
          }
          String nom = parts[1];
          int x = Integer.parseInt(parts[2]);
          int y = Integer.parseInt(parts[3]);
          Orientation orientation = parseOrientation(parts[4]);
          Deque<Mouvement> mouvements = parseMovements(parts[5]);
          Aventurier adventurer =
              new Aventurier(nom, new Position(x, y), orientation, mouvements, ordreDePassage++);
          aventuriers.add(adventurer);
        }
        default -> throw new IllegalArgumentException("Type de ligne inconnu : " + type);
      }
    }

    if (carteAuxTresors == null) {
      throw new IllegalStateException("Aucune carte définie dans le fichier d’entrée.");
    }

    return new DonneesChasseAuxTresors(carteAuxTresors, aventuriers);
  }

  private Orientation parseOrientation(String orientation) {
    return switch (orientation.toUpperCase()) {
      case "N" -> Orientation.N;
      case "S" -> Orientation.S;
      case "E" -> Orientation.E;
      case "O" -> Orientation.O;
      default -> throw new IllegalArgumentException("Orientation inconnue : " + orientation);
    };
  }

  private Deque<Mouvement> parseMovements(String sequence) {
    Deque<Mouvement> deque = new ArrayDeque<>();
    for (char c : sequence.trim().toCharArray()) {
      deque.add(Mouvement.fromChar(c));
    }
    return deque;
  }
}
