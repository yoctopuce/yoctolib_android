package com.yoctopuce.examples.yocto_graph;


import android.support.v4.app.Fragment;

public class GraphListActivity extends SingleFragmentActivity {
    @Override
    protected Fragment createFragment()
    {
        return new GraphListFragment();
    }
}
