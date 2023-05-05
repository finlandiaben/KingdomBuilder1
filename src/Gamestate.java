//todo: check ending conditions, check leaderboard graphics, merchants, testing
//todo bug: last player to play before game end cannot use extraactions

import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import javax.imageio.ImageIO;

public class Gamestate {

    private BufferedImage logo;
    public static final int logoSizeX = 350;
    public static final int logoSizeY = 200;
    public static final int logoX = 1200 / 2 - logoSizeX / 2;
    public static final int logoY = 50;
    private Player[] players;
    private Board board;
    private Deck deck;
    private ScoreCard scoreCards;
    private int startingPlayer;
    private int turn;
    private int gameState;
    private String message;
    private boolean isEnding;
    private boolean drawScoring;
    private boolean drawLeaderBoard;
    private int mandatorySettlementsInARow;
    private ExtraAction movingExtraActionToUse;
    //drawing variables
    private boolean drawYesOrNo;
    private boolean drawStart;
    private static final int yesOrNoButtonSize = 50;
    private static final int yesOrNoButtonX = 915 - 25;
    private static final int yesOrNoButtonY = 650;
    private static final int yesOrNoButtonSpacingX = 20 + yesOrNoButtonSize;
    private static final int messageX = 826;
    private static final int messageY = 625;
    public static final int headerX = 10;
    public static final int headerY = 15;
    public static final int headerWidth = 1125;
    public static final int headerHeight = 35;
    public static final int headerMessageSpacing = 170;
    public static final int drawScoreX = 925;
    public static final int drawScoreY = 400;
    public static final int drawScoreBackgroundSpacingX = 25;
    public static final int drawScoreBackgroundSpacingY = 43;
    public static final int drawScoreBackgroundSize = 150;
    public static final int drawScoreSpacingY = 30;


    public Gamestate() {
        try {
            logo = ImageIO.read(Gamestate.class.getResource("/Images/logo.png"));
            drawStart = true;
            players = new Player[4];
            for (int i = 0; i < 4; i++) players[i] = new Player(i);

            board = new Board();
            deck = new Deck();
            scoreCards = new ScoreCard();

            //instantiate objective cards
            mandatorySettlementsInARow = 0;

            newGame();
        } catch (Exception e) {

        }
    }

    public void newGame() {
//        board.reset():
//        deck.reset();
        for (int i = 0; i < 4; i++) {
            players[i].setCard(deck.drawCard());
        }
        startingPlayer = (int) (Math.random() * 4);
        players[startingPlayer].setStart(true);
        turn = startingPlayer;
        isEnding = false;
        drawScoring = false;
        drawLeaderBoard = false;
        drawYesOrNo = false;
        gameState = -1;
        //set message before each state occurs
        message = "Click anywhere to play";
    }

