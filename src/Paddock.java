import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;


public class Paddock implements ExtraAction{

    private static final boolean doesItMove = true;
    private static final String extraActionType = "paddock";
    private int x;
    private int y;
    private boolean isUsed;
    private boolean isIniated;
    private BufferedImage image;

    public Paddock(int x, int y) {
        try {
            this.x = x;
            this.y = y;
            isUsed = false;
            isIniated = false;
            image = ImageIO.read(Paddock.class.getResource("/Images/KB-paddock.png"));
        }
        catch (Exception e) {
            System.out.println(e);
        }

    }
    public void setAvailableMoves(Board board, Player player, Hex hex){
        isUsed = true;

        for (int j = 0; j < 6; j++) {
            if(hex.getNeighbors().get(j) != null && hex.getNeighbors().get(j).getNeighbors().get(j) != null){
                switch (hex.getNeighbors().get(j).getNeighbors().get(j).getType()) {
                    case "desert", "flower", "meadow", "forest", "canyon" -> {
                        hex.getNeighbors().get(j).getNeighbors().get(j).highlight();
                    }
                    default -> {
                        //lol
                    }
                }
            }
        }

        Settlement temp = hex.getSettlement();
        player.giveSettlement(temp);
        hex.setSettlement(null);
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
        return new Paddock(-1, -1);
    }
}
