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

class StaticJumper {

    private double[] position;

    private double[] speed;

    private List<double[]> points;

    private double[] color;

    StaticJumper() {
        position = new double[]{0.0, 0.0};
        points = new ArrayList<>(4);
    }

    void setColor(double[] color) {
        this.color = color;
    }

    void setPosition(double[] position) {
        this.position = position;
    }

    void init() {
        // make plazer a square shape
        double[] point1 = new double[]{-0.025, 0.04};
        double[] point2 = new double[]{0.025, 0.04};
        double[] point3 = new double[]{0.025, -0.04};
        double[] point4 = new double[]{-0.025, -0.04};

        points.add(point1);
        points.add(point2);
        points.add(point3);
        points.add(point4);

    }


    void render() {

        glBegin(GL_QUADS);
        glColor3dv(color);
        for (double[] point : points) {

            System.out.println("Point x: " + point[0] + " y: " + point[1]);
            System.out.println("Position: x: " + position[0] + " y: " + position[1]);

            glVertex2d(position[0] + point[0],
                        position[1] + point[1]);
        }

        glEnd();

    }



    double size = 0.02d;
    double momentum = 0.0d;


    void update(double dT, Game context) {

        // collision
//        double[]

        if (glfwGetKey(context.window, GLFW_KEY_W) == GLFW_PRESS) {

            // jump
            System.out.println("KKKK");

        }

    }



}