    public void playBasedOnState(int mouseX, int mouseY) {
        //TODO: making sure impossible moves are preemptively blocked
        switch (gameState) {
            case -1 -> {
                message = "Click on either an extra-action or the settlement icon";
                gameState = 1;
                drawStart = false;
            }

            case 1 -> {
                /*
                -detect what was clicked and see if it can be used
                -should be finished
                */
                if (!board.canSetMandatory(players[turn])) {
                    TerrainCard temp = players[turn].getCard();
                    deck.discardCard(temp);
                    players[turn].setCard(deck.drawCard());
                } else {

                    if (players[turn].extraActionClicked(mouseX, mouseY) != null && players[turn].extraActionClicked(mouseX, mouseY).doesItMove() &&
                            !players[turn].extraActionClicked(mouseX, mouseY).isUsed() && players[turn].extraActionClicked(mouseX, mouseY).isIniated() &&
                            players[turn].extraActionClicked(mouseX, mouseY).canSetAvailableMoves(board, players[turn], null)) {

                        movingExtraActionToUse = players[turn].extraActionClicked(mouseX, mouseY).copy();
                        gameState = 2;
                        message = "Click which settlement you would like to move";
                    } else if (players[turn].extraActionClicked(mouseX, mouseY) != null && !players[turn].extraActionClicked(mouseX, mouseY).doesItMove() &&
                            !players[turn].extraActionClicked(mouseX, mouseY).isUsed() && players[turn].extraActionClicked(mouseX, mouseY).isIniated() &&
                            players[turn].extraActionClicked(mouseX, mouseY).canSetAvailableMoves(board, players[turn], null)) {

                        //this works i think
                        players[turn].extraActionClicked(mouseX, mouseY).setAvailableMoves(board, players[turn], null);
                        message = "Click the highlighted hex you would like to settle";
                        gameState = 3;
                    } else if (players[turn].mandatorySettlementsClicked(mouseX, mouseY) && players[turn].getMandatorySettlementPhase().equals(MandatorySettlementPhase.hasNotUsed)) {

                        players[turn].setMandatorySettlementPhase(MandatorySettlementPhase.isUsing);
                        board.setMandatorySettlementHexes(players[turn]);
                        message = "Click the highlighted hex you would like to settle";
                        gameState = 3;
                    }
                }
            }

            case 2 -> {
                /*
                use either harbor or paddock to set moves
                 */
                for (int i = 0; i < board.getAllHexes().size(); i++) {

                    if (board.getAllHexes().get(i).isClicked(mouseX, mouseY) && board.getAllHexes().get(i).getSettlement() != null && board.getAllHexes().get(i).getSettlement().getOwnerId() == turn) {

                        if (movingExtraActionToUse.getExtraActionType().equals("harbor")) {

                            movingExtraActionToUse.setAvailableMoves(board, players[turn], board.getAllHexes().get(i));

                            for (ExtraAction a : players[turn].getExtraActions()) {
                                if (a.getExtraActionType().equals("harbor") && !a.isUsed()){
                                    a.setUsed(true); break;
                                }
                            }

                            message = "Click the highlighted hex you would like to settle";
                            gameState = 3;
                        } else if (movingExtraActionToUse.getExtraActionType().equals("paddock")) {
                            //checking available moves for paddock may move into a method
                            for (int j = 0; j < 6; j++) {
                                if (board.getAllHexes().get(i).getNeighbors().get(j) != null && board.getAllHexes().get(i).getNeighbors().get(j).getNeighbors().get(j) != null &&
                                        !board.getAllHexes().get(i).getNeighbors().get(j).getNeighbors().get(j).isSettled()) {
                                    boolean breaker = false;
                                    switch (board.getAllHexes().get(i).getNeighbors().get(j).getNeighbors().get(j).getType()) {
                                        case "desert", "flower", "meadow", "forest", "canyon" -> {

                                            movingExtraActionToUse.setAvailableMoves(board, players[turn], board.getAllHexes().get(i));

                                            for (ExtraAction a : players[turn].getExtraActions()) {
                                                if (a.getExtraActionType().equals("paddock") && !a.isUsed())
                                                    a.setUsed(true); break;
                                            }

                                            message = "Click the highlighted hex you would like to settle";
                                            gameState = 3;
                                            breaker = true;
                                        }
                                        default -> {
                                            //lol
                                        }
                                    }
                                    if (breaker) break;
                                }
                            }
                        }
                    }
                }
            }

            case 3 -> {
                /*
                placing settlement then managing scenarios
                */

                for (int i = 0; i < board.getAllHexes().size(); i++) {

                    if (board.getAllHexes().get(i).isClicked(mouseX, mouseY) &&
                            board.getAllHexes().get(i).isHighlighted()) {

                        players[turn].placeSettlement(board.getAllHexes().get(i));

                        //check for neighboring ExtraAction
                        for (Hex h : board.getAllHexes().get(i).getNeighbors()) {
                            if (h != null && (h.getType().equals("harbor") || h.getType().equals("oasis") ||
                                    h.getType().equals("paddock") || h.getType().equals("tavern")) &&
                                    !h.hasGivenToPlayer(players[turn])) {

                                h.giveExtraAction(players[turn]);
                            }
                        }

                        if (players[turn].getSettlementsRemaining() == 0) {
                            board.clearBoard();
                            isEnding = true;
                            //todo: sus
                            message = "Click anywhere to proceed";
                            gameState = 4;
                        } else if (players[turn].getMandatorySettlementPhase().equals(MandatorySettlementPhase.hasBeenUsed)) {
                            board.clearBoard();
                            message = "Click anywhere to proceed";
                            gameState = 4;
                        } else if (mandatorySettlementsInARow == 2) { //made it 2 because must include zero
                            board.clearBoard();
                            players[turn].setMandatorySettlementPhase(MandatorySettlementPhase.hasBeenUsed);
                            mandatorySettlementsInARow = 0;
                            message = "Click anywhere to proceed";
                            gameState = 4;
                        } else if (players[turn].getMandatorySettlementPhase().equals(MandatorySettlementPhase.isUsing)) {
                            mandatorySettlementsInARow++;
                            board.clearBoard();
                            board.setMandatorySettlementHexes(players[turn]);
                            gameState = 3;
                        } else if (players[turn].getMandatorySettlementPhase().equals(MandatorySettlementPhase.hasNotUsed)) {
                            board.clearBoard();
                            message = "Click on either an extra-action or the settlement icon";
                            gameState = 1;
                        }
                        break;
                    }

                }
            }

            case 4 -> {
                /*
                resetting everything for the next player
                 */

                if (isEnding && (turn + 1) % 4 == startingPlayer) {
                    for (int i = 0; i < 4; i++) {
                        scoreCards.minerScore(board, players[i]);
                    }

                    message = "Miners scored! Click anywhere to score Merchants!";
                    drawScoring = true;
                    gameState = 6;
                } else if (isEnding && players[turn].getSettlementsRemaining() == 0) {
                    TerrainCard temp = players[turn].getCard();
                    deck.discardCard(temp);
                    players[turn].setCard(deck.drawCard());
                    turn = (turn + 1) % 4;
                    message = "Click on either an extra-action or the settlement icon";
                    //don't have to reset extraactions
                    mandatorySettlementsInARow = 0;
                    gameState = 1;
                } else if (players[turn].getMandatorySettlementPhase().equals(MandatorySettlementPhase.hasBeenUsed) &&
                        players[turn].areAllExtraActionUsed() || !players[turn].canUseExtraActions(board, players[turn], null)) {
                    //this temp is to avoid pesky reference errors
                    TerrainCard temp = players[turn].getCard();
                    deck.discardCard(temp);
                    players[turn].setCard(deck.drawCard());
                    players[turn].resetExtraActions();
                    board.removeIfMovedAway(players[turn]);
                    message = "Click on either an extra-action or the settlement icon";
                    players[turn].setMandatorySettlementPhase(MandatorySettlementPhase.hasNotUsed);
                    mandatorySettlementsInARow = 0;
                    turn = (turn + 1) % 4;
                    gameState = 1;
                } else if (players[turn].getMandatorySettlementPhase().equals(MandatorySettlementPhase.hasBeenUsed)) {
                    message = "Would you like to end your turn?";
                    drawYesOrNo = true;
                    gameState = 5;
                }
            }

            case 5 -> {
                if (yesClicked(mouseX, mouseY)) {
                    if (isEnding && (turn + 1) % 4 == startingPlayer) gameState = 5;
                    else {
                        TerrainCard temp = players[turn].getCard();
                        deck.discardCard(temp);
                        players[turn].setCard(deck.drawCard());
                        players[turn].resetExtraActions();
                        board.removeIfMovedAway(players[turn]);
                        message = "Click on either an extra-action or the settlement icon";
                        players[turn].setMandatorySettlementPhase(MandatorySettlementPhase.hasNotUsed);
                        turn = (turn + 1) % 4;
                        mandatorySettlementsInARow = 0;
                        drawYesOrNo = false;
                        gameState = 1;
                    }
                } else if (noClicked(mouseX, mouseY)) {
                    drawYesOrNo = false;
                    board.clearBoard();
                    message = "Click on either an extra-action or the settlement icon";
                    gameState = 1;
                }
            }

            case 6 -> {
                for(int i = 0; i < 3; i++){
                    scoreCards.farmerScore(board, players[i]);
                }
                message = "Farmers scored! Click anywhere to score Knights!";
                gameState = 7;
            }

            case 7 -> {
                for(int i = 0; i  < 4; i++){
                    scoreCards.knightScore(board, players[i]);
                }

                message = "Knights scored! Click anywhere to score City!";
                gameState = 8;
            }

            case 8 -> {
                for(int i = 0; i  < 4; i++){
                    board.cityScore(players[i]);
                }

                message = "City scored! Click anywhere to see the leaderboard!";
                gameState = 9;
            }
            case 9 ->{
                message = "";
                drawLeaderBoard = true;
            }
        }
    }

