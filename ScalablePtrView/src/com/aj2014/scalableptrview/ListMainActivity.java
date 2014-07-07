package com.aj2014.scalableptrview;

import com.aj2014.scalableptrview.PtrLoadingView.IRefreshCallback;
import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class ListMainActivity extends Activity implements IRefreshCallback {
	
	ScalablePtrView mSptrView;
	ListView mListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list_main_activity);
		mSptrView = (ScalablePtrView) findViewById(R.id.scalable_ptr_view);
		mListView = (ListView) findViewById(R.id.list_items);
		
		mSptrView.setRefreshCallback(this);
		mListView.setAdapter(new CustomListAdapter(getApplicationContext(), new String[]{
			"#000000","#0000FF", "#000FF0", "#00FF00"
			,"#000000"
			,"#0000FF", "#000FF0", "#00FF00",
			"#000000","#0000FF", "#000FF0", "#00FF00","#000000","#0000FF", "#000FF0", "#00FF00"
		}));
		
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
