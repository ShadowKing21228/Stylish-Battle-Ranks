package net.shadowking21.stylishbattleranks.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.shadowking21.stylishbattleranks.SupportCombatCapability;
import net.shadowking21.stylishbattleranks.utils.BoundedList;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class CapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    private final ICombatCapability instance = new CombatCapability();
    private final LazyOptional<ICombatCapability> COMBAT_CAPABILITY = LazyOptional.of(() -> instance);

    @Override
    public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
        if (cap == SupportCombatCapability.COMBAT_CAPABILITY) {
            return COMBAT_CAPABILITY.cast();
        }
        return null;
    }
    public <T> void invalidateCaps(@NotNull Capability<T> cap){
        if(cap == SupportCombatCapability.COMBAT_CAPABILITY) {
            COMBAT_CAPABILITY.invalidate();
        }
    }
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        //tag.putBoolean("isInCombat", instance.isInCombat());
        //tag.putInt("outCombatTimer", instance.getOutCombatTimer());
        tag.putInt("inCombatTime", instance.getInCombatTime());
        //tag.putBoolean("styleWork", instance.getStyleWork());
        tag.put("style", instance.getStyle().serializeNBT());
        //ListTag listTag = new ListTag();
        //for (int i = 0; i < instance.getLatestTriggersList().size(); i++) {
        //    listTag.add((instance.getLatestTriggersList().get(i)));
        //}
        //tag.put("latestTriggerList", listTag);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        //instance.setInCombat(nbt.getBoolean("InCombat"));
        //instance.setOutCombatTimer(nbt.getInt("outCombatTimer"));
        instance.setInCombatTime(nbt.getInt("inCombatTime"));
        //instance.setStyleWork(nbt.getBoolean("styleWork"));
        instance.getStyle().deserializeNBT(nbt); // "style"
        //BoundedList<String> list = new BoundedList<>(5);
        //for (Tag tag : ((ListTag) Objects.requireNonNull(nbt.get("latestTriggerList"))))
        //{
        //    if (tag instanceof StringTag stringTag)
        //    {
        //        list.add(stringTag.getAsString());
        //    }
        //}
        //instance.setLatestTriggersList(list);
    }
}

