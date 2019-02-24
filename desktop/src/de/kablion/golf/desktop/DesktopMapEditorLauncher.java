package de.kablion.golf.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;

import de.kablion.golf.editor.Application;

public class DesktopMapEditorLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Golf Without Friends - Map Editor";
		config.width = 1280;
		config.height = 720;
        config.samples = 8;
        new LwjglApplication(new Application(), config);
	}
}
