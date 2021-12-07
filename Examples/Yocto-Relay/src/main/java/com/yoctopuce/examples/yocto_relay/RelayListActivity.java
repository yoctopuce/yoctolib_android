package com.yoctopuce.examples.yocto_relay;


import androidx.fragment.app.Fragment;

public class RelayListActivity extends SingleFragmentActivity {
    
	@Override
	protected Fragment createFragment() {
		return new RelayListFragment();
	}

}
