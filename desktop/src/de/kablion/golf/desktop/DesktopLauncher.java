package de.kablion.golf.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import de.kablion.golf.Application;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Golf Without Friends";
		config.width = 480;
		config.height = 800;
        config.samples = 8;
        new LwjglApplication(new Application(), config);
	}
}
