package net.shadowking21.stylishbattleranks.events;

import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.ShieldBlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.shadowking21.stylishbattleranks.SupportCombatCapability;
import net.shadowking21.stylishbattleranks.api.StylishBattle;
import net.shadowking21.stylishbattleranks.utils.BattleUtils;

public class BattleEvents {

    @SubscribeEvent
    public void PlayerIsAttackBattleStart (LivingHurtEvent event)
    {
        if (event.getSource().getEntity() instanceof Player player && event.getEntity() instanceof Monster)
        {
            BattleUtils.BattleStart(player);
        }
    }
    @SubscribeEvent
    public void PlayerIsAttackHandler (LivingHurtEvent event)
    {
        if (event.getSource().getEntity() instanceof Player player && event.getEntity() instanceof Monster && StylishBattle.isPlayerInBattle(player))
        {
            if (!event.getSource().is(DamageTypes.PLAYER_ATTACK)) {
                StylishBattle.playerActionHandler(event.getSource().toString(), player, 7);
            }
            else
            {
                ItemStack weaponItem = player.getItemInHand(InteractionHand.MAIN_HAND);
                if (weaponItem == ItemStack.EMPTY)
                {
                    if (BattleUtils.isCriticalHit(player)) StylishBattle.playerActionHandler(ItemStack.EMPTY, player, 4);
                    StylishBattle.playerActionHandler(ItemStack.EMPTY, player, 2);
                }
                else {
                    for (TagKey<Item> tagKey : weaponItem.getTags().toList()) {
                        if (BattleUtils.elementCompare(tagKey)) {
                            if (BattleUtils.isCriticalHit(player)) StylishBattle.playerActionHandler(tagKey, player, 5);
                            StylishBattle.playerActionHandler(tagKey, player, 10);
                            break;
                        }
                    }
                }
            }
        }
        else if (event.getEntity() instanceof Player player) {
            if (event.getSource().getEntity() == null)
            {
                event.getEntity().getCapability(SupportCombatCapability.COMBAT_CAPABILITY).ifPresent(iCombatCapability ->
                {
                    if (iCombatCapability.getStyle().getStyleScore()-5 <= 0) iCombatCapability.getStyle().setStyleScore(0);
                    else iCombatCapability.getStyle().setStyleScore(iCombatCapability.getStyle().getStyleScore()-5);
                });
            }
            else {
                event.getEntity().getCapability(SupportCombatCapability.COMBAT_CAPABILITY).ifPresent(iCombatCapability ->
                {
                    if (iCombatCapability.getStyle().getStyleScore()-10 <= 0) iCombatCapability.getStyle().setStyleScore(0);
                    else iCombatCapability.getStyle().setStyleScore(iCombatCapability.getStyle().getStyleScore()-10);
                });
            }
        }
    }
    @SubscribeEvent
    public void PlayerIsBlocking (ShieldBlockEvent event)
    {
        if (event.getEntity() instanceof Player player)
        {
            if (StylishBattle.isPlayerInBattle(player))
            {
                StylishBattle.playerActionHandler("shieldBlock", player, 11);
            }
        }
    }
}