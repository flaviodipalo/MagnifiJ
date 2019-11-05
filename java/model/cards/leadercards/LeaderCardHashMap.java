package model.cards.leadercards;

import model.players.Player;
import controller.states.*;

import java.util.HashMap;
import java.util.Map;

/**
 * The hashMap to handle the the leaderCards
 */
public class LeaderCardHashMap {

    @FunctionalInterface
    public interface LeaderCardMap {
        void overriddenFunction(Player player);
    }

    public static final Map<String, LeaderCardMap> leaderCardHashMap = new HashMap<String, LeaderCardMap>() {
        {
            put("Lucrezia Borgia", this::onLucreziaBorgia);
            put("Ludovico il Moro", this::onLudovicoIlMoro);
            put("Sigismondo Malatesta", this::onSigismondoMalatesta);
            put("Pico della Mirandola", this::onPicoDellaMirandola);
            put("Ludovico Ariosto", this::onLudovicoAriosto);
            put("Cesare Borgia", this::onCesareBorgia);
            put("Filippo Brunelleschi", this::onFilippoBrunelleschi);
            put("Santa Rita", this::onSantaRita);
            put("Sisto IV", this::onSistoIV);
            put("Lorenzo de' Medici", this::onLorenzoDeMedici);

        }

        private void onLucreziaBorgia(Player player) {
            player.setFamilyMemberState(new LucreziaBorgiaFamilyMemberState(player.getFamilyMemberState()));
        }

        private void onLudovicoIlMoro(Player player) {
            player.setFamilyMemberState(new LudovicoIlMoroFamilyMemberState(player));
        }

        private void onSigismondoMalatesta(Player player) {
            player.setNeutralMemberState(new SigismondoNeutralMemberState(player));
        }

        private void onPicoDellaMirandola(Player player) {
            player.setPickCardState(new PicoDellaMirandolaPickCardState(player.getPickCardState()));
        }

        private void onLudovicoAriosto(Player player) {
            player.setIsPositionOccupiedState(new IsPositionOccupiedLudovicoAriostoState(player.getIsPositionOccupiedState()));
        }

        private void onCesareBorgia(Player player) {
            player.setHasMilitaryPointsForGreenCardsState(new CesareBorgiaHasMilitaryPointsForGreenCards(player.getHasMilitaryPointsForGreenCardsState()));
        }

        private void onFilippoBrunelleschi(Player player) {
            player.setTowerOccupiedResourcesState(new TowerOccupiedResourcesFilippoBrunelleschiState(player.getTowerOccupiedResourcesState()));
        }

        private void onSantaRita(Player player) {
            player.setReceiveInstantRewardState(new SantaRitaReceiveInstantRewardState(player));
        }

        private void onSistoIV(Player player) {
            player.setShowSupportBonusState(new SistoIVShowSupportBonusState(player.getShowSupportBonusState()));
        }

        private void onLorenzoDeMedici(Player player) {
            player.activateLorenzoDeMedici();
        }

    };


}
