package com.pixelgriffin.grid.gpgs;

import java.util.ArrayList;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.multiplayer.Participant;
import com.pixelgriffin.grid.android.AndroidLauncher;

/**
 * 
 * @author Nathan
 *
 */
public interface GoogleAccountAdapter {
	public void signIn();
	public void signOut();
	public boolean isSignedIn();
	public String getMyID();
	
	public void startPickGame();
	public void startQuickGame();
	public void openInvitationScreen();
	
	public GoogleApiClient getAPI();
	public String getActiveRoomID();
	public void nullifyRoomID();
	public AndroidLauncher getInstance();
	public ArrayList<Participant> getParticipants();
}