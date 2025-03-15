package net.shadowking21.stylishbattleranks.network;

import dev.architectury.networking.simple.MessageType;
import dev.architectury.networking.simple.SimpleNetworkManager;
import net.shadowking21.stylishbattleranks.Stylishbattleranks;

public interface ModNetwork {
    SimpleNetworkManager NETWORK_MANAGER = SimpleNetworkManager.create(Stylishbattleranks.MODID);
    MessageType SYNCBATTLEDATA = NETWORK_MANAGER.registerS2C("battledata", SendBattleDataS2C::new);
    static void init()
    {

    }
}

