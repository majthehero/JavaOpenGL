package maj;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class ScrollingBackground {

    int horResolution;
    List<double[]> upperPoints;
    Random rng;

    public ScrollingBackground(int horResolution) {

        this.horResolution = horResolution;
        upperPoints = new ArrayList<>(this.horResolution);
        rng = new Random(123);

    }

    public void init() {

        // init arrays mem
        for (int i=0; i<horResolution; i++) {
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
        glColor3d(0.3, 0.6, 0.5);

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

        // move all points: 1sec -> 10% of screen -> dX = 0.2
        for (double[] point : upperPoints) {
            point[0] -= 0.2 * dT;
        }

        // remove first point if second if off screen, add one the other side
        if (upperPoints.get(1)[0] < -1.0d) {

            upperPoints.remove(0);

            double[] temp = new double[2];
            temp[0] = 1.2;
            temp[1] = rng.nextGaussian() * 0.3
                + upperPoints.get(upperPoints.size() - 1)[1];

            upperPoints.add(temp);
        }

    }


}