    public void draw(Graphics g) {
        if (drawStart) {
            g.drawImage(logo, logoX, logoY, logoSizeX, logoSizeY - 10, null);
            g.setColor(new Color(245, 229, 193));
            g.setFont(new Font("Algerian", Font.PLAIN, 20));
            g.drawString(message, logoX + 65, logoY + 600);
        } else if (drawLeaderBoard) {

            String[] ranks = setRanks();
            board.drawBoard(g);
            scoreCards.draw(g);
            drawHeader(g);
            g.setColor(new Color(245, 229, 193));
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.drawString(message, messageX, messageY);
            g.fillRoundRect(drawScoreX - drawScoreBackgroundSpacingX, drawScoreY - drawScoreBackgroundSpacingY, drawScoreBackgroundSize, drawScoreBackgroundSize, 30, 30);
            //todo
            g.setFont(new Font("SansSerif", Font.PLAIN, 20));

            for (int i = 0; i < 4; i++) {
                switch (i) {
                    case 0 -> {
                        g.setColor(new Color(242, 122, 10));
                    }
                    case 1 -> {
                        g.setColor(new Color(200, 66, 245));
                    }
                    case 2 -> {
                        g.setColor(Color.black);
                    }
                    case 3 -> {
                        g.setColor(new Color(173,166,166));
                    }
                }

                int t = i + 1;
                g.drawString(ranks[i] + ": " +players[i].getScore(), drawScoreX, drawScoreY + drawScoreSpacingY * i);
            }

        } else if (drawScoring) {
            board.drawBoard(g);
            scoreCards.draw(g);
            drawHeader(g);
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.setColor(new Color(245, 229, 193));
            g.fillRoundRect(drawScoreX - drawScoreBackgroundSpacingX, drawScoreY - drawScoreBackgroundSpacingY, drawScoreBackgroundSize, drawScoreBackgroundSize, 30, 30);
            g.drawString(message, messageX, messageY);

            g.setFont(new Font("SansSerif", Font.PLAIN, 20));
            for (int i = 0; i < 4; i++) {
                switch (i) {
                    case 0 -> {
                        g.setColor(new Color(242, 122, 10));
                    }
                    case 1 -> {
                        g.setColor(new Color(200, 66, 245));
                    }
                    case 2 -> {
                        g.setColor(Color.black);
                    }
                    case 3 -> {
                        g.setColor(Color.gray);
                    }
                }
                int t = i + 1;

                g.drawString("Player " + t + ": " + players[i].getScore(), drawScoreX, drawScoreY + drawScoreSpacingY * i);
            }

        } else {
            if (drawYesOrNo) {
                g.setColor(new Color(245, 229, 193));
                g.fillRoundRect(yesOrNoButtonX, yesOrNoButtonY, yesOrNoButtonSize, yesOrNoButtonSize, 30, 30);
                g.fillRoundRect(yesOrNoButtonX + yesOrNoButtonSize + yesOrNoButtonSpacingX, yesOrNoButtonY, yesOrNoButtonSize, yesOrNoButtonSize, 30, 30);
                g.setColor(Color.BLACK);
                g.setFont(new Font("SansSerif", Font.PLAIN, 20));
                g.drawString("yes", yesOrNoButtonX + 10, yesOrNoButtonY + 30);
                g.drawString("no", yesOrNoButtonX + yesOrNoButtonSpacingX + yesOrNoButtonSize + 16, yesOrNoButtonY + 30);
            }
            board.drawBoard(g);
            scoreCards.draw(g);
            drawHeader(g);
            g.setColor(new Color(245, 229, 193));
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.drawString(message, messageX, messageY);
            players[turn].draw(g);
            deck.draw(g);
        }
    }

