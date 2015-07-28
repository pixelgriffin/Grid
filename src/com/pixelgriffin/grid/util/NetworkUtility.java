package com.pixelgriffin.grid.util;

import com.google.android.gms.games.Games;
import com.google.android.gms.games.multiplayer.Participant;
import com.pixelgriffin.grid.android.GridGame;

/**
 * 
 * @author Nathan
 *
 */
public class NetworkUtility {
	public static final int RC_SELECT_PLAYERS = 10000;
	public static final int RC_INVITATION_INBOX = 10001;
	public static final int RC_WAITING_ROOM = 10002;
	
	public static final int PACKET_BUILD = 0;
	public static final int PACKET_DESTROY = 1;
	//public static final int PACKET_HEALTH = 1;
	
	public static void sendMessageReliable(byte[] _data, GridGame _instance) {
		
		for(Participant p : _instance.getGPGS().getParticipants()) {
			if(p.getParticipantId().equalsIgnoreCase(_instance.getGPGS().getMyID()))
				continue;
			
			Games.RealTimeMultiplayer.sendReliableMessage(_instance.getGPGS().getAPI(), _instance.getGPGS().getInstance(), _data, _instance.getGPGS().getActiveRoomID(), p.getParticipantId());
		}
	}
	
	public static void sendMessageUnreliable(byte[] _data, GridGame _instance) {
		
		for(Participant p : _instance.getGPGS().getParticipants()) {
			if(p.getParticipantId().equalsIgnoreCase(_instance.getGPGS().getMyID()))
				continue;
			
			//Games.RealTimeMultiplayer.sendUnr
			//Games.RealTimeMultiplayer.sendUnreliableMessage(_instance.getGPGS().getAPI(), _instance.getGPGS().getInstance(), _data, _instance.getGPGS().getActiveRoomID(), p.getParticipantId());
		}
	}
}
