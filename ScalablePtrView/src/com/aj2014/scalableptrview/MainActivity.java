package com.aj2014.scalableptrview;

import com.aj2014.scalableptrview.PtrLoadingView.IRefreshCallback;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity implements IRefreshCallback {
	
	ScalablePtrView mSptrView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_activity);
		mSptrView = (ScalablePtrView) findViewById(R.id.scalable_ptr_view);
		mSptrView.setRefreshCallback(this);
		ViewServer.get(getApplicationContext()).addWindow(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		ViewServer.get(getApplicationContext()).removeWindow(this);
	}

	@Override
	public void onRefresh() {
		mSptrView.postDelayed(new Runnable() {
			@Override
			public void run() {
				mSptrView.onRefreshComplete();
			}
		}, 1000l);
	}

}
