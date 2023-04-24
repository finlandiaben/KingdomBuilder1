import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class TerrainCard {
    private String terrainType;
    private BufferedImage cardFace;
    private static final int sizeX = 76;
    private static final int sizeY = 100;

    public TerrainCard(String terrainType){
        try{

            this.terrainType = terrainType;

            if(terrainType.equals("meadow")){
                cardFace = ImageIO.read(TerrainCard.class.getResource("/Images/KB-Card-Meadow.png"));
            } else if(terrainType.equals("flower")){
                cardFace = ImageIO.read(TerrainCard.class.getResource("/Images/KB-Card-Flower.png"));
            } else if(terrainType.equals("forest")){
                cardFace = ImageIO.read(TerrainCard.class.getResource("/Images/KB-Card-Forest.png"));
            } else if(terrainType.equals("canyon")){
                cardFace = ImageIO.read(TerrainCard.class.getResource("/Images/KB-Card-Canyon.png"));
            } else if(terrainType.equals("desert")){
                cardFace = ImageIO.read(TerrainCard.class.getResource("/Images/KB-Card-Desert.png"));
            }

        } catch(Exception e){
            System.out.println(e);
        }
    }

    public void draw(Graphics g, int x, int y){
        g.drawImage(cardFace, x, y, sizeX, sizeY, null);
    }

    public String getTerrainType(){
        return terrainType;
    }

    public static int getSizeX(){
        return sizeX;
    }

    public static int getSizeY(){
        return sizeY;
    }
}
