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
    private ArrayList<Hex> visited = new ArrayList<Hex>();

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

    public int merchantScore(Board board, Player player) {
        int score = 0;
        ArrayList<Hex> allHexes = board.getAllHexes();
        ArrayList<Hex> playerHexies = new ArrayList<Hex>();
        ArrayList<Hex> urSoHexy = new ArrayList<Hex>();

        for(Hex x : allHexes) //part 1 - Getting an arraylist of locations that have a neighboring settlement owned by the player (to save on run time).
        {
        	if(x.getSettlement() != null)
        		if(x.getSettlement().getOwnerId() == player.getId())
        			playerHexies.add(x);
        }
        for(Hex h : playerHexies)
        {
            ArrayList<Hex> tempNeighbors = h.getNeighbors();
            for(Hex z : tempNeighbors)
            {
            	if(z != null && z.getSettlement() != null) {
                String type = z.getType();
                if(type.equals("city") || type.equals("oasis") || type.equals("paddock") || type.equals("tavern") || type.equals("harbor"))
                    urSoHexy.add(z); }
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
            visited.clear();
            connected = isConnected(board, player, hix, urSoHexy);
        }
        System.out.println("hexy = " + urSoHexy);
        return score;
    }

    public boolean isConnected(Board b, Player p, Hex location1, ArrayList<Hex> otherSettlements) //extention to merchantScore to get recursion working
    {
        ArrayList<Hex> neighbors = location1.getNeighbors();
        //System.out.println("On player" + p + "x and y: " + )

        visited.add(location1);
        for(Hex neighbor : neighbors)
        {
        	if(neighbor != null)
        		if(!visited.contains(neighbor)) {
        			if(otherSettlements.contains(neighbor))
        				return true;
        			if(neighbor.getSettlement() != null)
        				if(neighbor.getSettlement().getOwnerId() == p.getId())
        					if(isConnected(b, p, neighbor, otherSettlements))
        						return true;
            }
        }
        return false;
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
