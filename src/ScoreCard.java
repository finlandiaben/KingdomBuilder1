import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class ScoreCard {
    private static final int SIZEX = 90;
    private static final int SIZEY = 150;
    private static final int x = 130;
    private static final int y = 5;
    private BufferedImage minersFace;
    private BufferedImage merchantsFace;
    private BufferedImage knightsFace;
    private static final int cardSpacingX = 30;
    
    public ScoreCard()
    {
        try {
            minersFace = ImageIO.read(Deck.class.getResource("/Images/KB-miners.png"));
            knightsFace = ImageIO.read(Deck.class.getResource("/Images/KB-knights.png"));
            merchantsFace = ImageIO.read(Deck.class.getResource("/Images/KB-merchants.png"));
        }catch(Exception e) {
            System.out.println(e);
        }
    }

    public int minerScore(Board board, Player player) {
        int score = 0;
        ArrayList<Hex> allHexes = board.getAllHexes();
        for(Hex x : allHexes)
        {
            if(x.getSettlement().getOwnerId() == player.getId())
            {
                ArrayList<Hex> neighborHexes = x.getNeighbors();
                for(Hex i : neighborHexes)
                    if(i.getType().equals("mountain"))
                        score += 1;
                        //need to break here because it doesn't matter how many u are touching
            }
        }
        return score;
    }
    
    public int merchantScore(Board board, Player player) {
        return 0;
    }

    
    public int knightScore(Board board, Player player) {
        int score = 0; 
        ArrayList<Hex> allHexes = board.getAllHexes();
        for(int x = 0; x < 20; x++)
        {
            int tempScore = 0;
            for(int y = 0; y < 20; y++)
            {
                if(allHexes.get((x*20)+y).getSettlement().getOwnerId() == player.getId())
                    tempScore+=2;
            }
            score = Math.max(tempScore, score);
        }
        return score;
    }

    public void draw(Graphics g) {
        g.drawImage(minersFace, x, y, SIZEX, SIZEY, null);
        g.drawImage(merchantsFace, x + + SIZEX + cardSpacingX, y, SIZEX, SIZEY, null);
        g.drawImage(knightsFace, x + (2 * SIZEX) + (2 * cardSpacingX), y, SIZEX, SIZEY, null);
    }
}
