package com.pixelgriffin.grid.android;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.utils.GdxNativesLoader;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMultiplayer;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.example.games.basegameutils.GameHelper;
import com.google.example.games.basegameutils.GameHelper.GameHelperListener;
import com.pixelgriffin.grid.entity.GeneratorUnit;
import com.pixelgriffin.grid.entity.MinerUnit;
import com.pixelgriffin.grid.entity.ShieldUnit;
import com.pixelgriffin.grid.entity.TurretUnit;
import com.pixelgriffin.grid.entity.Unit;
import com.pixelgriffin.grid.gpgs.GoogleAccountAdapter;
import com.pixelgriffin.grid.logic.GameBoard;
import com.pixelgriffin.grid.screen.MainMenuScreen;
import com.pixelgriffin.grid.util.NetworkUtility;

/**
 * Main android entry point
 * 
 * Handles networking and starts the game
 * Things it implements are from Google Play Game Services (GPGS)
 * 
 * Also holds ads
 * 
 * @author Nathan
 *
 */
public class AndroidLauncher extends AndroidApplication implements GameHelperListener, GoogleAccountAdapter, RoomUpdateListener, RoomStatusUpdateListener, RealTimeMessageReceivedListener, OnInvitationReceivedListener, RealTimeMultiplayer.ReliableMessageSentCallback {
	
	//GPGS interface helper
	private GameHelper helper;
	
	//the actual game instance
	private GridGame instance;
	
	//Networking IDs
	private String roomID;//room ID
	private String myID;//our participant ID
	
	//other participants in the room
	//this should include us and one other person
	private ArrayList<Participant> participants;
	
