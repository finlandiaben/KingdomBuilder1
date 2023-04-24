import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import javax.swing.*;

public class KingdomBuilderPanel extends JPanel implements MouseListener{

    private Gamestate gameState;
    private BufferedImage background;
    private int objectiveCardSpacing;

    //testing variables
    private Board b;
    private Player p;

    //here because apparently paint paints before construction which is interesting.
    private boolean hasConstructed;

    public KingdomBuilderPanel() {
         try{
           background = ImageIO.read(KingdomBuilderPanel.class.getResource("/Images/background.png"));
           gameState = new Gamestate();
           addMouseListener(this);
           hasConstructed = true;

        } catch(Exception e){
            System.out.println(e);
        }
    }

    public void paint(Graphics g) {
        g.clearRect(0, 0, getWidth(), getHeight()); //so panel doesn't get fucked up
        g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        if(hasConstructed){
        	gameState.draw(g);
        }
    }

    public void drawObjectiveCards(Graphics g) {

    }

    public void drawScoring(Graphics g) {

    }

    public void drawDeck(Graphics g) {

    }

    public void mouseClicked(MouseEvent e) {
        gameState.playBasedOnState(e.getX(), e.getY());
        repaint();

    }

    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e){}

    public void mouseExited(MouseEvent e) {}

}
