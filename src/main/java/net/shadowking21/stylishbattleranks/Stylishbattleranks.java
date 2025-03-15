package net.shadowking21.stylishbattleranks;

import com.mojang.logging.LogUtils;
import dev.architectury.event.events.common.CommandRegistrationEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.shadowking21.stylishbattleranks.capability.CapabilityHandler;
import net.shadowking21.stylishbattleranks.commands.SBRCommands;
import net.shadowking21.stylishbattleranks.config.SBRConfig;
import net.shadowking21.stylishbattleranks.config.SBRFileConfig;
import net.shadowking21.stylishbattleranks.events.BattleEvents;
import net.shadowking21.stylishbattleranks.network.ModNetwork;
import org.slf4j.Logger;


@Mod(Stylishbattleranks.MODID)
public class Stylishbattleranks {


    public static final String MODID = "stylishbattleranks";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final ResourceLocation COMBAT_CAPABILITY_LOCATION = new ResourceLocation(Stylishbattleranks.MODID, "combat");
    public Stylishbattleranks() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new BattleEvents());
        MinecraftForge.EVENT_BUS.register(CapabilityHandler.class);
        ModNetwork.init();
        SupportCombatCapability.init();
        SBRFileConfig.createDirectories();
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, SBRConfig.SPEC);
        CommandRegistrationEvent.EVENT.register(SBRCommands::registerCommands);
    }
    private void commonSetup(final FMLCommonSetupEvent event) {

    }
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {

    }
}