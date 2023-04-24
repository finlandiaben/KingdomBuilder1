import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class Oasis implements ExtraAction{

    private static final boolean doesItMove = false;
    private static final String extraActionType = "oasis";

    private int x;
    private int y;
    private boolean isUsed;
    private boolean isIniated;
    private BufferedImage image;

    public Oasis(int x, int y) {
        try {
            this.x = x;
            this.y = y;
            isUsed = false;
            isIniated = false;
            image = ImageIO.read(Harbor.class.getResource("/Images/KB-oasis.png"));
        }
        catch (Exception e) {
            System.out.println(e);
        }

    }
    
    public void setAvailableMoves(Board board, Player player, Hex hex){
            boolean placeAnywhere = true;
            for(Hex h: board.getAllHexes()){
                if(h.getSettlement() != null && h.getSettlement().getOwnerId() == player.getId()){
                    for(int i = 0; i<h.getNeighbors().size(); i++){
                        if(h.getNeighbors().get(i) != null && h.getNeighbors().get(i).getType().equals("desert") && h.getNeighbors().get(i).getSettlement() == null){
                            h.getNeighbors().get(i).highlight();
                            placeAnywhere = false;
                        }
                            
                    }
                  
                }
            } 
        if(placeAnywhere){
            for(Hex h: board.getAllHexes()){
                if(h.getSettlement() == null && h.getType().equals("desert")){
                    h.highlight();
                }
            }
        }
        isUsed = true;
    }

    public boolean canSetAvailableMoves(Board board, Player player, Hex hex){
        return true;
    }


    public void draw(Graphics g) {
        g.drawImage(image, x, y, sizeX, sizeY, null);
    }

    public void draw(Graphics g, int coordX, int coordY){
        g.drawImage(image, coordX, coordY, sizeX, sizeY, null);
    }
    public void initiate() {
        isIniated = true;
    }
    public boolean isIniated(){return isIniated;}

    public boolean isClicked(int mouseX, int mouseY){
        return mouseX > x && mouseX < x + sizeX && mouseY > y && mouseY < y + sizeY;
    }

    public boolean isUsed() {
        return isUsed;
    }
    public void setUsed(Boolean b){isUsed = b;}

    public boolean doesItMove() {
        return doesItMove;
    }

    public String getExtraActionType(){
        return extraActionType;
    }

    public void reset() {
        isUsed = false;
    }

    public void setX(int x){
        this.x = x;
    }

    public void setY(int y){
        this.y = y;
    }

    public int getX(){
        return x;
    }

    public int getY(){
        return y;
    }


    public ExtraAction copy() {
        /*because the ExtraAction will be used for setting purposes
         making its coords messed up should do no harm
         */
        return new Oasis(-1, -1);
    }
}
