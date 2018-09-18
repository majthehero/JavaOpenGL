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
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.abs;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

import static org.lwjgl.opengl.GL11.*;

class RandomTree {

    private Random rng;

    private int thickness = 1;
    private int height = 15;
    private static final int max_heigth = 60;

    private double scrollSpeed;

    double[] position;

    private ArrayList<double[]> blocks;
    private double[] color;

    RandomTree(double scrollSpeed) {
        this.scrollSpeed = scrollSpeed;
        position = new double[2];
        blocks = new ArrayList<>();
        rng = new Random();
    }

    void generate() {

        // explore a random tree structure and put blocks

        double[] begin = position.clone();

        for (int i=0; i < height; i++) {

            double curr_x = begin[0];
            double curr_y = begin[1] + 0.1 * i;

            System.out.println("Tree block: " + curr_x + " : " + curr_y);
            System.out.println("Tree block: " + i);


            // add block
            blocks.add(new double[]{curr_x, curr_y});

            double r = rng.nextDouble();
            if (r < 0.15) {
                // generate left brach
                generate_branch(i, 0, -1, curr_x, curr_y);
            } else if (r < 0.3) {
                generate_branch(i, 0, 1, curr_x, curr_y);
            }

        }

    }

    private void generate_branch(int curr_h, int curr_w,
                                 int dir,
                                 double begin_x, double begin_y) {

        if (curr_h > height) return;

        double curr_x = begin_x;
        double curr_y = begin_y;

//        System.out.println("Starting branch from: " + curr_x + " : " + curr_y);

        for (int i=0; i + curr_w < (height - curr_h) / 1.6; i++) { // ! TODO ABSOLUTE VALUE CHECK

            double r = rng.nextDouble();

            // normal straigth branch
            if (r < 0.8) {
                curr_x += dir * 0.03;
            }

            // move up
            else if (r < 0.9) {
                curr_y += 0.1;
            }

            // branch
            else if (r < 0.95) {
                // this branch down
                curr_y -= 0.1;
                generate_branch(curr_h++, curr_w++, dir, curr_x, curr_y + 0.1);
            }

            // end
            else {
                return;
            }

            blocks.add(new double[]{curr_x, curr_y});

        }

    }


    void update(double dT) {

        for (double[] block : blocks) {
//            block[0] -= dT * 0.2 * scrollSpeed;
        }
    }

    void render() {

        glColor3dv(color);
        // render each block
        for (double[] block : blocks) {
            double x1 = block[0] - 0.025;
            double x2 = block[0] + 0.025;
            double y1 = block[1] - 0.04;
            double y2 = block[1] + 0.04;

            glBegin(GL_QUADS);

            glVertex2d(x1, y1);
            glVertex2d(x2, y1);
            glVertex2d(x2, y2);
            glVertex2d(x1, y2);

            glEnd();
        }
    }

    void setColor(double[] color) {
        this.color = color;
    }

    void setPosition(double[] position) {
        this.position = position;
    }

    void setHeight(int height) {
        this.height = height;
    }

    void setThickness(int thickness) {
        this.thickness = thickness;
    }




}
