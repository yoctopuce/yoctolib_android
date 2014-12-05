package com.yoctopuce.examples.yocto_relay;

import java.util.UUID;

import org.json.JSONException;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.getpebble.android.kit.Constants;
import com.getpebble.android.kit.PebbleKit;
import com.getpebble.android.kit.util.PebbleDictionary;

public class PebbleInterface
{
    public final static UUID PEBBLE_APP_UUID = UUID.fromString("7D93A5D5-EA73-4192-B389-01A94242CA82");
    private final static int CMD_KEY = 0x0;
    private final static int PREVIOUS_RELAY = 0;
    private final static int TOGGLE_RELAY = 1;
    private final static int NEXT_RELAY = 2;


    public static class WatchReceiver extends BroadcastReceiver
    {
        private final static String TAG = "com.yoctopuce.examples.yocto_relay.WatchReciever";

        @Override
        public void onReceive(Context context, Intent intent)
        {
            if (intent.getAction().equals(Constants.INTENT_APP_RECEIVE)) {
                final UUID receivedUuid = (UUID) intent.getSerializableExtra(Constants.APP_UUID);

                // Pebble-enabled apps are expected to be good citizens and only
                // inspect broadcasts containing their UUID
                if (!PEBBLE_APP_UUID.equals(receivedUuid)) {
                    Log.i(TAG, "not my UUID");
                    return;
                }

                final int transactionId = intent.getIntExtra(Constants.TRANSACTION_ID, -1);
                final String jsonData = intent.getStringExtra(Constants.MSG_DATA);
                if (jsonData == null || jsonData.isEmpty()) {
                    Log.i(TAG, "jsonData null");
                    return;
                }
                int cmd;
                try {
                    final PebbleDictionary data = PebbleDictionary.fromJson(jsonData);
                    cmd = data.getUnsignedInteger(CMD_KEY).intValue();
                } catch (JSONException e) {
                    return;
                }
                Log.i(TAG, "receive command" + cmd);
                // ack the command
                PebbleKit.sendAckToPebble(context, transactionId);
                RelayListStorage singleton = RelayListStorage.get(context);
                Relay relay;
                switch(cmd){
                case PREVIOUS_RELAY:
                    singleton.pebblePrevious();
                    break;
                case TOGGLE_RELAY:
                    relay = singleton.pebbleGetCurrent();
                    if(relay==null)
                        break;
                    relay.toggle();
                    singleton.notifyChanges();
                    // send message to the service
                    Intent serviceintent = new Intent(context, YoctoService.class);
                    serviceintent.putExtra(YoctoService.EXTRA_TOGGLE, relay.getHwId());
                    context.startService(serviceintent);
                    break;
                case NEXT_RELAY:
                    singleton.pebbleNext();
                    break;
                default:
                }
                relay = singleton.pebbleGetCurrent();
                if (relay !=null) {
                    PebbleDictionary dictionary = relay.toPebbleDictionary();
                    PebbleKit.sendDataToPebble(context, PEBBLE_APP_UUID, dictionary);
                } else {
                    PebbleDictionary dictionary = Relay.getEmptyPebbleDictionary();
                    PebbleKit.sendDataToPebble(context, PEBBLE_APP_UUID, dictionary);
                }
            }
        }

    }

}
