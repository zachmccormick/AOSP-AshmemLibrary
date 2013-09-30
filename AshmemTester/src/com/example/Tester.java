package com.example;

import java.util.Arrays;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import edu.vanderbilt.mccormick.ashmemlibrary.AshmemBuffer;

public class Tester extends Activity {

	public static final int PAGES = 1;
	public static final int BYTES = 16;

	public static byte[] x = { 1, 2, 3, 4, 5, 6, 7, 8, 9, 0, 1, 2, 3, 4, 5, 6 };

	public static AshmemBuffer b = null;

	IAshmemTestService mIAshmemTestService;
	private final ServiceConnection mConnection = new ServiceConnection() {
		// Called when the connection with the service is established
		@Override
		public void onServiceConnected(final ComponentName className,
				final IBinder service) {
			// Following the example above for an AIDL interface,
			// this gets an instance of the IRemoteInterface, which we can use
			// to call on the service
			mIAshmemTestService = IAshmemTestService.Stub.asInterface(service);
			try {
				mIAshmemTestService.work(b);
			} catch (final RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		// Called when the connection with the service disconnects unexpectedly
		@Override
		public void onServiceDisconnected(final ComponentName className) {
			mIAshmemTestService = null;
		}
	};

	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		b = AshmemBuffer.createAshmemBuffer(PAGES);
		b.setPosition(0);
		try {
			b.writeBytes(x);
		} catch (Exception e) {
			e.printStackTrace();
		}
		b.setPosition(0);
		Log.v("TAG", "Testing from Activity");
		try {
			if (!Arrays.equals(b.readBytes(BYTES), Tester.x))
				Log.e("TAG", "Arrays Weren't Equal.");
			else
				Log.v("TAG", "Arrays Were Equal.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		Log.v("TAG", "Finished Testing from Activity");
		bindService(new Intent(this, com.example.AshmemTestService.class),
				mConnection, BIND_AUTO_CREATE);
	}
}
