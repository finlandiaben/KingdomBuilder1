import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

public class ExtraActionHex extends Hex {
    private boolean[] adjacentPlayers;
    private ArrayList<ExtraAction> extraActions;
    private String extraActionType;
    private static final int extraActionSpacingX = 2;
    private static final int extraActionSpacingY = 9;


    public ExtraActionHex(int[] pointsX, int[] pointsY, ArrayList<Hex> neighbors, String extraActionType) {
        super(pointsX, pointsY, neighbors);
        this.extraActionType = extraActionType;
        adjacentPlayers = new boolean[6];
        extraActions = new ArrayList<>();

        for (int i = 0; i < 6; i++) {
            adjacentPlayers[i] = false;
        }

        switch (extraActionType) {
            case "harbor" -> {
                extraActions.add(new Harbor(getPointsX()[0] - extraActionSpacingX, getPointsY()[0] - extraActionSpacingY));
                extraActions.add(new Harbor(getPointsX()[0] - extraActionSpacingX, getPointsY()[0] - extraActionSpacingY));
            }
            case "oasis" -> {
                extraActions.add(new Oasis(getPointsX()[0] - extraActionSpacingX, getPointsY()[0] - extraActionSpacingY));
                extraActions.add(new Oasis(getPointsX()[0] - extraActionSpacingX, getPointsY()[0] - extraActionSpacingY));
            }
            case "paddock" -> {
                extraActions.add(new Paddock(getPointsX()[0] - extraActionSpacingX, getPointsY()[0] - extraActionSpacingY));
                extraActions.add(new Paddock(getPointsX()[0] - extraActionSpacingX, getPointsY()[0] - extraActionSpacingY));
            }
            case "tavern" -> {
                extraActions.add(new Tavern(getPointsX()[0] - extraActionSpacingX, getPointsY()[0] - extraActionSpacingY));
                extraActions.add(new Tavern(getPointsX()[0] - extraActionSpacingX, getPointsY()[0] - extraActionSpacingY));
            }
        }
    }

    public void draw(Graphics g){
//        super.draw(g);
        for(int i = 0; i < extraActions.size(); i++){
            extraActions.get(i).draw(g);
        }
        g.setColor(Color.red);
        g.setFont(new Font("SansSerif", Font.PLAIN, 20));
        g.drawString(extraActions.size() + "", getPointsX()[0] + 5, getPointsY()[0] + 12);
    }
    public boolean isDepleted() {
        return extraActions.size() == 0;
    }

    public int howManyLeft(){
        return extraActions.size();
    }

    public void giveExtraAction(Player player) {
        if (!hasGivenToPlayer(player) && !isDepleted()) {
            player.giveExtraAction(extraActions.remove(0));
            adjacentPlayers[player.getId()] = true;
        }
    }

    public boolean hasGivenToPlayer(Player player) {
        return adjacentPlayers[player.getId()];
    }

    public boolean hasMovedAway(Player player) {
        for(Hex h : super.getNeighbors()){
            if(h.getSettlement() != null && h.getSettlement().getOwnerId() == player.getId()) return false;
        }
        return hasGivenToPlayer(player);
    }

    public String getType(){
        return extraActionType;
    }
}
