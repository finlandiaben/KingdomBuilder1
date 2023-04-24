import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class Tavern implements ExtraAction{

    private static final boolean doesItMove = false;
    private static final String extraActionType = "tavern";

    private int x;
    private int y;
    private boolean isUsed;
    private boolean isIniated;
    private BufferedImage image;

    public Tavern(int x, int y) {
        try {
            this.x = x;
            this.y = y;
            isUsed = false;
            isIniated = false;
            image = ImageIO.read(Tavern.class.getResource("/Images/KB-tavern.png"));
        }
        catch (Exception e) {
            System.out.println(e);
        }

    }
    
    public void setAvailableMoves(Board board, Player player, Hex hex){
                //probably not the best way to do this 
        for(Hex h: board.getAllHexes()){
             if(h.getSettlement() == null){//looks at all the free hexes
              for(Hex i: h.getNeighbors()){
                if(i.getSettlement() != null){ // starts the 3 settlement chain 
                  for(Hex j: i.getNeighbors()){
                    if(j.getSettlement() != null){ // checks for the second settlement
                        for(Hex k : j.getNeighbors()){
                         if(k.getSettlement() != null){ //checks for a third connected settlement
                            if(h.isHighlighted()== false){
                                h.highlight();
                            }
                         }
                        }
                    }
                  }
                }
              }
                 
             }  
             
          }
        isUsed = true;

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
        return new Tavern(-1, -1);
    }
}
