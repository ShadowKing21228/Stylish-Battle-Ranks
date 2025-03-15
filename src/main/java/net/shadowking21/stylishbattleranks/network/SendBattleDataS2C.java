package net.shadowking21.stylishbattleranks.network;

import dev.architectury.networking.NetworkManager;
import dev.architectury.networking.simple.BaseS2CMessage;
import dev.architectury.networking.simple.MessageType;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.shadowking21.stylishbattleranks.SupportCombatCapability;
import net.shadowking21.stylishbattleranks.style.Style;

import net.shadowking21.stylishbattleranks.style.StyleRank;

public class SendBattleDataS2C extends BaseS2CMessage {
    //private BoundedList<Object> latestTriggersList;
    private final int styleScore;
    private final StyleRank.StyleRankEnum styleRank;
    private final boolean inCombat;
    private final int outCombatTimer;
    private final int inCombatTime;
    private final boolean styleWork;

    public SendBattleDataS2C(Style style, boolean inCombat, int outCombatTimer, int inCombatTime, boolean styleWork) {
        //this.latestTriggersList = latestTriggersList;
        this.styleScore = style.getStyleScore();
        this.styleRank = style.getStyleRank();
        this.inCombat = inCombat;
        this.outCombatTimer = outCombatTimer;
        this.inCombatTime = inCombatTime;
        this.styleWork = styleWork;
    }

    public SendBattleDataS2C(FriendlyByteBuf buf) {
        //this.latestTriggersList = buf.readWithCodec(BoundedList.CODEC); // Сериализация BoundedList
        this.styleScore = buf.readInt();
        this.styleRank = buf.readEnum(StyleRank.StyleRankEnum.class);
        this.inCombat = buf.readBoolean();
        this.outCombatTimer = buf.readInt();
        this.inCombatTime = buf.readInt();
        this.styleWork = buf.readBoolean();
    }

    @Override
    public MessageType getType() {
        return ModNetwork.SYNCBATTLEDATA;
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        //buf.writeWithCodec(BoundedList.CODEC, latestTriggersList); // Десериализация BoundedList
        buf.writeInt(styleScore);
        buf.writeEnum(styleRank);
        buf.writeBoolean(inCombat);
        buf.writeInt(outCombatTimer);
        buf.writeInt(inCombatTime);
        buf.writeBoolean(styleWork);
    }

    @Override
    public void handle(NetworkManager.PacketContext context) {
        context.queue(() -> {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().player.getCapability(SupportCombatCapability.COMBAT_CAPABILITY).ifPresent(iCombatCapability ->
                {
                    iCombatCapability.getStyle().setStyleRank(styleRank);
                    iCombatCapability.getStyle().setStyleScore(styleScore);
                    iCombatCapability.setInCombatTime(inCombatTime);
                    iCombatCapability.setInCombat(inCombat);
                    iCombatCapability.setStyleWork(styleWork);
                    iCombatCapability.setOutCombatTimer(outCombatTimer);
                });
            }
        });
    }
}
