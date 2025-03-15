package net.shadowking21.stylishbattleranks.events;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.shadowking21.stylishbattleranks.api.StylishBattle;
import net.shadowking21.stylishbattleranks.config.SBRConfig;
import net.shadowking21.stylishbattleranks.sound.BattleMusicPlayer;
import net.shadowking21.stylishbattleranks.utils.BattleUtils;
import net.sixik.v2.color.TextureColor;

import static net.shadowking21.stylishbattleranks.Stylishbattleranks.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ClientBattleEvents {

    @SubscribeEvent
    public static void onWorldIn(ClientPlayerNetworkEvent.LoggingIn event)
    {
        if (SBRConfig.selectedTrack.get().isEmpty()) {
            assert Minecraft.getInstance().player != null;
            Minecraft.getInstance().player.sendSystemMessage(Component.translatable("sbr.tracknotinstalled"));
            Minecraft.getInstance().player.sendSystemMessage(Component.translatable("sbr.tracknotinstalled2"));
        }
    }

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() == VanillaGuiOverlay.HOTBAR.type()) {
            Player player = Minecraft.getInstance().player;
            if (player != null && StylishBattle.isStyleWork(player) && BattleUtils.getPlayerRank(player) != null && StylishBattle.isPlayerInBattle(player)) {
                try {
                    TextureColor color = TextureColor.create(BattleUtils.getPlayerRank(player));
                    int width = Minecraft.getInstance().getWindow().getGuiScaledWidth();
                    int valueScaled = (int) (150 / Minecraft.getInstance().getWindow().getGuiScale());
                    color.draw(event.getGuiGraphics(), width - valueScaled - 10, (int) (valueScaled * 0.4), valueScaled, valueScaled);

                    if (!BattleUtils.isMusicPlay.get() && !SBRConfig.selectedTrack.get().isEmpty()) {
                        BattleMusicPlayer.startMusic();
                        BattleUtils.isMusicPlay.set(true);
                    }

                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