    private void drawHeader(Graphics g) {
        g.setColor(new Color(245, 229, 193));
        g.fillRoundRect(headerX, headerY, headerWidth, headerHeight, 50, 30);
        g.setColor(new Color(150, 75, 0));
        g.setFont(new Font("SansSerif", Font.PLAIN, 20));
        g.drawString("Settlements left ", headerX + 20, headerY + 20);
        for (int i = 0; i < 4; i++) {
            switch (i) {
                case 0 -> {
                    g.setColor(new Color(242, 122, 10));
                }
                case 1 -> {
                    g.setColor(new Color(200, 66, 245));
                }
                case 2 -> {
                    g.setColor(Color.black);
                }
                case 3 -> {
                    g.setColor(Color.gray);
                }
            }
            int t = i + 1;
            g.drawString("Player " + t + ": " + players[i].getSettlementsRemaining(), headerX + 20 + headerMessageSpacing * (i + 1), headerY + 22);
        }
    }

    private boolean yesClicked(int x, int y) {
        return x > yesOrNoButtonX && x < yesOrNoButtonX + yesOrNoButtonSize &&
                y > yesOrNoButtonY && y < yesOrNoButtonY + yesOrNoButtonSize;
    }

    private boolean noClicked(int x, int y) {
        return x > yesOrNoButtonX + yesOrNoButtonSize + yesOrNoButtonSpacingX && x < yesOrNoButtonX + yesOrNoButtonSpacingX + yesOrNoButtonSize * 2 &&
                y > yesOrNoButtonY && y < yesOrNoButtonY + yesOrNoButtonSize;
    }

