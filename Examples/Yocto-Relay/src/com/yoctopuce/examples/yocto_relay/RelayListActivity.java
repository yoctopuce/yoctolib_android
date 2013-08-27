package com.yoctopuce.examples.yocto_relay;

import android.support.v4.app.Fragment;

public class RelayListActivity extends SingleFragmentActivity {
    
	@Override
	protected Fragment createFragment() {
		return new RelayListFragment();
	}

}
