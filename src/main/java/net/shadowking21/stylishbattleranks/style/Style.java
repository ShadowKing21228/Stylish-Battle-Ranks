package net.shadowking21.stylishbattleranks.style;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class Style implements INBTSerializable<CompoundTag> {
    private int styleScore = 0;
    private StyleRank.StyleRankEnum styleRank = StyleRank.StyleRankEnum.NOTHING;
    public StyleRank.StyleRankEnum getStyleRank() {
        return styleRank;
    }
    public int getStyleScore() {
        return styleScore;
    }
    public void setStyleRank(StyleRank.StyleRankEnum styleRank) {
        this.styleRank = styleRank;
    }
    public void setStyleScore(int styleScore) {
        this.styleScore = styleScore;
    }
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag compoundTag = new CompoundTag();
        compoundTag.putString("style", styleRank.name());
        compoundTag.putInt("style", styleScore);
        return compoundTag;
    }
    @Override
    public void deserializeNBT(CompoundTag nbt) {
        styleRank = StyleRank.valueOf(nbt.getString("style"));
        styleScore = nbt.getInt("style");
    }
}