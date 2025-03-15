package net.shadowking21.stylishbattleranks.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.loading.FMLPaths;
import net.shadowking21.stylishbattleranks.Stylishbattleranks;
import net.shadowking21.stylishbattleranks.SupportCombatCapability;
import net.shadowking21.stylishbattleranks.api.StylishBattle;
import net.shadowking21.stylishbattleranks.network.SendBattleDataS2C;
import net.shadowking21.stylishbattleranks.style.Style;
import net.shadowking21.stylishbattleranks.style.StyleRank;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class BattleUtils {

    // Utility static variables

    public static final AtomicBoolean isMusicPlay = new AtomicBoolean();
    static {
        isMusicPlay.set(false);
    }
    public static final ResourceLocation D = new ResourceLocation(Stylishbattleranks.MODID, "textures/gui/battleranks/d.png");
    public static final ResourceLocation C = new ResourceLocation(Stylishbattleranks.MODID, "textures/gui/battleranks/c.png");
    public static final ResourceLocation B = new ResourceLocation(Stylishbattleranks.MODID, "textures/gui/battleranks/b.png");
    public static final ResourceLocation A = new ResourceLocation(Stylishbattleranks.MODID, "textures/gui/battleranks/a.png");
    public static final ResourceLocation S = new ResourceLocation(Stylishbattleranks.MODID, "textures/gui/battleranks/s.png");
    public static final ResourceLocation SS = new ResourceLocation(Stylishbattleranks.MODID, "textures/gui/battleranks/ss.png");
    public static final ResourceLocation SSS = new ResourceLocation(Stylishbattleranks.MODID, "textures/gui/battleranks/sss.png");

    public static BoundedList<Object> checkTypes = new BoundedList<>(999999999);
    static
    {
        checkTypes.add(ItemTags.SWORDS);
        checkTypes.add(ItemTags.AXES);
    }

    public static List<String> styleRankList = new ArrayList<>();
    static
    {
        styleRankList.add("NOTHING");
        styleRankList.add("D");
        styleRankList.add("C");
        styleRankList.add("B");
        styleRankList.add("A");
        styleRankList.add("S");
        styleRankList.add("SS");
        styleRankList.add("SSS");
    }
    public static Path MusicPath = Paths.get(FMLPaths.GAMEDIR.get().toAbsolutePath().toString(), "config", "stylishbattleranks", "music");
    static {MusicPath = MusicPath.normalize();}

    // Methods

    public static boolean elementCompare(Player player, @NotNull Object element)
    {
        AtomicBoolean itHasInList = new AtomicBoolean(false);
        player.getCapability(SupportCombatCapability.COMBAT_CAPABILITY).ifPresent(iCombatCapability ->
        {
            for (int i = 0; i < iCombatCapability.getLatestTriggersList().size(); i++) {
                Object listElement = iCombatCapability.getLatestTriggersList().get(i);
                if (element.getClass().equals(listElement.getClass()) && Objects.equals(element, listElement)) {
                    itHasInList.set(true);
                    break;
                }
            }
        });
        return itHasInList.get();
    }
    public static boolean elementCompare(@NotNull Object element)
    {
        AtomicBoolean itHasInList = new AtomicBoolean(false);
        for (int i = 0; i < checkTypes.size(); i++) {
            Object listElement = checkTypes.get(i);
            if (element.getClass().equals(listElement.getClass()) && Objects.equals(element, listElement)) {
                itHasInList.set(true);
                break;
            }
        }
        return itHasInList.get();
    }
    public static boolean isCriticalHit(Player player) {
        return !player.onGround()
                && !player.isInWater()
                && !player.isFallFlying()
                && !player.onClimbable()
                && !player.hasEffect(MobEffects.BLINDNESS);
    }
    public static void sendChatMessage(String message) {
        Minecraft.getInstance().gui.getChat().addMessage(Component.literal(message));
    }
    public static @Nullable ResourceLocation getPlayerRank(@NotNull Player player)
    {
        AtomicInteger a = new AtomicInteger();
        player.getCapability(SupportCombatCapability.COMBAT_CAPABILITY).ifPresent(capability ->
        {
            a.set(capability.getStyle().getStyleScore());
        });
        if (convertStyleRank(a.get()) == StyleRank.StyleRankEnum.D) return D;
        if (convertStyleRank(a.get()) == StyleRank.StyleRankEnum.C) return C;
        if (convertStyleRank(a.get()) == StyleRank.StyleRankEnum.B) return B;
        if (convertStyleRank(a.get()) == StyleRank.StyleRankEnum.A) return A;
        if (convertStyleRank(a.get()) == StyleRank.StyleRankEnum.S) return S;
        if (convertStyleRank(a.get()) == StyleRank.StyleRankEnum.SS) return SS;
        if (convertStyleRank(a.get()) == StyleRank.StyleRankEnum.SSS) return SSS;
        return null;
    }
    public static StyleRank.StyleRankEnum convertStyleRank(int combatScore) {
        if (combatScore > 500) {
            return StyleRank.StyleRankEnum.SSS;
        }
        if (combatScore > 350) {
            return StyleRank.StyleRankEnum.SS;
        }
        if (combatScore > 200) {
            return StyleRank.StyleRankEnum.S;
        }
        if (combatScore > 100) {
            return StyleRank.StyleRankEnum.A;
        }
        if (combatScore > 50) {
            return StyleRank.StyleRankEnum.B;
        }
        if (combatScore > 25) {
            return StyleRank.StyleRankEnum.C;
        }
        if (combatScore > 10) {
            return StyleRank.StyleRankEnum.D;
        }
        return StyleRank.StyleRankEnum.NOTHING;
    }
    private static void BattleTimer(@NotNull Player player) {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            private int seconds = 0;
            @Override
            public void run() {
                if (player.getServer().isRunning()) {
                    seconds++;
                    player.getCapability(SupportCombatCapability.COMBAT_CAPABILITY).ifPresent(combat -> {
                        combat.tick();
                        if (combat.getStyleWork()) {
                            StyleRank.StyleRankEnum styleRank = combat.getStyle().getStyleRank();
                            combat.getStyle().setStyleRank(convertStyleRank(combat.getStyle().getStyleScore()));
                            if (styleRank != combat.getStyle().getStyleRank())
                            {
                                if (player instanceof ServerPlayer serverPlayer) StylishBattle.syncBattleData(serverPlayer);
                            }
                        }
                        if (!combat.isInCombat()) {
                            timer.cancel();
                        }
                    });
                    if (player instanceof ServerPlayer serverPlayer) StylishBattle.syncBattleData(serverPlayer);
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }
    public static void BattleStart(@NotNull Player player)
    {
        var capability = player.getCapability(SupportCombatCapability.COMBAT_CAPABILITY);
        capability.ifPresent(combat ->
        {
            if (!combat.isInCombat())
            {
                combat.setStyle(new Style());
                combat.setInCombatTime(0);
                BattleTimer(player);
            }
            StylishBattle.setInBattle(player);
        });
    }
    public static float nearestPointInList(List<Float> list, float currentPoint) {
        float result = list.get(0);
        float minDifference = Math.abs(currentPoint - result);

        for (float controlPoint : list) {
            float difference = Math.abs(currentPoint - controlPoint);
            if (difference < minDifference) {
                result = controlPoint;
                minDifference = difference;
            }
        }
        return result;
    }
}