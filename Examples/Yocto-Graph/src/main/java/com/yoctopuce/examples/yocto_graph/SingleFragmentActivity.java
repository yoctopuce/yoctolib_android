package com.yoctopuce.examples.yocto_graph;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

public class SingleFragmentActivity extends FragmentActivity {

    private Fragment _fragment;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fragment);

        final ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
            // set up list nav
            ArrayAdapter<CharSequence> graphDurationAdapter = ArrayAdapter.createFromResource(this, R.array.graph_duration,
                    android.R.layout.simple_spinner_dropdown_item);

            actionBar.setListNavigationCallbacks(graphDurationAdapter,
                    new ActionBar.OnNavigationListener()
                    {
                        public boolean onNavigationItemSelected(int itemPosition,
                                                                long itemId)
                        {
                            int graphRange;
                            switch (itemPosition) {
                                case 0:
                                    graphRange = 60000;
                                    break;
                                case 1:
                                    graphRange = 5 * 60000;
                                    break;
                                case 2:
                                    graphRange = 15 * 60000;
                                    break;
                                case 3:
                                    graphRange = 30 * 60000;
                                    break;
                                case 4:
                                    graphRange = 60 * 60000;
                                    break;
                                default:
                                    return false;
                            }
                            ((GraphListFragment)_fragment).onRangeChange(graphRange);

                            return false;
                        }
                    });
            actionBar.setSelectedNavigationItem(0);
        }



		FragmentManager fm = getSupportFragmentManager();
        _fragment = fm.findFragmentById(R.id.fragmentContainter);
		if (_fragment == null) {
			_fragment =  new GraphListFragment();
			fm.beginTransaction().add(R.id.fragmentContainter, _fragment)
					.commit();
		}



	}


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_generic_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_about) {
            AboutDialog.showAbout(this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }




    @Override
    public void onStart()
    {
        super.onStart();
        YoctopuceBgThread.Start(this);
    }


    @Override
    public void onStop()
    {
        YoctopuceBgThread.Stop();
        super.onStop();
    }

}
