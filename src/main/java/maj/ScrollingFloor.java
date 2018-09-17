package maj;

import org.lwjgl.Version;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;



public class ScrollingFloor {

    private int horResolution;
    private int numPoints;
    List<double[]> upperPoints;
    private Random rng;

    private double[] color;
    private double scrollSpeed;

    // self generation info
    private int current_chasm_length = 0;
    private final int max_chasm_length = 30;
    private final int min_chasm_length = 10;

    private int wall_height = 3;
    private int current_wall_length = 0;
    private final int min_wall_length = 2;
    private final int max_wall_length = 10;

    private double preFeatureY = 0.0;


    ScrollingFloor(int horResolution) {

        this.horResolution = horResolution;
        this.numPoints = horResolution + 10;
        upperPoints = new ArrayList<>(this.numPoints);
        rng = new Random();
        color = new double[]{0.3, 0.6, 0.5};
        scrollSpeed = 1.2;

    }

    public void setScrollSpeed(double scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
    }

    void setColor(double[] color) {
        this.color = color;
    }

    void init() {

        // init arrays mem
        for (int i=0; i<numPoints; i++) {
            upperPoints.add(new double[2]);
        }

        // make it into terrain
        double x = -1;
        for (double[] point : upperPoints) {

            point[0] = x;
            x += 2.0 / horResolution; // openGL screen coords are (-1, 1)

            point[1] = -0.2;

        }
    }

    void render() {

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

    void update(double dT) {

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

        double nextY;
        double r = rng.nextDouble();

        // currently generating chasm
        if (current_chasm_length > 0) {
            // continue chasm
            if (current_chasm_length < min_chasm_length) {
                nextY = -1.1;
            }
            // stop chasm
            else if (current_chasm_length > max_chasm_length) {
                nextY = preFeatureY;
                current_chasm_length = -1;
            }
            // maaaybe
            else {
                // continue chasm
                if (r < 0.6) {
                    nextY = -1.1;
                }
                // stop chasm
                else {
                    nextY = preFeatureY;
                    current_chasm_length = -1;
                }
            }
            current_chasm_length++;
        }

        // currently generating wall
        else if (current_wall_length > 0) {
            // continue wall
            if (current_wall_length < min_wall_length) {
                nextY = preFeatureY + 0.1 * wall_height;
            }
            // stop wall
            else if (current_wall_length > max_wall_length) {
                nextY = preFeatureY;
                current_wall_length = -1;
            }
            // maaaybe
            else {
                // continue wall
                if (r < 0.2) {
                    nextY = preFeatureY + 0.1 * wall_height;
                }
                // stop wall
                else {
                    nextY = preFeatureY;
                    current_wall_length = -1;
                }
            }
            current_wall_length++;
        }

        // else
        else if (r < 0.02) { // start chasm
            preFeatureY = prevY;
            nextY = -1.1;
            current_chasm_length = 1;
        }

        else if (r < 0.04) { // start wall
            preFeatureY = prevY;
            nextY = prevY + 0.1 * wall_height;
            current_wall_length = 1;
        }

        // make generic walking path
        else {

            if (prevY > 0.3) {
                nextY = prevY - abs(r / 30.0);
            }
            else if (prevY < -0.5) {
                nextY = prevY + abs(r / 30.0);
            }
            else {
                nextY = prevY + (r - 0.5) / 50.0;
            }

            current_wall_length = 0;
            current_chasm_length = 0;
        }


        return nextY;

    }


}
