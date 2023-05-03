import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class ScoreCard {
    private static final int SIZEX = 70;
    private static final int SIZEY = 90;
    private static final int x = 315 - 100 - 12;
    private static final int y = 60;
    private BufferedImage minersFace;
    private BufferedImage merchantsFace;
    private BufferedImage knightsFace;
    private static final int cardSpacingX = 30;

    public ScoreCard() {
        try {
            minersFace = ImageIO.read(Deck.class.getResource("/Images/KB-miners.png"));
            knightsFace = ImageIO.read(Deck.class.getResource("/Images/KB-knights.png"));
            merchantsFace = ImageIO.read(Deck.class.getResource("/Images/KB-merchants.png"));
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

    public void merchantScore(Board board) {
    }



    public void knightScore(Board board, Player player) {
        int score = 0;
        int tempScore = 0;
        for(int i = 0; i < 20; i++){
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
