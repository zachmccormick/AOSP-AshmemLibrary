package com.example;

import java.util.Arrays;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;
import edu.vanderbilt.mccormick.ashmemlibrary.AshmemBuffer;

public class AshmemTestService extends Service {

	public IAshmemTestService.Stub mBinder = new IAshmemTestService.Stub() {

		@Override
		public void work(final AshmemBuffer b) throws RemoteException {
			Log.v("TAG", "Service called!  Checking if arrays are the same...");
			b.setPosition(0);
			try {
				if (!Arrays.equals(b.readBytes(Tester.BYTES), Tester.x))
					Log.e("TAG", "Arrays Weren't Equal.");
				else
					Log.v("TAG", "Arrays Were Equal.");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Log.v("TAG", "Service completed!");
		}
	};

	@Override
	public IBinder onBind(final Intent intent) {
		return mBinder;
	}
}
