package com.aj2014.scalableptrview;

import android.content.Context;
import android.graphics.Color;
import android.test.ApplicationTestCase;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2014/6/30.
 */
public class CustomListAdapter extends BaseAdapter {

    Context context;
    /**
     * items' data, also you can custom it
     */
    String[] items;
    /**
     * items' view's height entries
     */
    SparseIntArray mHeightEntries;
    /**
     * the entries of the distance from item's top to it's parent's top
     */
    SparseIntArray mDistanceEntries;

    public CustomListAdapter(Context context, String[] items) {
        this.context = context;
        this.items = items;
        mHeightEntries = new SparseIntArray();
        mDistanceEntries = new SparseIntArray();
        // first item has the distance of 0
        mDistanceEntries.put(0, 0);
    }

    @Override
    public int getCount() {
        return null == items ? 0 : items.length;
    }

    @Override
    public Object getItem(int i) {
        return null == items ? null : items[i];
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        String curItem = (String) getItem(i);
        TextView tView = null;
        if (null == view) {
            view = LayoutInflater.from(context).inflate(R.layout.layout_list_item, null);
        }
        tView = (TextView) view.findViewById(R.id.title);
        tView.setText("Item-" + i);
        tView.setBackgroundColor(Color.parseColor(curItem));
        addViewObserverCallback(view, new CustomedOnGlobalLayoutListener(view, i));
        return view;
    }
    
    /**
     * custom OnGlobalLayoutListener to obtain the relative item's view's height
     * at 'position'
     * @author Administrator
     */
    public class CustomedOnGlobalLayoutListener implements OnGlobalLayoutListener {
    	
    	int position;
    	View relativeView;
    	
    	public CustomedOnGlobalLayoutListener(View view, int position) {
    		this.position = position;
    		relativeView = view;
    	}
    	
		@Override
		public void onGlobalLayout() {
			// first remove callback, avoid the loop
			removeViewObserverCallback(relativeView, CustomedOnGlobalLayoutListener.this);
			final ListView parent = (ListView)relativeView.getParent();
			if (null == parent) {
				return;
			}
			final int height = relativeView.getHeight();
			// store the height
			mHeightEntries.put(position + 1, height);
			final int preDist = mDistanceEntries.get(position + 1);
			final int curDist = preDist + parent.getDividerHeight() + height; 
			// store the distance
			mDistanceEntries.put(position + 2, curDist);
		}
    	
    }
    
    public void addViewObserverCallback(View view, OnGlobalLayoutListener listener) {
    	if (null != view) {
    		view.getViewTreeObserver().addOnGlobalLayoutListener(listener);
    	}
    }
    
    private void removeViewObserverCallback(View view, OnGlobalLayoutListener victim) {
    	if (null != view) {
    		view.getViewTreeObserver().removeGlobalOnLayoutListener(victim);
    	}
    }

    public int getItemHeightAtPosition(int pos) {
    	return mHeightEntries.get(pos);
    }
    
    /**
     * @param pos item's index
     * @return distance between item's top and container' top
     */
    public int getItemDistanceFromTopAtPosition(int pos) {
    	return mDistanceEntries.get(pos);
    }

}
