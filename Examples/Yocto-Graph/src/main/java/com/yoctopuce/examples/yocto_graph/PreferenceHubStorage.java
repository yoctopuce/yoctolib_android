package com.yoctopuce.examples.yocto_graph;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.yoctopuce.examples.helpers.Hub;
import com.yoctopuce.examples.helpers.HubStorage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PreferenceHubStorage implements HubStorage
{

    private static final String HUB_LIST = "HUB_LIST";
    private static final String USB_USB_PORT = "USE_USB_PORT";
    private static PreferenceHubStorage __instance = null;
    private final SharedPreferences _sharedPreferences;

    static public PreferenceHubStorage Get(Context context)
    {
        if (__instance == null) {
            __instance = new PreferenceHubStorage(context);
        }
        return __instance;
    }

    private PreferenceHubStorage(Context context)
    {
        _sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

    }


    @Override
    public Hub getUsbPseudoHub()
    {
        return null;
    }

    @Override
    public List<Hub> getHubs()
    {
        HashSet<String> defValues = new HashSet<>();
        Set<String> hub_urls = _sharedPreferences.getStringSet(HUB_LIST, defValues);
        ArrayList<Hub> hubs = new ArrayList<>();
        if (hub_urls != null) {
            for (String uuid_url : hub_urls) {
                hubs.add(new Hub(uuid_url));
            }
        }
        return hubs;
    }

    @Override
    public void addNewHub(Hub hub)
    {
        HashSet<String> defValues = new HashSet<>();
        Set<String> hub_urls = _sharedPreferences.getStringSet(HUB_LIST, defValues);
        hub_urls.add(hub.toString());
        _sharedPreferences.edit()
                .clear()
                .putStringSet(HUB_LIST, hub_urls)
                .apply();
    }

    @Override
    public boolean updateHub(Hub hub)
    {
        String uuid_str = hub.getUuid().toString();
        HashSet<String> defValues = new HashSet<>();
        Set<String> hub_urls = _sharedPreferences.getStringSet(HUB_LIST, defValues);
        if (hub_urls != null) {
            for (String uuid_url : hub_urls) {
                if (uuid_url.startsWith(uuid_str)){
                    hub_urls.remove(uuid_url);
                    hub_urls.add(hub.toString());
                    _sharedPreferences.edit()
                            .clear()
                            .putStringSet(HUB_LIST, hub_urls)
                            .apply();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Hub getHub(UUID uuid)
    {
        List<Hub> hubs = getHubs();
        for (Hub hub : hubs) {
            if (hub.getUuid().equals(uuid)) {
                return hub;
            }
        }
        return null;
    }

    @Override
    public boolean remove(UUID uuid)
    {
        String uuid_str = uuid.toString();
        HashSet<String> defValues = new HashSet<>();
        Set<String> hub_urls = _sharedPreferences.getStringSet(HUB_LIST, defValues);
        if (hub_urls != null) {
            for (String uuid_url : hub_urls) {
                if (uuid_url.startsWith(uuid_str)){
                    hub_urls.remove(uuid_url);
                    _sharedPreferences.edit()
                            .clear()
                            .putStringSet(HUB_LIST, hub_urls)
                            .apply();
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean useUSB()
    {
        return _sharedPreferences.getBoolean(USB_USB_PORT,true);
    }

    @Override
    public void setUseUSB(boolean use)
    {
        _sharedPreferences.edit()
                .putBoolean(USB_USB_PORT, use)
                .apply();
    }
}
