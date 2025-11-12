package com.tresors.chasse.domain.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tresors.chasse.domain.model.Aventurier;
import com.tresors.chasse.domain.model.CarteAuxTresors;
import com.tresors.chasse.domain.model.Case;
import com.tresors.chasse.domain.model.DonneesChasseAuxTresors;
import com.tresors.chasse.domain.model.Mouvement;
import com.tresors.chasse.domain.model.Position;
import com.tresors.chasse.domain.model.TypeDeCase;
import com.tresors.chasse.domain.ports.in.SimulerChasseAuxTresorsUseCase;

/** SimulerChasseAuxTresorsService : implémente la logique tour par tour. */
@Service
public class SimulerChasseAuxTresorsService implements SimulerChasseAuxTresorsUseCase {
  @Override
  public DonneesChasseAuxTresors simuler(
      CarteAuxTresors carteAuxTresors, List<Aventurier> aventuriers) {
    // Position initiale des aventuriers
    placerAventuriersSurLaCarte(carteAuxTresors, aventuriers);

    // Boucle tant qu'au moins un aventurier a des mouvements restants
    boolean unAventurierAEncoreUnMouvement =
        aventuriers.stream().anyMatch(Aventurier::aUnProchainMouvement);
    while (unAventurierAEncoreUnMouvement) {

      // parcourir les aventuriers dans l'ordre
      for (Aventurier aventurier : aventuriers) {
        if (!aventurier.aUnProchainMouvement()) {
          continue;
        }

        Mouvement mouvement = aventurier.prochainMouvement();
        switch (mouvement) {
          case G -> aventurier.tourneVersLaGauche();
          case D -> aventurier.tourneVersLaDroite();
          case A -> {
            Position prochainePosition = aventurier.prochainePosition();

            // Vérifier si la prochaine position est valide
            Case caseCible = recupererCaseCible(carteAuxTresors, prochainePosition);
            if (caseCible == null) {
              break;
            }

            // Déplacement
            carteAuxTresors
                .caseALaPosition(aventurier.getPosition())
                .ifPresent(Case::enleverOccupant);
            aventurier.seDeplaceVers(prochainePosition);
            caseCible.setOccupant(aventurier);

            // Collecte d'un tresor
            if (caseCible.tresorRecupere()) {
              aventurier.collecteUnTresor();
            }
          }
        }
      }
      unAventurierAEncoreUnMouvement =
          aventuriers.stream().anyMatch(Aventurier::aUnProchainMouvement);
    }
    return new DonneesChasseAuxTresors(carteAuxTresors, aventuriers);
  }

  private static void placerAventuriersSurLaCarte(
      CarteAuxTresors carteAuxTresors, List<Aventurier> aventuriers) {
    for (Aventurier aventurier : aventuriers) {
      carteAuxTresors
          .caseALaPosition(aventurier.getPosition())
          .ifPresent(c -> c.setOccupant(aventurier));
    }
  }

  private static Case recupererCaseCible(
      CarteAuxTresors carteAuxTresors, Position prochainePosition) {
    if (!prochainePosition.compriseEntre(
        carteAuxTresors.getLargeur(), carteAuxTresors.getHauteur())) {
      return null;
    }
    Optional<Case> opt = carteAuxTresors.caseALaPosition(prochainePosition);
    if (opt.isEmpty()) {
      return null;
    }
    Case caseCible = opt.get();
    if (caseCible.getType() == TypeDeCase.MONTAGNE) {
      return null;
    }
    if (caseCible.getOccupant() != null) {
      return null;
    }
    return caseCible;
  }
}
