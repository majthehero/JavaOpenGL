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
    private boolean in_jump = false;
    private double time_in_jump;

    private List<double[]> points;

    private double[] color;


    StaticJumper() {
        position = new double[]{0.0, 0.0};
        speed = new double[]{0.0, 0.0};
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

//            System.out.println("Point x: " + point[0] + " y: " + point[1]);
//            System.out.println("Position: x: " + position[0] + " y: " + position[1]);

            glVertex2d(position[0] + point[0],
                        position[1] + point[1]);
        }

        glEnd();

    }



    double size = 0.02d;
    double momentum = 0.0d;


    void update(double dT, Game context) {

        // handle input

        if (glfwGetKey(context.window, GLFW_KEY_W) == GLFW_PRESS) {
            // jump
            if (! in_jump) {
                speed[1] = 2.0;
                in_jump = true;
            }
            // double jump
            else if (0.4 < time_in_jump && time_in_jump < 3.0) {
                speed[1] = 2.0;
                time_in_jump = 3.1; // only once
            }
        }

//        if (glfwGetKey(context.window, GLFW_KEY_A) == GLFW_PRESS) {
//            // add horizontal speed up to a limit
//            if (speed[0] < 0.2) {
//                speed[0] += 0.04 * dT;
//            } else {
//                speed[0] = 0.2;
//            }
//        }

//        if (glfwGetKey(context.window, GLFW_KEY_D) == GLFW_PRESS) {
//            // remove horizontal speed up to a limit
//            if (speed[0] > -0.2) {
//                speed[0] -= 0.04;
//            } else {
//                speed[0] = -0.2;
//            }
//        }

        if (glfwGetKey(context.window, GLFW_KEY_S) == GLFW_PRESS) {
            // stop entirely
            if (!in_jump) {
                speed[0] = 0;
            }
        }

        time_in_jump += dT;

        // gravity
        speed[1] -= 0.1;

        // update position
        position[0] += speed[0] * dT;
        position[1] += speed[1] * dT;


        // collide

        double collision_radius = 0.1;
        double my_curr_x = position[0];

        double[] temp = new double[]{0.0, 0.0};
        double[] prev_point = new double[]{0.0, 0.0};
        double[] next_point = new double[]{0.0, 0.0};
        for (double[] point : context.floor.upperPoints) {
            if (point[0] > my_curr_x) {
                prev_point = temp;
                next_point = point;
                break;
            }
            temp = point;
        }

        double scale_coef = 1.0 / (next_point[0] - prev_point[0]);
        double my_interp_coef = (my_curr_x - prev_point[0]) * scale_coef;

        double curr_y = (prev_point[1] * (1.0 - my_interp_coef) +
                            next_point[1] * my_interp_coef);

        // am undergorund?
        if (position[1] - collision_radius < curr_y) {
            position[1] = curr_y + collision_radius;
            in_jump = false;
            time_in_jump = 0.0d;
        }


    }



}
