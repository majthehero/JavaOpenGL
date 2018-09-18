package maj;

import java.util.ArrayList;
import java.util.Random;

import static org.lwjgl.opengl.GL11.*;

class RandomTree {

    private Random rng;

    private int height = 15;

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
        int max_heigth = height;
        double x0 = position[0];
        double y0 = position[1];

        // make tree trunk
        double y = y0;
        for ( ; y<max_heigth; y++) {

            int width = (int)((max_heigth - y) / 10);
            if (width < 2) width = 2;

            double x = x0;
            for (int i=0; i<x; i++) {
                blocks.add(new double[]{(x + i)/10.0, y/10.0 - 1.0});
            }
        }

        // make tree branches
        y = y0;
        int k = 2;
        int num_branches = (int)( Util.log2((int)(max_heigth/3)));

        for (int i=0; i<num_branches; i++) {

            y += (int)(max_heigth / k);
            k *= 2;

            double branch_x = x0;
            double branch_y = (int)( y + (rng.nextDouble() * (y/4)) - (y/8));

            int max_length = (int)(max_heigth - branch_y + rng.nextDouble() * (y/4) - (y/8));

            generateBranch(branch_x, branch_y, max_length);
        }
    }

    private void generateBranch(double x0, double y0, int max_length) {

        int steps = 0;
        int max_steps = (int)(Util.log2(max_length) / 3);

        double x = x0;
        double y = y0;
        for (int i=0; i<max_length; i++) {

            if (max_length * 0.2 < max_length - i &&
                max_length - i < max_length * 0.8 &&
                steps < max_steps)
            {
                if (rng.nextDouble() < (max_length / (steps + 1))) {
                    y++;
                    steps++;
                }
            }
            else {
                x += 1;
            }

            blocks.add(new double[]{x/10.0, y/10.0 - 1.0});
        }
    }


    void update(double dT) {

        for (double[] block : blocks) {
//            block[0] -= dT * 0.2 * scrollSpeed;
        }
    }

    void render() {

        System.out.println("##### FRAME ################################");

        glColor3dv(color);
        // render each block
        for (double[] block : blocks) {

            System.out.println("Block: " + block[0] + " : " + block[1]);

            double x1 = block[0] - 0.01;
            double x2 = block[0] + 0.01;
            double y1 = block[1] - 0.01;
            double y2 = block[1] + 0.01;

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





}
