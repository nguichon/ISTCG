package Shared;

public interface NetworkObject extends Comparable<NetworkObject> {
	void SetNetworkID( int new_ID );
	int GetNetworkID( );
	boolean ParseMessage( String s );
}