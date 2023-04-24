import java.awt.Graphics;

public interface ExtraAction{

    static final int sizeX = 30;
    static final int sizeY = 39;

    void draw(Graphics g);
    void draw(Graphics g, int coordX, int coordY);
    void initiate();
    boolean isIniated();
    boolean isUsed();
    String getExtraActionType();
    boolean doesItMove();
    void reset();
    boolean isClicked(int mouseX, int mouseY);
    ExtraAction copy();
    void setAvailableMoves(Board b, Player p, Hex h);
    boolean canSetAvailableMoves(Board b, Player p, Hex h);
    void setX(int x);
    void setY(int y);

}
