import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

//TODO: setting extra action coordinates when they are entered into the ExtraActions ArrayList

public class Player {

    private static final int boxX = 825;
    private static final int boxY = 400;
    private static final int boxSizeX = 300;
    private static final int boxSizeY = 200;
    private static final int playerNameMarginX = 175;
    private static final int playerNameMarginY = 175;
    private static final int mandatorySettlementButtonSize = 50;
    private static final int mandatorySettlementsButtonSpacingX = 20;
    private static final int mandatorySettlementsButtonSpacingY = 140;
    private static final int extraActionRelativeX = 10;
    private static final int extraActionRelativeY = 10;
    private static final int extraActionSpacingX = ExtraAction.sizeX + 15;
    private static final int extraActionSpacingY = ExtraAction.sizeY + 5;
    private static final int[] mandatorySettlementButtonPointsX = {
                    boxX + mandatorySettlementsButtonSpacingX,
                    boxX + mandatorySettlementsButtonSpacingX + mandatorySettlementButtonSize / 2,
                    boxX + mandatorySettlementsButtonSpacingX + mandatorySettlementButtonSize,
                    boxX + mandatorySettlementsButtonSpacingX + mandatorySettlementButtonSize,
                    boxX + mandatorySettlementsButtonSpacingX};
    private static final int[] mandatorySettlementButtonPointsY = {
                    boxY + mandatorySettlementsButtonSpacingY,
                    boxY + mandatorySettlementsButtonSpacingY - 20,
                    boxY + mandatorySettlementsButtonSpacingY,
                    boxY + mandatorySettlementsButtonSpacingY + mandatorySettlementButtonSize,
                    boxY + mandatorySettlementsButtonSpacingY + mandatorySettlementButtonSize };
    private static final int cardMarginX = 1025 - boxX;
    private static final int cardMarginY = 415 - boxY;
    private Color settlementButtonColor;
    private final int id;
    private ArrayList<Settlement> settlements;
    private ArrayList<ExtraAction> extraActions;
    private TerrainCard card;
    private int score;
    private MandatorySettlementPhase mandatorySettlementPhase;

    public Player(int id){
        this.id = id;
        settlements = new ArrayList<>();
        
        for(int i = 0; i < 40; i++){
            settlements.add(new Settlement(this.id));
        }
        
        extraActions = new ArrayList<>();
        int score = 0;
        mandatorySettlementPhase = MandatorySettlementPhase.hasNotUsed;

        if (id == 0) {
            settlementButtonColor = new Color(242, 122, 10);
        } else if (id == 1) {
            settlementButtonColor = new Color(200, 66, 245);
        } else if (id == 2) {
            settlementButtonColor = Color.black;
        } else if (id == 3) {
            settlementButtonColor = Color.gray;
        }
    }

    public int getId(){
        return id;
    }

    public int getScore(){
        return score;
    }

    public void setScore(int score){
        this.score = score;
    }

    public int getSettlementsRemaining(){
        return settlements.size();
    }

    public void placeSettlement(Hex h){
        if(getSettlementsRemaining() > 0){
            h.placeSettlement(settlements.remove(0));
        }
    }

    public void giveExtraAction(ExtraAction extraAction){
        extraActions.add(extraAction);
    }

    public TerrainCard getCard(){
        return card;
    }
    public void setCard(TerrainCard c){
        card = c;
    }

    public void draw(Graphics g){
        //set bg color
        g.setColor(new Color(245, 229, 193));
        //create bg
        g.fillRoundRect(boxX, boxY, boxSizeX, boxSizeY, 50, 30);
       //draw player name
        g.setFont(new Font("Times New Roman", Font.BOLD | Font.ITALIC, 30));
        g.setColor(Color.BLACK);
        int t = getId() + 1;
        g.drawString("Player " + t, boxX + playerNameMarginX, boxY + playerNameMarginY);
        //drawing settlement button
        g.setColor(settlementButtonColor);
        g.fillPolygon(mandatorySettlementButtonPointsX, mandatorySettlementButtonPointsY, 5);
        //draw amount of settlements left
        if(id >= 2) {
            g.setColor(Color.WHITE);
        }else {
            g.setColor(Color.BLACK);
        }
        g.setFont(new Font("SansSerif", Font.PLAIN, 20));
        g.drawString(" " + getSettlementsRemaining(), mandatorySettlementButtonPointsX[0] + 9, mandatorySettlementButtonPointsY[0] + 25);
        //draw card
        card.draw(g, boxX + cardMarginX, boxY + cardMarginY);

        for(int i = 0; i < extraActions.size(); i++){
            if(!extraActions.get(i).isUsed() && extraActions.get(i).isIniated()){
                extraActions.get(i).setX(boxX + extraActionRelativeX + (i % 4) * extraActionSpacingX);
                extraActions.get(i).setY(boxY + extraActionRelativeY + (i / 4) * extraActionSpacingY);
                extraActions.get(i).draw(g, boxX + extraActionRelativeX + (i % 4) * extraActionSpacingX, boxY + extraActionRelativeY + (i / 4) * extraActionSpacingY);
            }
        }

    }

    public ExtraAction extraActionClicked(int mouseX, int mouseY){
        //perhaps we can later figure this out w/basic math instead of with a bunch of methods
        for(int i = 0; i < extraActions.size(); i++){
            if(extraActions.get(i).isClicked(mouseX, mouseY)) return extraActions.get(i);
        }

        return null;

    }

    //chatGPT with the assist
    public boolean mandatorySettlementsClicked(int x, int y) {

        // initialize the crossing number
        int cn = 0;

        // iterate over the edges of the pentagon
        for (int i = 0; i < Player.mandatorySettlementButtonPointsX.length; i++) {
            // get the coordinates of the endpoints of the current edge
            double x1 = Player.mandatorySettlementButtonPointsX[i];
            double y1 = Player.mandatorySettlementButtonPointsY[i];
            double x2 = Player.mandatorySettlementButtonPointsX[(i+1) % Player.mandatorySettlementButtonPointsX.length];
            double y2 = Player.mandatorySettlementButtonPointsY[(i+1) % Player.mandatorySettlementButtonPointsY.length];

            // check if the edge crosses the horizontal ray from the point
            if (((y1 <= y) && (y2 > y)) || ((y1 > y) && (y2 <= y))) {
                // calculate the x-coordinate of the intersection point
                double xi = (y - y1) * (x2 - x1) / (y2 - y1) + x1;

                // if the intersection point is to the right of the point, increment the crossing number
                if (xi > x) {
                    cn++;
                }
            }
        }

        // if the crossing number is odd, the point is inside the pentagon
        return (cn % 2 == 1);
    }


    public boolean areAllExtraActionUsed() {
        for(ExtraAction a : extraActions) {
            if(!a.isUsed() && a.isIniated()) return false;
        } return true;
    }
    
    public void resetExtraActions(){
        for(int i = 0; i < extraActions.size(); i++){
            extraActions.get(i).initiate();
            extraActions.get(i).reset();
        }
    }

    public ArrayList<ExtraAction> getExtraActions(){
        return extraActions;
    }

    public MandatorySettlementPhase getMandatorySettlementPhase(){
        return mandatorySettlementPhase;
    }

    public void setMandatorySettlementPhase(MandatorySettlementPhase p){
        mandatorySettlementPhase = p;
    }
    public void giveSettlement(Settlement temp) {
        settlements.add(temp);
    }
}
