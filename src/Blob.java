import org.w3c.dom.css.Rect;

import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Blob {

    //VARIABLE DECLARATION SECTION
    public String name;
    public int xpos;
    public int ypos;
    public int dx;
    public int dy;
    public int width;
    public int height;
    public Image pic;
    public boolean isAlive = false;
    public boolean killedDino = false;
    public Rectangle rec;
    public Ellipse2D ell;

    //METHOD DECLARATION SECTION
    public Blob(int pXpos, int pYpos, int pdx, int pdy, int pWidth, int pHeight) {
        xpos = pXpos;
        ypos = pYpos;
        dx = pdx;
        dy = pdy;
        width = pWidth;
        height = pHeight;

        isAlive = false;

        rec = new Rectangle(xpos, ypos, width, height); // change this to ellipse eventually
        ell = new Ellipse2D.Double(xpos, ypos, width, height);

    } // constructor

    public void move() {
        xpos = xpos + dx;
        ypos = ypos + dy;

        if (xpos + width < 0 || xpos > 900 || ypos + height < 0 || ypos > 700 && killedDino == false) {
            isAlive = false;
        }

        rec = new Rectangle(xpos, ypos, (int)(width*.8), (int)(height*.8));

        rec = new Rectangle((int)(xpos + width*.2), (int)(ypos + height*.2),
                (int)(width*.6), (int)(height*.5));
        ell = new Ellipse2D.Double(xpos, ypos + height*.2,
                width, height - height*.2);

    } // move
}






