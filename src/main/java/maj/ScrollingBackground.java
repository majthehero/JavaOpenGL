package maj;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import static org.lwjgl.opengl.GL11.*;

public class ScrollingBackground {

    int horResolution;
    int numPoints;
    List<double[]> upperPoints;
    Random rng;

    public double[] color;
    public double scrollSpeed;

    public ScrollingBackground(int horResolution) {

        this.horResolution = horResolution;
        this.numPoints = horResolution + 10;
        upperPoints = new ArrayList<>(this.numPoints);
        rng = new Random();
        color = new double[]{0.3, 0.6, 0.5};
        scrollSpeed = 1.0;

    }

    public void setScrollSpeed(double scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    public void setColor(double[] color) {
        this.color = color;
    }

    public void init() {

        // init arrays mem
        for (int i=0; i<numPoints; i++) {
            upperPoints.add(new double[2]);
        }

        // make it into terrain
        double x = -1;
        for (double[] point : upperPoints) {

            point[0] = x;
            x += 2.0 / horResolution; // openGL screen coords are (-1, 1)

            double r = rng.nextGaussian();
            point[1] = r;

        }
    }

    public void render() {

        glBegin(GL_TRIANGLE_STRIP);
        glColor3dv(color);

        int i = 0;
        for (double[] point : upperPoints) {

            // DBG: print
//            System.out.println("Upper point " + i + " x: " + point[0] + " y: " + point[1]);

            // upper point from array
            glVertex2dv(point);

            // lower point fixed
            glVertex2d(point[0], -1.1); // just off-screen

            i++;
        }

        glEnd();

    }


    public void update(double dT) {

//        System.out.println("Update. dT: " + dT);

        // remove first point if second if off screen, add one the other side
        if (upperPoints.get(1)[0] < -1.0d) {

            upperPoints.remove(0);

            double[] temp = new double[2];
            temp[0] = 1 + 2.0 / horResolution;

            double y = upperPoints.get(upperPoints.size() - 1)[1];

            temp[1] = nextY(y);

            upperPoints.add(temp);
        }

        // move all points: 1sec -> 10% of screen -> dX = 0.2
        for (double[] point : upperPoints) {
            point[0] -= 0.2 * dT * scrollSpeed;
        }

    }

    private double nextY(double prevY) {

        double nextY = 0.0;
        double r;

        if (prevY > 0.8) { // make next point lower
            r = rng.nextDouble() * 0.15;
            nextY = prevY - r;
        }

        else if (prevY < -0.8) { // make next point higher
            r = rng.nextDouble() * 0.05;
            nextY = prevY + r;
        }

        else {

            r = rng.nextGaussian() * 0.1;

            if (prevY < -0.4) {
                r += rng.nextDouble() * 0.03;
            }
            if (prevY > 0.4) {
                r += rng.nextDouble() * -0.03;
            }

            nextY = prevY + r;
        }



        return nextY;
    }

}