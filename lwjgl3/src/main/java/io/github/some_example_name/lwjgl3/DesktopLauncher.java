package io.github.some_example_name.lwjgl3;


import GameMain.Main;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import utils.GameConstants;

public class DesktopLauncher {
    public static void main(String[] args) {
        Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
        
        // Set working directory  to assets folder
        config.setWindowedMode(
            (int)GameConstants.VIEWPORT_WIDTH, 
            (int)GameConstants.VIEWPORT_HEIGHT
        );
        
        // Important: Set the default window title
        config.setTitle("Ohio");
        
        // Set the working directory to the assets folder
        System.setProperty("org.lwjgl.workingdir", "C:\\Users\\UmarPC\\ICSGame\\assets");
        
        new Lwjgl3Application(new Main(), config);
    }
}