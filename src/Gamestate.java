import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Gamestate {

    private Player[] players;
    private Board board;
    private Deck deck;
    private ScoreCard scoreCards;
    private int startingPlayer;
    private int turn;
    private int gameState;
    private String message;
    private boolean isEnding;
    private boolean toScore;
    private int mandatorySettlementsInARow;
    private ExtraAction movingExtraActionToUse;
    //drawing variables
    private boolean drawYesOrNo;
    private static final int yesOrNoButtonSize = 50;
    private static final int yesOrNoButtonX = 915 - 25;
    private static final int yesOrNoButtonY = 650;
    private static final int yesOrNoButtonSpacingX = 20 + yesOrNoButtonSize;
    private static final int messageX = 826;
    private static final int messageY = 625;


    public Gamestate() {
        players = new Player[4];
        for (int i = 0; i < 4; i++) players[i] = new Player(i);

        board = new Board();
        deck = new Deck();
        scoreCards = new ScoreCard();

        //instantiate objective cards
        mandatorySettlementsInARow = 0;

        newGame();
    }

    public void newGame() {
//        board.reset():
//        deck.reset();
        for (int i = 0; i < 4; i++) {
            players[i].setCard(deck.drawCard());
        }
        startingPlayer = (int) (Math.random() * 4);
        turn = startingPlayer;
        isEnding = false;
        toScore = false;
        drawYesOrNo = false;
        gameState = 1;
        //set message before each state occurs
        message = "Click on either an extra-action or the mandatory settlements";
    }

    public void playBasedOnState(int mouseX, int mouseY) {
        //TODO: making sure impossible moves are preemptively blocked
        switch (gameState) {

            case 1 -> {
                /*
                -detect what was clicked and see if it can be used
                -should be finished
                */

                if (players[turn].extraActionClicked(mouseX, mouseY) != null && players[turn].extraActionClicked(mouseX, mouseY).doesItMove() &&
                        !players[turn].extraActionClicked(mouseX, mouseY).isUsed() && players[turn].extraActionClicked(mouseX, mouseY).isIniated()) {
                    //todo
                    movingExtraActionToUse = players[turn].extraActionClicked(mouseX, mouseY).copy();
                    gameState = 2;
                    message = "Click which settlement you would like to move";
                }
                else if (players[turn].extraActionClicked(mouseX, mouseY) != null && !players[turn].extraActionClicked(mouseX, mouseY).doesItMove() &&
                        !players[turn].extraActionClicked(mouseX, mouseY).isUsed() && players[turn].extraActionClicked(mouseX, mouseY).isIniated()) {

                    //this works i think
                    players[turn].extraActionClicked(mouseX, mouseY).setAvailableMoves(board, players[turn], null);
                    message = "Click the highlighted hex you would like to settle";
                    gameState = 3;
                }
                else if (players[turn].mandatorySettlementsClicked(mouseX, mouseY) &&
                        players[turn].getMandatorySettlementPhase().equals(MandatorySettlementPhase.hasNotUsed)) {
                    //this works for sure
                    players[turn].setMandatorySettlementPhase(MandatorySettlementPhase.isUsing);
                    board.setMandatorySettlementHexes(players[turn]);
                    message = "Click the highlighted hex you would like to settle";
                    gameState = 3;
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

                            for(ExtraAction a : players[turn].getExtraActions()){
                                if(a.getExtraActionType().equals("harbor") && !a.isUsed()) a.setUsed(true);
                            }

                            message = "Click the highlighted hex you would like to settle";
                            gameState = 3;
                        }

                        else if (movingExtraActionToUse.getExtraActionType().equals("paddock")) {
                            //checking available moves for paddock may move into a method
                            for (int j = 0; j < 6; j++) {
                                if(board.getAllHexes().get(i).getNeighbors().get(j) != null && board.getAllHexes().get(i).getNeighbors().get(j).getNeighbors().get(j) != null &&
                                        !board.getAllHexes().get(i).getNeighbors().get(j).getNeighbors().get(j).isSettled()){
                                    boolean breaker = false;
                                    switch (board.getAllHexes().get(i).getNeighbors().get(j).getNeighbors().get(j).getType()) {
                                        case "desert", "flower", "meadow", "forest", "canyon" -> {

                                            movingExtraActionToUse.setAvailableMoves(board, players[turn], board.getAllHexes().get(i));

                                            for(ExtraAction a : players[turn].getExtraActions()) {
                                                if (a.getExtraActionType().equals("paddock") && !a.isUsed())
                                                    a.setUsed(true);
                                            }

                                            message = "Click the highlighted hex you would like to settle";
                                            gameState = 3;
                                            breaker = true;
                                        }
                                        default -> {
                                            //lol
                                        }
                                    }
                                    if(breaker) break;
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
                            gameState = 4;
                        } else if(players[turn].getMandatorySettlementPhase().equals(MandatorySettlementPhase.hasBeenUsed)){
                            board.clearBoard();
                            gameState = 4;
                        }
                        else if (mandatorySettlementsInARow == 2) { //made it 2 because must include zero
                            board.clearBoard();
                            players[turn].setMandatorySettlementPhase(MandatorySettlementPhase.hasBeenUsed);
                            mandatorySettlementsInARow = 0;
                            gameState = 4;
                        } else if (players[turn].getMandatorySettlementPhase().equals(MandatorySettlementPhase.isUsing)) {
                            mandatorySettlementsInARow++;
                            board.clearBoard();
                            board.setMandatorySettlementHexes(players[turn]);
                            gameState = 3;
                        } else if (players[turn].getMandatorySettlementPhase().equals(MandatorySettlementPhase.hasNotUsed)) {
                            board.clearBoard();
                            message = "Click on either an extra-action or the mandatory settlements";
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
                    message = "Click anywhere to score the next objective card";
                    gameState = 6;
                } else if (isEnding && players[turn].getSettlementsRemaining() == 0) {
                    turn = (turn + 1) % 4;
                    message = "Click on either an extra-action or the mandatory settlements";
                    //don't have to reset extraactions
                    mandatorySettlementsInARow = 0;
                    gameState = 1;
                } else if (players[turn].getMandatorySettlementPhase().equals(MandatorySettlementPhase.hasBeenUsed) &&
                        players[turn].areAllExtraActionUsed()) {
                    //this temp is to avoid pesky reference errors
                    TerrainCard temp = players[turn].getCard();
                    deck.discardCard(temp);
                    players[turn].setCard(deck.drawCard());
                    players[turn].resetExtraActions();
                    board.removeIfMovedAway(players[turn]);
                    message = "Click on either an extra-action or the mandatory settlements";
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

            case 5 ->{
                if(yesClicked(mouseX, mouseY)){
                    if(isEnding && (turn + 1) % 4 == startingPlayer) gameState = 5;
                    else{
                        TerrainCard temp = players[turn].getCard();
                        deck.discardCard(temp);
                        players[turn].setCard(deck.drawCard());
                        players[turn].resetExtraActions();
                        board.removeIfMovedAway(players[turn]);
                        message = "Click on either an extra-action or the mandatory settlements";
                        players[turn].setMandatorySettlementPhase(MandatorySettlementPhase.hasNotUsed);
                        turn = (turn + 1) % 4;
                        mandatorySettlementsInARow = 0;
                        drawYesOrNo = false;
                        gameState = 1;
                    }
                } else if(noClicked(mouseX, mouseY)){
                    drawYesOrNo = false;
                    board.clearBoard();
                    gameState = 1;
                }
            }
        }
    }

    public void draw(Graphics g) {
        if (toScore) {
            //TODO: draw scoring here
             g.clearRect(0, 0, 1200, 800);
            //so that the original array doesn't get messed up
            Player[] players2 = new Player[4];
            for(Player p :players){

            }
            //find out actual placement values later
            int x = 0;
            int y = 0;
            g.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 30));
            g.setColor(Color.BLACK);
            int a = players2[3].getId() + 1;
            int b = players2[2].getId() + 1;
            int c = players2[1].getId() + 1;
            int d = players2[0].getId() + 1;

            g.drawString("Player " + a + ": " + players2[3].getScore() + " gold", x, y);
            g.drawString("Player " + b + ": " + players2[2].getScore() + " gold", x, y + 30);
            g.drawString("Player " + c + ": " + players2[1].getScore() + " gold", x, y + 60);
            g.drawString("Player " + d  + ": " + players2[0].getScore() + " gold", x, y + 90);
        } else {
            if (drawYesOrNo) {
                g.setColor(new Color(245, 229, 193));
                g.fillRoundRect(yesOrNoButtonX, yesOrNoButtonY, yesOrNoButtonSize, yesOrNoButtonSize, 30, 30);
                g.fillRoundRect(yesOrNoButtonX + yesOrNoButtonSize + yesOrNoButtonSpacingX, yesOrNoButtonY, yesOrNoButtonSize, yesOrNoButtonSize, 30, 30);
                g.setColor(Color.BLACK);
                g.setFont(new Font("SansSerif", Font.PLAIN, 20));
                //todo: fix this
                g.drawString("yes", yesOrNoButtonX + 10, yesOrNoButtonY + 30);
                g.drawString("no",  yesOrNoButtonX + yesOrNoButtonSpacingX + yesOrNoButtonSize + 16 , yesOrNoButtonY + 30);
            }
            g.setColor(new Color(245, 229, 193));
            g.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g.drawString(message, messageX, messageY);
            board.drawBoard(g);
            players[turn].draw(g);
            scoreCards.draw(g);
            deck.draw(g);
        }
    }
    /*
    todo: fix this
     */
    private boolean yesClicked(int x, int y) {
        return x > yesOrNoButtonX && x < yesOrNoButtonX + yesOrNoButtonSize &&
                y > yesOrNoButtonY && y < yesOrNoButtonY + yesOrNoButtonSize;
    }

    private boolean noClicked(int x, int y) {
        return x > yesOrNoButtonX + yesOrNoButtonSize + yesOrNoButtonSpacingX && x < yesOrNoButtonX + yesOrNoButtonSpacingX + yesOrNoButtonSize * 2 &&
                y > yesOrNoButtonY && y < yesOrNoButtonY + yesOrNoButtonSize;
    }
}
