/*
 *
 *  $Id: Inventory.java 32625 2018-10-10 13:27:32Z seb $
 *
 *  Doc-Inventory example
 *
 *  You can find more information on our web site:
 *   Android API Reference:
 *      https://www.yoctopuce.com/EN/doc/reference/yoctolib-android-EN.html
 *
 */

package com.yoctopuce.doc_examples;

import android.app.Activity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yoctopuce.YoctoAPI.YAPI;
import com.yoctopuce.YoctoAPI.YAPI_Exception;
import com.yoctopuce.YoctoAPI.YModule;

public class InventoryNetwork extends Activity
{

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventory_network);
    }

    public void refreshInventory(View view)
    {
        EditText editText = findViewById(R.id.urlEdit);
        LinearLayout layout = findViewById(R.id.inventoryList);
        layout.removeAllViews();
        String url = editText.getText().toString();
        new Thread(new Runnable()
        {
            public void run()
            {

                int res;
                try {
                    res = YAPI.TestHub(url, 1000);
                } catch (YAPI_Exception e) {
                    res = e.errorType;
                }
                if (res == YAPI.SSL_UNK_CERT) {
                    // remote TLS certificate is unknown ask user what to do
                    pushItemOnUI(layout, "Remote SSL/TLS certificate is unknown...");
                    YAPI.SetNetworkSecurityOptions(YAPI.NO_HOSTNAME_CHECK | YAPI.NO_TRUSTED_CA_CHECK |
                            YAPI.NO_EXPIRATION_CHECK);
                }
                try {
                    YAPI.RegisterHub(url);
                    YAPI.UpdateDeviceList();
                    YModule module = YModule.FirstModule();
                    while (module != null) {
                        String line = module.get_serialNumber() + " (" + module.get_productName() + ")";
                        pushItemOnUI(layout, line);
                        module = module.nextModule();
                    }
                } catch (YAPI_Exception e) {
                    pushItemOnUI(layout, e.getLocalizedMessage());
                    e.printStackTrace();
                }
                YAPI.FreeAPI();
                // A potentially time consuming task.
            }
        }).start();
    }

    private void pushItemOnUI(LinearLayout layout, String line)
    {
        TextView tx = new TextView(getApplicationContext());
        tx.setText(line);
        tx.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        layout.post(new Runnable()
        {
            public void run()
            {
                layout.addView(tx);
            }
        });
    }


    @Override
    protected void onStop()
    {
        super.onStop();
    }

}
