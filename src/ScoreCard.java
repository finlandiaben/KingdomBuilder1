import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class ScoreCard {
    private static final int SIZEX = 100;
    private static final int SIZEY = 125;
    private static final int x = 145;
    private static final int y = 50;
    private BufferedImage minersFace;
    private BufferedImage merchantsFace;
    private BufferedImage knightsFace;
    private static final int cardSpacingX = 20;

    public ScoreCard() {
        try {
            minersFace = ImageIO.read(Deck.class.getResource("/Images/KB-miners.png"));
            knightsFace = ImageIO.read(Deck.class.getResource("/Images/KB-knights.png"));
            merchantsFace = ImageIO.read(Deck.class.getResource("/Images/KB-farmers.png"));
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public void minerScore(Board board, Player player) {
        int score = 0;
        for (Hex hex : board.getAllHexes()) {
            if (hex.getSettlement() != null && hex.getSettlement().getOwnerId() == player.getId()) {
                for (Hex h : hex.getNeighbors())
                    if (h != null && h.getType().equals("mountain")) {
                        score += 1;
                        break;
                    }
            }
        }
        player.setScore(player.getScore() + score);
        System.out.println(player.getScore());
    }

    public void farmerScore(Board board, Player player) {
        int score = Integer.MAX_VALUE;
        for(int i = 1; i <= 4; i++){
            if(score > scoreQuadrant(board, i, player)) score = scoreQuadrant(board, i, player);
        }
        player.setScore(player.getScore() + score);
    }

    private int scoreQuadrant(Board board, int quadrant, Player player){
        int score = 0;
        switch(quadrant){
            case 1 ->{
                for(int i = 0; i < 10; i++){
                    for(int j = 20; j < 40; j += 2){
                        if(i % 2 == 1 && j == 20) j += 1;

                        if(board.getRow(i)[j].getSettlement() != null && board.getRow(i)[j].getSettlement().getOwnerId() == player.getId()){
                            score += 3;
                        }
                    }
                }
            }

            case 2 ->{
                for(int i = 0; i < 10; i++){
                    for(int j = 0; j < 20; j += 2){
                        if(i % 2 == 1 && j == 0) j += 1;

                        if(board.getRow(i)[j].getSettlement() != null && board.getRow(i)[j].getSettlement().getOwnerId() == player.getId()){
                            score += 3;
                        }
                    }
                }
            }

            case 3 ->{
                for(int i = 10; i < 20; i++){
                    for(int j = 0; j < 20; j += 2){
                        if(i % 2 == 1 && j == 0) j += 1;

                        if(board.getRow(i)[j].getSettlement() != null && board.getRow(i)[j].getSettlement().getOwnerId() == player.getId()){
                            score += 3;
                        }
                    }
                }
            }

            case 4 ->{
                for(int i = 10; i < 20; i++){
                    for(int j = 20; j < 40; j += 2){
                        if(i % 2 == 1 && j == 20) j += 1;

                        if(board.getRow(i)[j].getSettlement() != null && board.getRow(i)[j].getSettlement().getOwnerId() == player.getId()){
                            score += 3;
                        }
                    }
                }
            }

        }

        return score;
    }





    public void knightScore(Board board, Player player) {
        int score = 0;
        for(int i = 0; i < 20; i++){
            int tempScore = 0;
            for(Hex h : board.getRow(i)){
                if(h!= null && h.getSettlement() != null && h.getSettlement().getOwnerId() == player.getId()){
                    tempScore += 2;
                }
            }

            if(tempScore >= score){
                score = tempScore;
            }
        }

        player.setScore(player.getScore() + score);
    }

    public void draw(Graphics g) {
        g.drawImage(minersFace, x, y, SIZEX, SIZEY, null);
        g.drawImage(merchantsFace, x + +SIZEX + cardSpacingX, y, SIZEX, SIZEY, null);
        g.drawImage(knightsFace, x + (2 * SIZEX) + (2 * cardSpacingX), y, SIZEX, SIZEY, null);
    }
}
