package net.shadowking21.stylishbattleranks.api;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.shadowking21.stylishbattleranks.SupportCombatCapability;
import net.shadowking21.stylishbattleranks.network.SendBattleDataS2C;
import net.shadowking21.stylishbattleranks.utils.BattleUtils;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.atomic.AtomicBoolean;

public class StylishBattle {
    public static boolean isStyleWork(Player player)
    {
        AtomicBoolean atomicBoolean = new AtomicBoolean();
        atomicBoolean.set(false);
        player.getCapability(SupportCombatCapability.COMBAT_CAPABILITY).ifPresent(iCombatCapability -> {
            atomicBoolean.set(iCombatCapability.getStyleWork());
        });
        return atomicBoolean.get();
    }
    public static void setInBattle(Player player)
    {
        player.getCapability(SupportCombatCapability.COMBAT_CAPABILITY).ifPresent(combat -> {
            combat.setInCombat(true);
            combat.setOutCombatTimer(30);
        });
    }
    public static boolean isPlayerInBattle(Player player)
    {
        AtomicBoolean isInCombat = new AtomicBoolean(false);
        player.getCapability(SupportCombatCapability.COMBAT_CAPABILITY).ifPresent(iCombatCapability -> {
            isInCombat.set(iCombatCapability.isInCombat());
        });
        return isInCombat.get();
    }
    /**
     * The handler of the player's actions.
     * It is designed so that the player needs to mix different methods of getting points (Stylish triggers), since the same actions will not work.
     * For convenience, it accepts an Object, but it is important to remember that if you want to put an object there,
     * make sure that repeating the Style Trigger returns the same objects so as not to "break" the handler.
     *
     * @param element - Any data that can be used to identify a specific trigger.
     * @param player who will do a Stylish Trigger.
     * @param score awarded to the player.
     */
    public static void playerActionHandler(@NotNull Object element, Player player, int score) {
        player.getCapability(SupportCombatCapability.COMBAT_CAPABILITY).ifPresent(iCombatCapability ->
        {
            if (!BattleUtils.elementCompare(player, element))
            {
                iCombatCapability.getStyle().setStyleScore(iCombatCapability.getStyle().getStyleScore()+score);
                iCombatCapability.getLatestTriggersList().add(element);
            }
        });
    }
    /**
     * The handler of the player's actions.
     * If you want to add the usual Stylish Triggers (about them in the method above), then use the upper method.
     * This method is designed for triggers that will be incredibly difficult to repeat, so that the player will not be able to abuse it in any way. Use it carefully.
     *
     * @param player who will do a Stylish Trigger.
     * @param score awarded to the player.
     */
    public static void playerActionHandler(Player player, int score) {
        player.getCapability(SupportCombatCapability.COMBAT_CAPABILITY).ifPresent(iCombatCapability ->
                iCombatCapability.getStyle().setStyleScore(iCombatCapability.getStyle().getStyleScore()+score));
    }
    /**
     * Data packet synchronize. Use if you need immediately sync with client and server.
     * You should not use it for nothing, because the player who is already in battle syncs his data every second.
     *
     * @param player who will receive a package of his combat data.
     */
    public static void syncBattleData(ServerPlayer player)
    {
        player.getCapability(SupportCombatCapability.COMBAT_CAPABILITY).ifPresent(iCombatCapability ->
        {
            new SendBattleDataS2C(iCombatCapability.getStyle(), iCombatCapability.isInCombat(), iCombatCapability.getOutCombatTimer(), iCombatCapability.getInCombatTime(), iCombatCapability.getStyleWork()).sendTo(player);
        });
    }
}
