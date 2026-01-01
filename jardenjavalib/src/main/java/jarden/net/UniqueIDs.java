package jarden.net;

import java.util.UUID;

/**
 * Simple code to generate a universal unique identify; the string displayed is
 * used in jarden.bluetooth.BluetoothServer, part of an Android app.
 * @author John
 *
 */
public class UniqueIDs {
	public static void main(String[] args) {
		UUID a = UUID.randomUUID();
		System.out.println("uuid = " + a);
	}

}