	//ads
	private InterstitialAd interAd;
	
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);//super on create
		
		//keep the screen from going to sleep without input
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		//start game
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		this.instance = new GridGame(this);
		initialize(instance, config);
		
		//GPGS initialization
		helper = new GameHelper(this, GameHelper.CLIENT_GAMES);
		helper.setMaxAutoSignInAttempts(0);
		helper.enableDebugLog(false);
		
		helper.setup(this);
		
		//initialize network stuff
		participants = new ArrayList<Participant>();
		
		//nullify room ID
		this.roomID = null;
		
		//ads
		this.interAd = new InterstitialAd(this);
		this.interAd.setAdUnitId("Id");
		this.interAd.setAdListener(new AdListener() {
			@Override
			public void onAdLoaded() {
				//showAd();
			}
			
			@Override
			public void onAdOpened() {
				//showingAd = true;
			}
		});
	}
	
	/*public boolean showedAd() {
		return this.showingAd;
	}
	
	public void resetAdShown() {
		this.showingAd = false;
	}*/
	
	public void loadAd() {
		try {
			//try to run a new sign in thread
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					AdRequest req = new AdRequest.Builder().build();
					interAd.loadAd(req);
				}
				
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void showAd() {
		try {
			//try to run a new sign in thread
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if(interAd.isLoaded())
						interAd.show();
				}
				
			});
		} catch(Exception e) {
		}
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		
		//interface with GPGS
		helper.onStart(this);
	}
	
	@Override
	protected void onStop() {
		super.onStop();
		
		//interface with GPGS
		helper.onStop();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//required activity management
		super.onActivityResult(requestCode, resultCode, data);
		//GPGS continue connection if OK
		helper.onActivityResult(requestCode, resultCode, data);
		
		//handle other
		//do not continue if not OK
		if(resultCode != Activity.RESULT_OK) {
			return;
		}
		
		/*
		 * Handle intent responses
		 */
		switch(requestCode) {
		//select players intent (lobby game)
		case NetworkUtility.RC_SELECT_PLAYERS:
			handleResultSelectPlayers(data);
			break;
			
		//invitation screen
		case NetworkUtility.RC_INVITATION_INBOX:
			handleInvitationInbox(data);
			break;
			
		//waiting room intent
		case NetworkUtility.RC_WAITING_ROOM:
			handleWaitingRoom(resultCode, data);
			break;
		}
	}
	
	private void handleWaitingRoom(int _resp, Intent _data) {
		if(_resp == Activity.RESULT_CANCELED) {
			Games.RealTimeMultiplayer.leave(this.helper.getApiClient(), this, this.roomID);
			this.instance.toMenu(MainMenuScreen.INDEX_NETWORK, false);
		} else if(_resp == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
			Games.RealTimeMultiplayer.leave(this.helper.getApiClient(), this, this.roomID);
			this.instance.toMenu(MainMenuScreen.INDEX_NETWORK, false);
		}
	}
	
	//I have no idea what this does. I don't think it works
	//but it doesn't mess anything up so I'm leaving it
	private void handleInvitationInbox(Intent _data) {
		Bundle extra = _data.getExtras();
		
		Invitation inv = extra.getParcelable(Multiplayer.EXTRA_INVITATION);
		
		RoomConfig conf = makeRoomConfig()
				.setInvitationIdToAccept(inv.getInvitationId())
				.build();
		Games.RealTimeMultiplayer.join(this.helper.getApiClient(), conf);
	}
	
	//handle selecting players
	private void handleResultSelectPlayers(Intent _data) {
		Bundle extra = _data.getExtras();
		
		//get invited people
		final ArrayList<String> invited = _data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);
		
		//get auto-match criteria
		Bundle match = null;
		int minAuto = _data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
		int maxAuto = _data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);
		
		if(minAuto > 0) {
			match = RoomConfig.createAutoMatchCriteria(minAuto, maxAuto, 0);
		} else {
			match = null;
		}
		
		//build match criteria
		RoomConfig.Builder builder = makeRoomConfig();
		builder.addPlayersToInvite(invited);
		if(match != null) {
			builder.setAutoMatchCriteria(match);
		}
		
		//connect to our room
		RoomConfig conf = builder.build();
		Games.RealTimeMultiplayer.create(helper.getApiClient(), conf);
		
		//go to waiting screen
		this.instance.toNetworkWaiting();
	}

	/*
	 * ------------
	 * GPGS methods
	 * ------------
	 * 
	 */
	
	//basic room configuration build
	private RoomConfig.Builder makeRoomConfig() {
		return RoomConfig.builder(this)
				.setMessageReceivedListener(this)
				.setRoomStatusUpdateListener(this);
	}
	
	@Override
	public void signIn() {
		try {
			//try to run a new sign in thread
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					helper.beginUserInitiatedSignIn();
				}
				
			});
		} catch(Exception e) {
		}
	}

	@Override
	public void signOut() {
		try {
			//try to run a sign out intent
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					helper.signOut();
				}
				
			});
		} catch(Exception e) {
		}
	}

	@Override
	public boolean isSignedIn() {
		//is the player signed in to GPGS
		return helper.isSignedIn();
	}
	
	@Override
	public void openInvitationScreen() {
		//start intent
		Intent i = Games.Invitations.getInvitationInboxIntent(helper.getApiClient());
		startActivityForResult(i, NetworkUtility.RC_INVITATION_INBOX);
		
		//go to network waiting screen so the user knows the game is working on it
		this.instance.toNetworkWaiting();
	}

	@Override
	public void onSignInFailed() {
		//do nothing (for now)
	}

	@Override
	public void onSignInSucceeded() {
		//once we have signed in we should start checking if someone has
		//invited us to a game
		Games.Invitations.registerInvitationListener(this.helper.getApiClient(), this);
	}

	@Override
	public void startPickGame() {
		//start intent
		Intent i = Games.RealTimeMultiplayer.getSelectOpponentsIntent(helper.getApiClient(), 1, 1);
		startActivityForResult(i, NetworkUtility.RC_SELECT_PLAYERS);
	}
	
	@Override
	public void startQuickGame() {
		//create auto-match critera
		//since we don't really have any criteria the third
		//passed variable is 0
		Bundle am = RoomConfig.createAutoMatchCriteria(1, 1, 0);
		
		//build room
		RoomConfig.Builder builder = makeRoomConfig();
		builder.setAutoMatchCriteria(am);
		RoomConfig conf = builder.build();
		
		//connect to room
		Games.RealTimeMultiplayer.create(this.helper.getApiClient(), conf);
		
		//go to network screen
		this.instance.toNetworkWaiting();
	}

	@Override
	public GoogleApiClient getAPI() {
		//return API
		return this.helper.getApiClient();
	}

	@Override
	public String getActiveRoomID() {
		//return our room ID
		return this.roomID;
	}
	
	@Override
	public AndroidLauncher getInstance() {
		//return the instance of this
		return this;
	}
	
	@Override
	public String getMyID() {
		//return our participant ID
		return this.myID;
	}

	
	
	
	/*
	 * ----------------
	 * Message Networking
	 * ----------------
	 */

	@Override
	public void onRealTimeMessageReceived(RealTimeMessage msg) {
		//we must be in game in order to receive network messages
		if(!this.instance.isInGame())
			return;
		
		//gather data
		byte[] data = msg.getMessageData();
		
		//handle packets based on packet ID
		switch(data[0]) {
		case NetworkUtility.PACKET_BUILD:
			handleBuildPacket(data);
			break;
		
		case NetworkUtility.PACKET_DESTROY:
			handleDeathPacket(data);
			break;
		}
	}
	
	//when the other palyer built
	private void handleBuildPacket(byte[] _data) {
		//create byte buffer
		ByteBuffer position = ByteBuffer.wrap(_data, 2, _data.length - 2);
		
		Unit u = null;
		
		//switch based on unit type
		switch(_data[1]) {
		case 0:
			//create unit
			u = new GeneratorUnit(false);
			GameBoard.otherResource -= GeneratorUnit.VALUE;
			
			break;
		case 1:
			//create unit
			u = new MinerUnit(false);
			GameBoard.otherResource -= MinerUnit.VALUE;
			
			//add miners to miner count
			//this is so we can keep track later to tell if someone
			//won the game or not
			GridGame.getGame().getOpponent().ourMiners.add((MinerUnit) u);
			
			break;
		case 2:
			//create unit
			u = new ShieldUnit(false);
			GameBoard.otherResource -= ShieldUnit.VALUE;
			
			break;
		case 3:
			//create unit
			u = new TurretUnit(false);
			GameBoard.otherResource -= TurretUnit.VALUE;
			
			break;
		}
		
		try {
			//add created unit to the board at a flipped position
			//we flip the position since on the opponent's screen
			//he builds from our perspective
			GridGame.getGame().getBoard().addUnit(9 - position.getInt(), 5 - position.getInt(), u);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void handleDeathPacket(byte[] _data) {
		//crate byte buffer reader
		ByteBuffer bb = ByteBuffer.wrap(_data, 1, _data.length - 1);
		
		//gather positions from data
		int x = bb.getInt();
		int y = bb.getInt();
		
		try {
			//select unit
			Unit u = GridGame.getGame().getBoard().getUnit(9 - x, 5 - y);
			u.flagForDeath();//kill it
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/*private void handleHealthPacket(byte[] _data) {
		ByteBuffer bb = ByteBuffer.wrap(_data, 1, _data.length - 1);
		
		int x = bb.getInt();
		int y = bb.getInt();
		int health = bb.getInt();
		
		try {
			Unit u = GridGame.getGame().getBoard().getUnit(9 - x, 5 - y);
			u.overrideHealth(health);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/

	@Override
	public void onRealTimeMessageSent(int arg0, int arg1, String arg2) {
	}
	
	
	
	/*
	 * --------------
	 * P2P Networking
	 * --------------
	 * 
	 */
	
	@Override
	public void onConnectedToRoom(Room room) {
	}

	@Override
	public void onDisconnectedFromRoom(Room room) {
	}

	@Override
	public void onP2PConnected(String arg0) {
	}

	@Override
	public void onP2PDisconnected(String arg0) {
	}

	@Override
	public void onPeerDeclined(Room room, List<String> arg1) {
	}

	@Override
	public void onPeerInvitedToRoom(Room room, List<String> arg1) {
	}

	@Override
	public void onPeerJoined(Room room, List<String> arg1) {
	}

	@Override
	public void onPeerLeft(Room room, List<String> arg1) {
	}

	@Override
	public void onPeersConnected(Room room, List<String> peers) {
		//check if we should start the game
		if(shouldGameStart(room)) {
			//go to game screen
			this.instance.toGame(true, 0);
		}
	}

	@Override
	public void onPeersDisconnected(Room room, List<String> peers) {
		//is the game still playing
		if(GridGame.getInstance().isInGame()) {
			//leave the game
			//since it's 1 on 1 if someone leaves
			//then there is no opponent and the game must end
			Games.RealTimeMultiplayer.leave(this.helper.getApiClient(), this, this.roomID);
			//go back to menu @ the network index
			this.instance.toFinish("WON", true);
			//this.instance.toMenu(MainMenuScreen.INDEX_NETWORK);
		}
	}
	
	//check if the game should start
	private boolean shouldGameStart(Room _room) {
		int count = 0;
		for(Participant p : _room.getParticipants()) {
			if(p.isConnectedToRoom())
				count++;
		}
		
		//start if there are two people in the room.. basically
		return (count == 2);
	}

	@Override
	public void onRoomAutoMatching(Room room) {
	}

	@Override
	public void onRoomConnecting(Room room) {
	}

	@Override
	public void onJoinedRoom(int status, Room room) {
		//do not continue if there is an issue
		if(status != GamesStatusCodes.STATUS_OK)
			return;
		
		this.roomID = room.getRoomId();//gather our room ID
		this.myID = room.getParticipantId(Games.Players.getCurrentPlayerId(this.helper.getApiClient()));//gather our ID
		
		//start waiting room intent, so we can see the other person connect and shit
		Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(this.helper.getApiClient(), room, 2);
		startActivityForResult(i, NetworkUtility.RC_WAITING_ROOM);
		
		//go to main menu screen, just in case they exit the intent
		//this way they won't see "Waiting for network" again
		this.instance.toMenu(MainMenuScreen.INDEX_NETWORK, false);
	}

	@Override
	public void onLeftRoom(int arg0, String arg1) {
	}

	@Override
	public void onRoomConnected(int status, Room room) {
		//once everyone has connected and the room is finished
		//we should gather everyone's participant object
		this.participants = room.getParticipants();
	}

	@Override
	public void onRoomCreated(int status, Room room) {
		//if there is an error
		if(status != GamesStatusCodes.STATUS_OK) {
			//print and stop
			System.out.println("CREATE ROOM ERROR(" + status + ")");
			return;
		}
		
		//get our room ID
		this.roomID = room.getRoomId();
		//get our participant ID
		this.myID = room.getParticipantId(Games.Players.getCurrentPlayerId(this.helper.getApiClient()));
		
		//start intent
		Intent i = Games.RealTimeMultiplayer.getWaitingRoomIntent(this.helper.getApiClient(), room, 2);
		startActivityForResult(i, NetworkUtility.RC_WAITING_ROOM);
		
		//to main menu
		this.instance.toMenu(MainMenuScreen.INDEX_NETWORK, false);
	}

	@Override
	public void onInvitationReceived(Invitation inv) {
		//when we receive an invitation we should vibrate a bit
		Gdx.input.vibrate(new long[] {0, 100, 0, 100}, -1);
		
		//then add the invite to the queue to be displayed
		this.instance.pushInvite();
	}

	@Override
	public void onInvitationRemoved(String arg0) {
	}

	@Override
	public ArrayList<Participant> getParticipants() {
		//return all room participants
		return participants;
	}

	@Override
	public void nullifyRoomID() {
		//remove the room ID, so that other things know we don't have an active room
		this.roomID = null;
	}
	
	public void showOrLoadAd() {
		try {
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					if(interAd.isLoaded()) {
						interAd.show();
					} else {
						AdRequest req = new AdRequest.Builder().build();
						interAd.loadAd(req);
					}
				}
				
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
