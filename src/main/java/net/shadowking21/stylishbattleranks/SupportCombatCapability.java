package net.shadowking21.stylishbattleranks;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.shadowking21.stylishbattleranks.capability.ICombatCapability;

public class SupportCombatCapability {
    public static final Capability<ICombatCapability> COMBAT_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {
    });
    public static void init() {

    }
}
