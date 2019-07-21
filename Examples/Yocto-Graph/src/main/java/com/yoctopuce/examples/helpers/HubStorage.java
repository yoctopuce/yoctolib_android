package com.yoctopuce.examples.helpers;

import java.util.List;
import java.util.UUID;

public interface HubStorage
{
    Hub getUsbPseudoHub();

    List<Hub> getHubs();

    void addNewHub(Hub hub);

    boolean updateHub(Hub hub);

    Hub getHub(UUID uuid);

    boolean remove(UUID uuid);

    boolean useUSB();

    void setUseUSB(boolean use);
}
