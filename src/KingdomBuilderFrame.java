import java.awt.*;
import javax.swing.*;

public class KingdomBuilderFrame extends JFrame{

    private static final int WIDTH  = 1200;
    private static final int HEIGHT = 800;

    public KingdomBuilderFrame(String frameName) {

        super(frameName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(WIDTH, HEIGHT);
        setResizable(false);
        add(new KingdomBuilderPanel());
        setVisible(true);

    }

}
