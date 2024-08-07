package com.yoctopuce.doc_examples;

import java.util.ArrayList;
import java.util.Collections;

import android.app.ListFragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ExampleListFragment extends ListFragment {
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        ArrayList<Example> mExampleList = new ArrayList<Example>();
        mExampleList.add(new Example("Hub Detected", HubListActivity.class, false));
        mExampleList.add(new Example("Prog-EventBased", ProgEventBasedActivity.class, false));
        mExampleList.add(new Example("Inventory (Network)", InventoryNetwork.class, false));
        mExampleList.add(new Example("Prog-MODBUS", ProgModbus.class, false));
        /** generated start */
        /** generated GettingStarted_Yocto_RFID start */
        mExampleList.add(new Example("Yocto-RFID", GettingStarted_Yocto_RFID.class, false));
        /** generated GettingStarted_Yocto_RFID stop */   
        /** generated GettingStarted_Yocto_SDI12 start */
        mExampleList.add(new Example("Yocto-SDI12", GettingStarted_Yocto_SDI12.class, false));
        /** generated GettingStarted_Yocto_SDI12 stop */   
        /** generated GettingStarted_Yocto_MaxiKnob start */
        mExampleList.add(new Example("Yocto-MaxiKnob", GettingStarted_Yocto_MaxiKnob.class, false));
        /** generated GettingStarted_Yocto_MaxiKnob stop */   
        /** generated GettingStarted_Yocto_Inclinometer start */
        mExampleList.add(new Example("Yocto-Inclinometer", GettingStarted_Yocto_Inclinometer.class, false));
        /** generated GettingStarted_Yocto_Inclinometer stop */   
        /** generated GettingStarted_Yocto_MaxiBuzzer start */
        mExampleList.add(new Example("Yocto-MaxiBuzzer", GettingStarted_Yocto_MaxiBuzzer.class, false));
        /** generated GettingStarted_Yocto_MaxiBuzzer stop */   
        /** generated GettingStarted_Yocto_MaxiMicroVolt_Rx start */
        mExampleList.add(new Example("Yocto-MaxiMicroVolt-Rx", GettingStarted_Yocto_MaxiMicroVolt_Rx.class, false));
        /** generated GettingStarted_Yocto_MaxiMicroVolt_Rx stop */   
        /** generated GettingStarted_Yocto_I2C start */
        mExampleList.add(new Example("Yocto-I2C", GettingStarted_Yocto_I2C.class, false));
        /** generated GettingStarted_Yocto_I2C stop */   
        /** generated GettingStarted_Yocto_Pressure start */
        mExampleList.add(new Example("Yocto-Pressure", GettingStarted_Yocto_Pressure.class, false));
        /** generated GettingStarted_Yocto_Pressure stop */   
        /** generated GettingStarted_Yocto_Temperature_IR start */
        mExampleList.add(new Example("Yocto-Temperature-IR", GettingStarted_Yocto_Temperature_IR.class, false));
        /** generated GettingStarted_Yocto_Temperature_IR stop */   
        /** generated GettingStarted_Yocto_Color_V2 start */
        mExampleList.add(new Example("Yocto-Color-V2", GettingStarted_Yocto_Color_V2.class, false));
        /** generated GettingStarted_Yocto_Color_V2 stop */   
        /** generated GettingStarted_Yocto_Bridge start */
        mExampleList.add(new Example("Yocto-Bridge", GettingStarted_Yocto_Bridge.class, false));
        /** generated GettingStarted_Yocto_Bridge stop */   
        /** generated GettingStarted_Yocto_MaxiBridge start */
        mExampleList.add(new Example("Yocto-MaxiBridge", GettingStarted_Yocto_MaxiBridge.class, false));
        /** generated GettingStarted_Yocto_MaxiBridge stop */   
        /** generated GettingStarted_Yocto_0_10V_Tx start */
        mExampleList.add(new Example("Yocto-0-10V-Tx", GettingStarted_Yocto_0_10V_Tx.class, false));
        /** generated GettingStarted_Yocto_0_10V_Tx stop */   
        /** generated GettingStarted_Yocto_IO start */
        mExampleList.add(new Example("Yocto-IO", GettingStarted_Yocto_IO.class, false));
        /** generated GettingStarted_Yocto_IO stop */   
        /** generated GettingStarted_Yocto_RangeFinder start */
        mExampleList.add(new Example("Yocto-RangeFinder", GettingStarted_Yocto_RangeFinder.class, false));
        /** generated GettingStarted_Yocto_RangeFinder stop */   
        /** generated GettingStarted_Yocto_Proximity start */
        mExampleList.add(new Example("Yocto-Proximity", GettingStarted_Yocto_Proximity.class, false));
        /** generated GettingStarted_Yocto_Proximity stop */   
        /** generated GettingStarted_Yocto_3D_V2 start */
        mExampleList.add(new Example("Yocto-3D-V2", GettingStarted_Yocto_3D_V2.class, false));
        /** generated GettingStarted_Yocto_3D_V2 stop */   
        /** generated GettingStarted_Yocto_SPI start */
        mExampleList.add(new Example("Yocto-SPI", GettingStarted_Yocto_SPI.class, false));
        /** generated GettingStarted_Yocto_SPI stop */   
        /** generated GettingStarted_Yocto_MaxiThermistor start */
        mExampleList.add(new Example("Yocto-MaxiThermistor", GettingStarted_Yocto_MaxiThermistor.class, false));
        /** generated GettingStarted_Yocto_MaxiThermistor stop */   
        /** generated GettingStarted_Yocto_4_20mA_Tx start */
        mExampleList.add(new Example("Yocto-4-20mA-Tx", GettingStarted_Yocto_4_20mA_Tx.class, false));
        /** generated GettingStarted_Yocto_4_20mA_Tx stop */   
        /** generated GettingStarted_Yocto_Light_V3 start */
        mExampleList.add(new Example("Yocto-Light-V3", GettingStarted_Yocto_Light_V3.class, false));
        /** generated GettingStarted_Yocto_Light_V3 stop */   
        /** generated GettingStarted_Yocto_Thermistor_C start */
        mExampleList.add(new Example("Yocto-Thermistor-C", GettingStarted_Yocto_Thermistor_C.class, false));
        /** generated GettingStarted_Yocto_Thermistor_C stop */   
        /** generated GettingStarted_Yocto_RS485 start */
        mExampleList.add(new Example("Yocto-RS485", GettingStarted_Yocto_RS485.class, false));
        /** generated GettingStarted_Yocto_RS485 stop */   
        /** generated GettingStarted_Yocto_Serial start */
        mExampleList.add(new Example("Yocto-Serial", GettingStarted_Yocto_Serial.class, false));
        /** generated GettingStarted_Yocto_Serial stop */   
        /** generated GettingStarted_Yocto_GPS start */
        mExampleList.add(new Example("Yocto-GPS", GettingStarted_Yocto_GPS.class, false));
        /** generated GettingStarted_Yocto_GPS stop */   
        /** generated GettingStarted_Yocto_RS232 start */
        mExampleList.add(new Example("Yocto-RS232", GettingStarted_Yocto_RS232.class, false));
        /** generated GettingStarted_Yocto_RS232 stop */   
        /** generated GettingStarted_Yocto_Buzzer start */
        mExampleList.add(new Example("Yocto-Buzzer", GettingStarted_Yocto_Buzzer.class, false));
        /** generated GettingStarted_Yocto_Buzzer stop */   
        /** generated GettingStarted_Yocto_MaxiPowerRelay start */
        mExampleList.add(new Example("Yocto-MaxiPowerRelay", GettingStarted_Yocto_MaxiPowerRelay.class, false));
        /** generated GettingStarted_Yocto_MaxiPowerRelay stop */
        /** generated GettingStarted_Yocto_WatchdogDC start */
        mExampleList.add(new Example("Yocto-WatchdogDC", GettingStarted_Yocto_WatchdogDC.class, false));
        /** generated GettingStarted_Yocto_WatchdogDC stop */
        /** generated GettingStarted_Yocto_PWM_Rx start */
        mExampleList.add(new Example("Yocto-PWM-Rx", GettingStarted_Yocto_PWM_Rx.class, false));
        /** generated GettingStarted_Yocto_PWM_Rx stop */
        /** generated GettingStarted_Yocto_milliVolt_Rx_BNC start */
        mExampleList.add(new Example("Yocto-milliVolt-Rx-BNC", GettingStarted_Yocto_milliVolt_Rx_BNC.class, false));
        /** generated GettingStarted_Yocto_milliVolt_Rx_BNC stop */
        /** generated GettingStarted_Yocto_milliVolt_Rx start */
        mExampleList.add(new Example("Yocto-milliVolt-Rx", GettingStarted_Yocto_milliVolt_Rx.class, false));
        /** generated GettingStarted_Yocto_milliVolt_Rx stop */
        /** generated GettingStarted_Yocto_Motor_DC start */
        mExampleList.add(new Example("Yocto-Motor-DC", GettingStarted_Yocto_Motor_DC.class, false));
        /** generated GettingStarted_Yocto_Motor_DC stop */
        /** generated GettingStarted_Yocto_Altimeter start */
        mExampleList.add(new Example("Yocto-Altimeter", GettingStarted_Yocto_Altimeter.class, false));
        /** generated GettingStarted_Yocto_Altimeter stop */
        /** generated GettingStarted_Yocto_PWM_Tx start */
        mExampleList.add(new Example("Yocto-PWM-Tx", GettingStarted_Yocto_PWM_Tx.class, false));
        /** generated GettingStarted_Yocto_PWM_Tx stop */
        /** generated GettingStarted_Yocto_3D start */
        mExampleList.add(new Example("Yocto-3D", GettingStarted_Yocto_3D.class, false));
        /** generated GettingStarted_Yocto_3D stop */
        /** generated GettingStarted_Yocto_0_10V_Rx start */
        mExampleList.add(new Example("Yocto-0-10V-Rx", GettingStarted_Yocto_0_10V_Rx.class, false));
        /** generated GettingStarted_Yocto_0_10V_Rx stop */
        /** generated GettingStarted_Yocto_Demo start */
        mExampleList.add(new Example("Yocto-Demo", GettingStarted_Yocto_Demo.class, false));
        /** generated GettingStarted_Yocto_Demo stop */
        /** generated GettingStarted_Yocto_Amp start */
        mExampleList.add(new Example("Yocto-Amp", GettingStarted_Yocto_Amp.class, false));
        /** generated GettingStarted_Yocto_Amp stop */
        /** generated GettingStarted_Yocto_Volt start */
        mExampleList.add(new Example("Yocto-Volt", GettingStarted_Yocto_Volt.class, false));
        /** generated GettingStarted_Yocto_Volt stop */
        /** generated GettingStarted_Yocto_Watt start */
        mExampleList.add(new Example("Yocto-Watt", GettingStarted_Yocto_Watt.class, false));
        /** generated GettingStarted_Yocto_Watt stop */
        /** generated GettingStarted_Yocto_4_20mA_Rx start */
        mExampleList.add(new Example("Yocto-4-20mA-Rx", GettingStarted_Yocto_4_20mA_Rx.class, false));
        /** generated GettingStarted_Yocto_4_20mA_Rx stop */
        /** generated GettingStarted_Yocto_CO2 start */
        mExampleList.add(new Example("Yocto-CO2", GettingStarted_Yocto_CO2.class, false));
        /** generated GettingStarted_Yocto_CO2 stop */
        /** generated GettingStarted_Yocto_Maxi_IO start */
        mExampleList.add(new Example("Yocto-Maxi-IO", GettingStarted_Yocto_Maxi_IO.class, false));
        /** generated GettingStarted_Yocto_Maxi_IO stop */
        /** generated GettingStarted_Yocto_PT100 start */
        mExampleList.add(new Example("Yocto-PT100", GettingStarted_Yocto_PT100.class, false));
        /** generated GettingStarted_Yocto_PT100 stop */
        /** generated GettingStarted_Yocto_Knob start */
        mExampleList.add(new Example("Yocto-Knob", GettingStarted_Yocto_Knob.class, false));
        /** generated GettingStarted_Yocto_Knob stop */
        /** generated GettingStarted_Yocto_Display start */
        mExampleList.add(new Example("Yocto-Display", GettingStarted_Yocto_Display.class, false));
        /** generated GettingStarted_Yocto_Display stop */
        /** generated GettingStarted_Yocto_MaxiDisplay start */
        mExampleList.add(new Example("Yocto-MaxiDisplay", GettingStarted_Yocto_MaxiDisplay.class, false));
        /** generated GettingStarted_Yocto_MaxiDisplay stop */
        /** generated GettingStarted_Yocto_MiniDisplay start */
        mExampleList.add(new Example("Yocto-MiniDisplay", GettingStarted_Yocto_MiniDisplay.class, false));
        /** generated GettingStarted_Yocto_MiniDisplay stop */
        /** generated GettingStarted_Yocto_Color start */
        mExampleList.add(new Example("Yocto-Color", GettingStarted_Yocto_Color.class, false));
        /** generated GettingStarted_Yocto_Color stop */
        /** generated GettingStarted_Yocto_PowerColor start */
        mExampleList.add(new Example("Yocto-PowerColor", GettingStarted_Yocto_PowerColor.class, false));
        /** generated GettingStarted_Yocto_PowerColor stop */
        /** generated GettingStarted_Yocto_Light start */
        mExampleList.add(new Example("Yocto-Light", GettingStarted_Yocto_Light.class, false));
        /** generated GettingStarted_Yocto_Light stop */
        /** generated GettingStarted_Yocto_MaxiCoupler start */
        mExampleList.add(new Example("Yocto-MaxiCoupler", GettingStarted_Yocto_MaxiCoupler.class, false));
        /** generated GettingStarted_Yocto_MaxiCoupler stop */
        /** generated GettingStarted_Yocto_Meteo start */
        mExampleList.add(new Example("Yocto-Meteo", GettingStarted_Yocto_Meteo.class, false));
        /** generated GettingStarted_Yocto_Meteo stop */
        /** generated GettingStarted_Yocto_Relay start */
        mExampleList.add(new Example("Yocto-Relay", GettingStarted_Yocto_Relay.class, false));
        /** generated GettingStarted_Yocto_Relay stop */
        /** generated GettingStarted_Yocto_MaxiRelay start */
        mExampleList.add(new Example("Yocto-MaxiRelay", GettingStarted_Yocto_MaxiRelay.class, false));
        /** generated GettingStarted_Yocto_MaxiRelay stop */
        /** generated GettingStarted_Yocto_PowerRelay start */
        mExampleList.add(new Example("Yocto-PowerRelay", GettingStarted_Yocto_PowerRelay.class, false));
        /** generated GettingStarted_Yocto_PowerRelay stop */
        /** generated GettingStarted_Yocto_LatchedRelay start */
        mExampleList.add(new Example("Yocto-LatchedRelay", GettingStarted_Yocto_LatchedRelay.class, false));
        /** generated GettingStarted_Yocto_LatchedRelay stop */
        /** generated GettingStarted_Yocto_Servo start */
        mExampleList.add(new Example("Yocto-Servo", GettingStarted_Yocto_Servo.class, false));
        /** generated GettingStarted_Yocto_Servo stop */
        /** generated GettingStarted_Yocto_Temperature start */
        mExampleList.add(new Example("Yocto-Temperature", GettingStarted_Yocto_Temperature.class, false));
        /** generated GettingStarted_Yocto_Temperature stop */
        /** generated GettingStarted_Yocto_Thermocouple start */
        mExampleList.add(new Example("Yocto-Thermocouple", GettingStarted_Yocto_Thermocouple.class, false));
        /** generated GettingStarted_Yocto_Thermocouple stop */
        /** generated GettingStarted_Yocto_VOC start */
        mExampleList.add(new Example("Yocto-VOC", GettingStarted_Yocto_VOC.class, false));
        /** generated GettingStarted_Yocto_VOC stop */
        /** generated SaveSettings start */
        mExampleList.add(new Example("SaveSettings", SaveSettings.class, true));
        /** generated SaveSettings stop */
        /** generated ModuleControl start */
        mExampleList.add(new Example("ModuleControl", ModuleControl.class, true));
        /** generated ModuleControl stop */
        /** generated Inventory start */
        mExampleList.add(new Example("Inventory", Inventory.class, true));
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
