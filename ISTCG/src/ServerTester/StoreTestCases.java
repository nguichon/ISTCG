package ServerTester;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

import org.junit.Assert;
import org.junit.Test;

import Shared.ClientResponses;


public class StoreTestCases {
	static TestClient client = new TestClient( "Grogian", "password" );
	
	@Test
	public void testGetBalance() {
		client.SendMessage( ClientResponses.BALANCE );
		String[] r = client.GetResponse();
		Assert.assertEquals( "BALANCE" , r[0] );
		Assert.assertTrue( Integer.valueOf( r[1] ) >= 0 );
	}
	
	@Test
	public void testGetProductInfo() {
		client.SendMessage( ClientResponses.PRODUCTINFO, String.valueOf(0) );
		String[] r = client.GetResponse();
		Assert.assertEquals( "PRODUCTINFO" , r[0] );
		Assert.assertTrue( Integer.valueOf( r[1] ) == 0 );
		Assert.assertTrue( Integer.valueOf( r[2] ) == 200 );	
		Assert.assertTrue( r[3].equals( "Card Pack" ) );		
	}
	
	@Test
	public void testNoneExistantProduct() {
		client.SendMessage( ClientResponses.PRODUCTINFO, String.valueOf(1) );
		String[] r = client.GetResponse();
		Assert.assertEquals( "SERVER" , r[0] );	
	}
	
	@Test
	public void testBuyItem() {
		client.SendMessage( ClientResponses.BALANCE );
		String[] r = client.GetResponse();
		Assert.assertEquals( "BALANCE" , r[0] );
		int balance = Integer.valueOf( r[1] );
		
		client.SendMessage( ClientResponses.PRODUCTINFO, String.valueOf(0) );
		r = client.GetResponse();
		Assert.assertEquals( "PRODUCTINFO" , r[0] );
		int cost = Integer.valueOf( r[2] );
		
		client.SendMessage( ClientResponses.PURCHASE, String.valueOf( 0 ) );
		r = client.GetResponse();
		Assert.assertEquals( "RECEIPT" , r[0] );
		Assert.assertTrue( Integer.valueOf( r[1] ) == balance - cost );
		
		client.SendMessage( ClientResponses.BALANCE );
		r = client.GetResponse();
		Assert.assertEquals( "BALANCE" , r[0] );
		Assert.assertTrue( balance - cost == Integer.valueOf( r[1] ));
	}
	
}
