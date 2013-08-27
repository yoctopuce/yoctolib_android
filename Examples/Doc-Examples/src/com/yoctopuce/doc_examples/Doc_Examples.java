package com.yoctopuce.doc_examples;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;

public class Doc_Examples extends Activity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doc_examples);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.activity_doc__examples, menu);
        return true;
    }

    /** Called when the user clicks the Send button */
    public void sendMessage(View view)
    {
        Intent intent = null;
        switch (view.getId()) {
        /** generated start */
        /** generated GettingStarted_Yocto_LatchedRelay start */
        case R.id.button_GettingStarted_Yocto_LatchedRelay:
           intent = new Intent(this, GettingStarted_Yocto_LatchedRelay.class);
           break;
        /** generated GettingStarted_Yocto_LatchedRelay stop */   
        /** generated GettingStarted_Yocto_MaxiCoupler start */
        case R.id.button_GettingStarted_Yocto_MaxiCoupler:
           intent = new Intent(this, GettingStarted_Yocto_MaxiCoupler.class);
           break;
        /** generated GettingStarted_Yocto_MaxiCoupler stop */   
        /** generated GettingStarted_Yocto_Display start */
        case R.id.button_GettingStarted_Yocto_Display:
           intent = new Intent(this, GettingStarted_Yocto_Display.class);
           break;
        /** generated GettingStarted_Yocto_Display stop */   
        /** generated GettingStarted_Yocto_MaxiDisplay start */
        case R.id.button_GettingStarted_Yocto_MaxiDisplay:
           intent = new Intent(this, GettingStarted_Yocto_MaxiDisplay.class);
           break;
        /** generated GettingStarted_Yocto_MaxiDisplay stop */   
        /** generated GettingStarted_Yocto_MiniDisplay start */
        case R.id.button_GettingStarted_Yocto_MiniDisplay:
           intent = new Intent(this, GettingStarted_Yocto_MiniDisplay.class);
           break;
        /** generated GettingStarted_Yocto_MiniDisplay stop */   
        /** generated GettingStarted_Yocto_Watt start */
        case R.id.button_GettingStarted_Yocto_Watt:
           intent = new Intent(this, GettingStarted_Yocto_Watt.class);
           break;
        /** generated GettingStarted_Yocto_Watt stop */   
        /** generated GettingStarted_Yocto_Amp start */
        case R.id.button_GettingStarted_Yocto_Amp:
           intent = new Intent(this, GettingStarted_Yocto_Amp.class);
           break;
        /** generated GettingStarted_Yocto_Amp stop */
        /** generated GettingStarted_Yocto_CO2 start */
        case R.id.button_GettingStarted_Yocto_CO2:
           intent = new Intent(this, GettingStarted_Yocto_CO2.class);
           break;
        /** generated GettingStarted_Yocto_CO2 stop */
        /** generated GettingStarted_Yocto_Knob start */
        case R.id.button_GettingStarted_Yocto_Knob:
           intent = new Intent(this, GettingStarted_Yocto_Knob.class);
           break;
        /** generated GettingStarted_Yocto_Knob stop */
        /** generated GettingStarted_Yocto_Color start */
        case R.id.button_GettingStarted_Yocto_Color:
           intent = new Intent(this, GettingStarted_Yocto_Color.class);
           break;
        /** generated GettingStarted_Yocto_Color stop */
        /** generated GettingStarted_Yocto_PowerColor start */
        case R.id.button_GettingStarted_Yocto_PowerColor:
           intent = new Intent(this, GettingStarted_Yocto_PowerColor.class);
           break;
        /** generated GettingStarted_Yocto_PowerColor stop */
        /** generated GettingStarted_Yocto_Light start */
        case R.id.button_GettingStarted_Yocto_Light:
           intent = new Intent(this, GettingStarted_Yocto_Light.class);
           break;
        /** generated GettingStarted_Yocto_Light stop */
        /** generated GettingStarted_Yocto_Meteo start */
        case R.id.button_GettingStarted_Yocto_Meteo:
           intent = new Intent(this, GettingStarted_Yocto_Meteo.class);
           break;
        /** generated GettingStarted_Yocto_Meteo stop */
        /** generated GettingStarted_Yocto_Relay start */
        case R.id.button_GettingStarted_Yocto_Relay:
           intent = new Intent(this, GettingStarted_Yocto_Relay.class);
           break;
        /** generated GettingStarted_Yocto_Relay stop */
        /** generated GettingStarted_Yocto_MaxiRelay start */
        case R.id.button_GettingStarted_Yocto_MaxiRelay:
           intent = new Intent(this, GettingStarted_Yocto_MaxiRelay.class);
           break;
        /** generated GettingStarted_Yocto_MaxiRelay stop */
        /** generated GettingStarted_Yocto_PowerRelay start */
        case R.id.button_GettingStarted_Yocto_PowerRelay:
           intent = new Intent(this, GettingStarted_Yocto_PowerRelay.class);
           break;
        /** generated GettingStarted_Yocto_PowerRelay stop */
        /** generated GettingStarted_Yocto_Servo start */
        case R.id.button_GettingStarted_Yocto_Servo:
           intent = new Intent(this, GettingStarted_Yocto_Servo.class);
           break;
        /** generated GettingStarted_Yocto_Servo stop */
        /** generated GettingStarted_Yocto_Temperature start */
        case R.id.button_GettingStarted_Yocto_Temperature:
           intent = new Intent(this, GettingStarted_Yocto_Temperature.class);
           break;
        /** generated GettingStarted_Yocto_Temperature stop */
        /** generated GettingStarted_Yocto_Thermocouple start */
        case R.id.button_GettingStarted_Yocto_Thermocouple:
           intent = new Intent(this, GettingStarted_Yocto_Thermocouple.class);
           break;
        /** generated GettingStarted_Yocto_Thermocouple stop */
        /** generated GettingStarted_Yocto_VOC start */
        case R.id.button_GettingStarted_Yocto_VOC:
           intent = new Intent(this, GettingStarted_Yocto_VOC.class);
           break;
        /** generated GettingStarted_Yocto_VOC stop */
        /** generated GettingStarted_Yocto_Volt start */
        case R.id.button_GettingStarted_Yocto_Volt:
           intent = new Intent(this, GettingStarted_Yocto_Volt.class);
           break;
        /** generated GettingStarted_Yocto_Volt stop */
        /** generated GettingStarted_Yocto_Demo start */
        case R.id.button_GettingStarted_Yocto_Demo:
            intent = new Intent(this, GettingStarted_Yocto_Demo.class);
            break;
        /** generated GettingStarted_Yocto_Demo stop */
        /** generated SaveSettings start */
        case R.id.button_SaveSettings:
           intent = new Intent(this, SaveSettings.class);
           break;
        /** generated SaveSettings stop */
        /** generated ModuleControl start */
        case R.id.button_ModuleControl:
           intent = new Intent(this, ModuleControl.class);
           break;
        /** generated ModuleControl stop */
        /** generated Inventory start */
        case R.id.button_Inventory:
           intent = new Intent(this, Inventory.class);
           break;
        /** generated Inventory stop */
        /** generated stop */

        default:
            Log.e("Doc_Example", "no handler for button " + view.toString());
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

}