    private String[] setRanks(){
        String[] ranks = new String[players.length];
        int[] scores = new int[players.length];

        for(int i = 0; i < scores.length; i++) {
            scores[i] = players[i].getScore();
        }

        Arrays.sort(scores);

        int counter = 1;
        int rank = 1;
        for(int i = scores.length - 1; i >= 0; ) {
            for(int j = i - 1; j >= 0; j--) {
                if(scores[j] == scores[i]) {
                    counter++;
                }
            }


            switch(rank) {
                case 1:
                    for(int j = 0; j < ranks.length; j++) {
                        if(players[j].getScore() == scores[i]) {
                            ranks[j] = "1st";

                            if(counter > 1) {
                                ranks[j] += " (tie)";
                            }
                        }
                    }
                    break;

                case 2:
                    for(int j = 0; j < ranks.length; j++) {
                        if(players[j].getScore() == scores[i]) {
                            ranks[j] = "2nd";

                            if(counter > 1) {
                                ranks[j] += " (tie)";
                            }
                        }
                    }
                    break;

                case 3:
                    for(int j = 0; j < ranks.length; j++) {
                        if(players[j].getScore() == scores[i]) {
                            ranks[j] = "3rd";

                            if(counter > 1) {
                                ranks[j] += " (tie)";
                            }
                        }
                    }
                    break;

                case 4:
                    for(int j = 0; j < ranks.length; j++) {
                        if(players[j].getScore() == scores[i]) {
                            ranks[j] = "4th";

                            if(counter > 1) {
                                ranks[j] += " (tie)";
                            }
                        }
                    }
                    break;
            }

            rank++;
            i -= counter;
        }

        return ranks;
    }
}
