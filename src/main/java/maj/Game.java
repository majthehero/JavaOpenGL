package maj;

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


public class Game {


    // global state

    long window;

    // game objects

    private ScrollingBackground background_back;
    private ScrollingBackground background_mid;
    private ScrollingBackground background_front;

    ScrollingFloor floor;

    RandomTree tree;

    private StaticJumper player;


    private void run() {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init_opengl();
        init_game();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(window);
        glfwDestroyWindow(window);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }



    private void init_opengl() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if ( !glfwInit() )
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(800, 480, "Scrolling!", NULL, NULL);
        if ( window == NULL )
            throw new RuntimeException("Failed to create the GLFW window");

        // Setup a key callback. It will be called every time a key is pressed, repeated or released.
        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if ( key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE )
                glfwSetWindowShouldClose(window, true); // We will detect this in the rendering loop
        });

        // Get the thread stack and push a new frame
        try ( MemoryStack stack = stackPush() ) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);
        // Enable v-sync
        glfwSwapInterval(1);

        // Make the window visible
        glfwShowWindow(window);
    }



    private void init_game() {

        // background
        double[] frontColor = new double[]{0.25, 0.4, 0.15};
        double[] midColor = new double[]{0.15, 0.35, 0.25};
        double[] backColor = new double[]{0.1, 0.3, 0.3};

        background_back = new ScrollingBackground(100);
        background_back.setColor(backColor);
        background_back.setScrollSpeed(0.7);
        background_back.init();
        
        background_mid = new ScrollingBackground(100);
        background_mid.setColor(midColor);
        background_mid.setScrollSpeed(1.0);
        background_mid.init();
        
        background_front = new ScrollingBackground(100);
        background_front.setColor(frontColor);
        background_front.setScrollSpeed(1.3);
        background_front.init();

        // floor
        double[] floorColor = new double[]{0.4, 0.6, 0.2};
        floor = new ScrollingFloor(100);
        floor.setColor(floorColor);
        floor.init();

        // tree
        double[] treeColor = new double[]{1.0, 0.4, 0.1};
        tree = new RandomTree(1.2);
        tree.setPosition(new double[]{0.0, -0.8});
        tree.setHeight(20);
        tree.setColor(treeColor);
        tree.generate();

        // player
        double[] playerColor = new double[]{0.7, 0.1, 0.6};
        player = new StaticJumper();
        player.setColor(playerColor);
        player.setPosition(new double[]{-0.4, 0.0});
        player.init();

    }


    private void loop() {


        long time_previous = System.nanoTime();


        /*
        This line is critical for LWJGL's interoperation with GLFW's
        OpenGL context, or any context that is managed externally.
        LWJGL detects the context that is current in the current thread,
        creates the GLCapabilities instance and makes the OpenGL
        bindings available for use.
        */

        GL.createCapabilities();

        // Set the clear color
        glClearColor(0.0f, 0.2f, 0.3f, 1.0f);

        // Run the rendering loop until the user has attempted to close
        // the window or has pressed the ESCAPE key.
        while ( !glfwWindowShouldClose(window) ) {

            long time_loop_start = System.nanoTime();
            long deltaT = time_loop_start - time_previous;
            double dT = (double) deltaT /  1000000000.0d;

            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT); // clear the framebuffer


            //  render
            background_back.render();
            background_mid.render();
            background_front.render();

            floor.render();

            tree.render();

            player.render();

            // update
            background_back.update(dT);
            background_mid.update(dT);
            background_front.update(dT);

            floor.update(dT);

            tree.update(dT);

            player.update(dT, this);


            glfwSwapBuffers(window); // swap the color buffers

            // Poll for window events. The key callback above will only be
            // invoked during this call.
            glfwPollEvents();

            // measure time again
            time_previous = time_loop_start;

        }
    }


    public static void main(String[] args)
    {

        System.out.println("Hello, dev.");

        new Game().run();

        System.exit(0);

    }

}
