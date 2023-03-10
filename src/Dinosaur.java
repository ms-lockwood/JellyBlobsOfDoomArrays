import java.awt.*;

public class Dinosaur {

    public String name;
    public double xpos;
    public double ypos;
    public double dx;
    public double dy;
    public int width;
    public int height;
    public boolean isAlive;
    public int points = 0;
    public Rectangle rec;


    public Dinosaur(int pXpos, int pYpos) {
        xpos = pXpos;
        ypos = pYpos;
        dx = 0;
        dy = 0;
        width = 20;
        height = (int)(width*.7);
        isAlive = true;
        points = 0;

        rec = new Rectangle((int)xpos, (int)ypos, width, height);

    } // constructor


    public void move() {
        if (isAlive) {
            xpos = xpos + dx;
            ypos = ypos + dy;

            if (xpos < 0 && dx < 0) {
                xpos = 900 + width;
            }
            if (xpos > 900 && dx > 0) {
                xpos = 0 - width;
            }

            if (ypos < 0 && dy < 0) {
                ypos = 700 + height;
            }
            if (ypos > 700 && dy > 0) {
                ypos = 0 - height;
            }
        } else if (xpos < 0 || xpos > 900 || ypos < 0 || ypos > 700) {
            // game over
            xpos = xpos + dx;
            ypos = ypos + dy;
        }

        rec = new Rectangle((int)xpos, (int)ypos, width, height);

    } // move

    public void deadMove() {

    }

}
