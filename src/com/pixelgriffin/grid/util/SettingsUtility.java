package com.pixelgriffin.grid.util;

import java.io.IOException;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * 
 * @author Nathan
 *
 */
public class SettingsUtility {
	public static boolean musicOn;
	
	public static void load() {
		FileHandle f = Gdx.files.external("Grid/settings.dat");
		if(!f.exists()) {
			try {
				f.file().createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			musicOn = true;
			return;
		}
		
		String data = f.readString();
		
		musicOn = Boolean.valueOf(data).booleanValue();
	}
	
	public static void save() {
		FileHandle f = Gdx.files.external("Grid/settings.dat");
		f.writeString(Boolean.valueOf(musicOn).toString(), false);
	}
}
