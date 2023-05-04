import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

public class Settlement {

    private int owner;
    private BufferedImage image;
    private static final int imageSizeX = 30;
    private static final int imageSizeY = 30;

    public Settlement(int o){
        try{
            owner = o;

            if(owner == 0){
                image = ImageIO.read(Settlement.class.getResource("/Images/KB-Player1Settlement.png"));
            } else if(owner == 1){
                image = ImageIO.read(Settlement.class.getResource("/Images/KB-Player2Settlement.png"));
            } else if(owner == 2){
                image = ImageIO.read(Settlement.class.getResource("/Images/KB-Player3Settlement.png"));
            } else if(owner == 3){
                image = ImageIO.read(Settlement.class.getResource("/Images/KB-Player4Settlement.png"));
            }

        }catch(Exception e){
            System.out.println(e);
        }
    }

    public int getOwnerId(){
        return owner;
    }

    public void draw(Graphics g, int x, int y){
        g.drawImage(image, x, y, imageSizeX, imageSizeY, null);
    }

}
