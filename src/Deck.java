import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import javax.swing.*;

import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.Stack;
import javax.imageio.ImageIO;

public class Deck {

    private Stack<TerrainCard> deck;
    private Stack<TerrainCard> discarded;
    private static final int deckX = 975 - 86;
    private static final int deckY = 250;
    private static final int deckSpacingX = 20;
    private BufferedImage cardBack;

    public Deck(){
        try{

            cardBack = ImageIO.read(Deck.class.getResource("/Images/KB-Card-Back.png"));
            deck = new Stack<TerrainCard>();
            discarded = new Stack<TerrainCard>();

            for(int i = 0; i < 5; i++){
                deck.push(new TerrainCard("meadow"));
                deck.push(new TerrainCard("flower"));
                deck.push(new TerrainCard("forest"));
                deck.push(new TerrainCard("canyon"));
                deck.push(new TerrainCard("desert"));
            }

            Collections.shuffle(deck);

        }catch(Exception e){
            System.out.println(e);
        }
    }
    public TerrainCard drawCard(){
        TerrainCard card = deck.pop();
        if(deck.size() == 0){
            refill();
        }
        return card;
    }

    public void discardCard(TerrainCard card){
        discarded.push(card);
    }

    public void draw(Graphics g){
        g.drawImage(cardBack, deckX, deckY, TerrainCard.getSizeX(), TerrainCard.getSizeY(), null);
        if(discarded.size() > 0){
        	//is this not working?
            discarded.peek().draw(g, deckX + 76 + deckSpacingX, deckY);
        }
    }

    private void refill(){
        for(int i = 0; i < discarded.size(); i++){
            deck.push(discarded.pop());
        }

        Collections.shuffle(deck);
    }
}
