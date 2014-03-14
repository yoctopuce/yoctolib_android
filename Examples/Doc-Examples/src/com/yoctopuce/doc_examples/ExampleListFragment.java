package com.yoctopuce.doc_examples;

import java.util.ArrayList;
import java.util.Collections;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * Created by seb on 09.09.13.
 */
public class ExampleListFragment extends ListFragment {
    private ArrayList<Example> mExampleList;
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mExampleList = new ArrayList<Example>();
        mExampleList.add(new Example("Hub Detected", HubListActivity.class,false));
        /** generated start */
        /** generated GettingStarted_Yocto_3D start */
        mExampleList.add(new Example("Yocto-3D", GettingStarted_Yocto_3D.class,false));
        /** generated GettingStarted_Yocto_3D stop */   
        /** generated GettingStarted_Yocto_0_10V_Rx start */
        mExampleList.add(new Example("Yocto-0-10V-Rx", GettingStarted_Yocto_0_10V_Rx.class,false));
        /** generated GettingStarted_Yocto_0_10V_Rx stop */   
        /** generated GettingStarted_Yocto_Demo start */
        mExampleList.add(new Example("Yocto-Demo", GettingStarted_Yocto_Demo.class,false));
        /** generated GettingStarted_Yocto_Demo stop */   
        /** generated GettingStarted_Yocto_Amp start */
        mExampleList.add(new Example("Yocto-Amp", GettingStarted_Yocto_Amp.class,false));
        /** generated GettingStarted_Yocto_Amp stop */   
        /** generated GettingStarted_Yocto_Volt start */
        mExampleList.add(new Example("Yocto-Volt", GettingStarted_Yocto_Volt.class,false));
        /** generated GettingStarted_Yocto_Volt stop */   
        /** generated GettingStarted_Yocto_Watt start */
        mExampleList.add(new Example("Yocto-Watt", GettingStarted_Yocto_Watt.class,false));
        /** generated GettingStarted_Yocto_Watt stop */   
        /** generated GettingStarted_Yocto_4_20mA_Rx start */
        mExampleList.add(new Example("Yocto-4-20mA-Rx", GettingStarted_Yocto_4_20mA_Rx.class,false));
        /** generated GettingStarted_Yocto_4_20mA_Rx stop */   
        /** generated GettingStarted_Yocto_CO2 start */
        mExampleList.add(new Example("Yocto-CO2", GettingStarted_Yocto_CO2.class,false));
        /** generated GettingStarted_Yocto_CO2 stop */   
        /** generated GettingStarted_Yocto_Maxi_IO start */
        mExampleList.add(new Example("Yocto-Maxi-IO", GettingStarted_Yocto_Maxi_IO.class,false));
        /** generated GettingStarted_Yocto_Maxi_IO stop */   
        /** generated GettingStarted_Yocto_PT100 start */
        mExampleList.add(new Example("Yocto-PT100", GettingStarted_Yocto_PT100.class,false));
        /** generated GettingStarted_Yocto_PT100 stop */   
        /** generated GettingStarted_Yocto_Knob start */
        mExampleList.add(new Example("Yocto-Knob", GettingStarted_Yocto_Knob.class,false));
        /** generated GettingStarted_Yocto_Knob stop */   
        /** generated GettingStarted_Yocto_Display start */
        mExampleList.add(new Example("Yocto-Display", GettingStarted_Yocto_Display.class,false));
        /** generated GettingStarted_Yocto_Display stop */   
        /** generated GettingStarted_Yocto_MaxiDisplay start */
        mExampleList.add(new Example("Yocto-MaxiDisplay", GettingStarted_Yocto_MaxiDisplay.class,false));
        /** generated GettingStarted_Yocto_MaxiDisplay stop */   
        /** generated GettingStarted_Yocto_MiniDisplay start */
        mExampleList.add(new Example("Yocto-MiniDisplay", GettingStarted_Yocto_MiniDisplay.class,false));
        /** generated GettingStarted_Yocto_MiniDisplay stop */   
        /** generated GettingStarted_Yocto_Color start */
        mExampleList.add(new Example("Yocto-Color", GettingStarted_Yocto_Color.class,false));
        /** generated GettingStarted_Yocto_Color stop */   
        /** generated GettingStarted_Yocto_PowerColor start */
        mExampleList.add(new Example("Yocto-PowerColor", GettingStarted_Yocto_PowerColor.class,false));
        /** generated GettingStarted_Yocto_PowerColor stop */   
        /** generated GettingStarted_Yocto_Light start */
        mExampleList.add(new Example("Yocto-Light", GettingStarted_Yocto_Light.class,false));
        /** generated GettingStarted_Yocto_Light stop */   
        /** generated GettingStarted_Yocto_MaxiCoupler start */
        mExampleList.add(new Example("Yocto-MaxiCoupler", GettingStarted_Yocto_MaxiCoupler.class,false));
        /** generated GettingStarted_Yocto_MaxiCoupler stop */   
        /** generated GettingStarted_Yocto_Meteo start */
        mExampleList.add(new Example("Yocto-Meteo", GettingStarted_Yocto_Meteo.class,false));
        /** generated GettingStarted_Yocto_Meteo stop */   
        /** generated GettingStarted_Yocto_Relay start */
        mExampleList.add(new Example("Yocto-Relay", GettingStarted_Yocto_Relay.class,false));
        /** generated GettingStarted_Yocto_Relay stop */   
        /** generated GettingStarted_Yocto_MaxiRelay start */
        mExampleList.add(new Example("Yocto-MaxiRelay", GettingStarted_Yocto_MaxiRelay.class,false));
        /** generated GettingStarted_Yocto_MaxiRelay stop */   
        /** generated GettingStarted_Yocto_PowerRelay start */
        mExampleList.add(new Example("Yocto-PowerRelay", GettingStarted_Yocto_PowerRelay.class,false));
        /** generated GettingStarted_Yocto_PowerRelay stop */   
        /** generated GettingStarted_Yocto_LatchedRelay start */
        mExampleList.add(new Example("Yocto-LatchedRelay", GettingStarted_Yocto_LatchedRelay.class,false));
        /** generated GettingStarted_Yocto_LatchedRelay stop */   
        /** generated GettingStarted_Yocto_Servo start */
        mExampleList.add(new Example("Yocto-Servo", GettingStarted_Yocto_Servo.class,false));
        /** generated GettingStarted_Yocto_Servo stop */   
        /** generated GettingStarted_Yocto_Temperature start */
        mExampleList.add(new Example("Yocto-Temperature", GettingStarted_Yocto_Temperature.class,false));
        /** generated GettingStarted_Yocto_Temperature stop */   
        /** generated GettingStarted_Yocto_Thermocouple start */
        mExampleList.add(new Example("Yocto-Thermocouple", GettingStarted_Yocto_Thermocouple.class,false));
        /** generated GettingStarted_Yocto_Thermocouple stop */   
        /** generated GettingStarted_Yocto_VOC start */
        mExampleList.add(new Example("Yocto-VOC", GettingStarted_Yocto_VOC.class,false));
        /** generated GettingStarted_Yocto_VOC stop */   
        /** generated SaveSettings start */
        mExampleList.add(new Example("SaveSettings", SaveSettings.class,true));
        /** generated SaveSettings stop */   
        /** generated ModuleControl start */
        mExampleList.add(new Example("ModuleControl", ModuleControl.class,true));
        /** generated ModuleControl stop */   
        /** generated Inventory start */
        mExampleList.add(new Example("Inventory", Inventory.class,true));
        /** generated Inventory stop */   
        /** generated stop */
        Collections.sort(mExampleList);
        ArrayAdapter<Example> adapter =
                new ArrayAdapter<Example>(getActivity(),
                        android.R.layout.simple_list_item_1,
                        mExampleList);
        setListAdapter(adapter);
    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        Example ex = (Example) getListAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), ex.getClassToExecute());
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
