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
        defValues.add("usb");
        Set<String> hub_urls = _sharedPreferences.getStringSet(HUB_LIST, defValues);
        ArrayList<Hub> hubs = new ArrayList<>();
        if (hub_urls != null) {
            for (String url : hub_urls) {
                hubs.add(new Hub(url));
            }
        }
        return hubs;
    }

    @Override
    public void addNewHub(Hub hub)
    {

    }

    @Override
    public int updateHub(Hub hub)
    {
        return 0;
    }

    @Override
    public Hub getHub(UUID uuid)
    {
        //fixme: impremetn uuid setup
        return null;
    }
}
