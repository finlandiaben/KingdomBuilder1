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
        int score = 0;
        ArrayList<Hex> allHexes = board.getAllHexes();
        ArrayList<Hex> playerHexies = new ArrayList<Hex>();
        ArrayList<Hex> urSoHexy = new ArrayList<Hex>();

        for(Hex x : allHexes) //part 1 - Getting an arraylist of locations that have a neighboring settlement owned by the player (to save on run time).
        {
            if(x.getSettlement().getOwnerId()== player.getId())
                playerHexies.add(x);
        }
        for(Hex h : playerHexies)
        {
            ArrayList<Hex> tempNeighbors = h.getNeighbors();
            for(Hex z : tempNeighbors)
            {
                String type = z.getType();
                if(type.equals("city") || type.equals("oasis") || type.equals("paddock") || type.equals("tavern") || type.equals("harbor"))
                    urSoHexy.add(z);
            }
        }

        if(urSoHexy.size() < 2)
            return 0;

        boolean connected = false; //part 2 - Checking if each location is connected to another location, and adding 4 to score if it is.

        for(Hex hix : urSoHexy)
        {
            if(connected)
            {
                score+=4;
                connected = false;
            }
            for(Hex hox : urSoHexy)
                if(!connected)
                    if(!hox.equals(hix))
                    {
                        visited.clear();
                        connected = isConnected(board, player, hix, urSoHexy);
                    }
        }
        return score;
    }
    private ArrayList<Hex> visited = new ArrayList<Hex>();

    public boolean isConnected(Board b, Player p, Hex location1, ArrayList<Hex> otherSettlements) //extention to merchantScore to get recursion working
    {
        ArrayList<Hex> neighbors = location1.getNeighbors();

        visited.add(location1);
        for(Hex neighbor : neighbors)
        {
            if(!visited.contains(neighbor)) {
                if(otherSettlements.contains(neighbor))
                    return true;
                if(neighbor.getSettlement().getOwnerId()== p.getId())
                    if(isConnected(b, p, neighbor, otherSettlements))
                        return true;
            }
        }
        return false;
    }

    public int knightScore(Board board, Player player) {
        int score = 0; 
        ArrayList<Hex> allHexes = board.getAllHexes();
        for(int x = 0; x < 20; x++)
        {
            int tempScore = 0;
            for(int y = 0; x < 20; y++)
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
