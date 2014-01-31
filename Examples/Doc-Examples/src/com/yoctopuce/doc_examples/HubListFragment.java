package com.yoctopuce.doc_examples;

import java.util.ArrayList;

import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI.NewHubCallback;
import com.yoctopuce.YoctoAPI.YAPI_Exception;

import android.app.ListFragment;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class HubListFragment extends ListFragment {
	private class DetectedHub {
		private String mSerial;
		private String mUrl;
		
		public DetectedHub(String serial,String url) {
			mSerial=serial;
			mUrl=url;
		}
		
		
		public String getSerial() {
			return mSerial;
		}


		public String getUrl() {
			return mUrl;
		}


		public void setUrl(String url) {
			mUrl = url;
		}


		@Override
		public String toString() {
			return mSerial +" ("+mUrl+")";
		}
		
		
	}

	protected static final String NEW_HUB_SERIAL = "new_hub_serial";
	protected static final String NEW_HUB_URL = "new_hub_url";

	private ArrayList<DetectedHub> mHubList = new ArrayList<DetectedHub>();
	
	
    private Handler mMainHandler = new Handler() {
		public void handleMessage(Message msg) {
			String serial =msg.getData().getString(NEW_HUB_SERIAL);    			    			
			String url =msg.getData().getString(NEW_HUB_URL);
        	for(DetectedHub h : mHubList){
        		if (h.getSerial().equals(serial)){
        			h.setUrl(url);
        			mAdapter.notifyDataSetChanged();
        			return;
        		}
        		
        	}
        	mHubList.add(new DetectedHub(serial, url));
			mAdapter.notifyDataSetChanged();
        }
    };

    
    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        DetectedHub hub = (DetectedHub) mAdapter.getItem(position);
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://"+hub.getUrl()));
        startActivity(browserIntent);
    }

    
    
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mAdapter = new ArrayAdapter<DetectedHub>(getActivity(),
		        android.R.layout.simple_list_item_1,
		        mHubList);
        setListAdapter(mAdapter);
    }


    @Override
	public void onStart()
    {
        super.onStart();
        AsyncTask<Integer,Integer,Integer> task = new AsyncTask<Integer, Integer, Integer>() {

            @Override
            protected Integer doInBackground(Integer... params) {
                try {
                    //YAPI.EnableUSBHost(getActivity());
                    YAPI.InitAPI(YAPI.DETECT_NONE);
                    YAPI.RegisterHubDiscoveryCallback(mNewHub);                    
                } catch (YAPI_Exception e) {
                    e.printStackTrace();
                }
                return 0;
            }
        };
        task.execute(0);
    }

    
    
    private NewHubCallback mNewHub = new NewHubCallback() {
        @Override
        public void yNewHub(String serial, String url) {
            Message myMessage = mMainHandler.obtainMessage();
            Bundle messageBundle =new Bundle();
			messageBundle.putString(NEW_HUB_SERIAL,serial);
			messageBundle.putString(NEW_HUB_URL, url);
            myMessage.setData(messageBundle);
            mMainHandler.sendMessage(myMessage);
        }
    };
	private ArrayAdapter<DetectedHub> mAdapter;


    @Override
	public void onStop()
    {
        super.onStop();
        YAPI.FreeAPI();
    }


}
