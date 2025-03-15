package net.shadowking21.stylishbattleranks.capability;

import net.shadowking21.stylishbattleranks.style.Style;
import net.shadowking21.stylishbattleranks.utils.BoundedList;

public class CombatCapability implements ICombatCapability {
    private boolean isInCombat = false;
    private int outCombatTimer = 0;
    private int inCombatTime = 0;
    private boolean styleWork = false;
    private Style style = new Style();
    private BoundedList<Object> latestTriggersList = new BoundedList<>(5);
    @Override
    public BoundedList<Object> getLatestTriggersList() {
        return latestTriggersList;
    }
    @Override
    public void setLatestTriggersList(BoundedList<Object> triggersList) {
        latestTriggersList = triggersList;
    }
    @Override
    public Style getStyle() {
        return style;
    }
    @Override
    public void setStyle(Style style) {
        this.style = style;
    }
    @Override
    public boolean isInCombat() {
        return isInCombat;
    }
    @Override
    public void setInCombat(boolean inCombat) {
        this.isInCombat = inCombat;
    }
    @Override
    public int getOutCombatTimer() {
        return outCombatTimer;
    }
    @Override
    public int getInCombatTime()
    {
        return inCombatTime;
    }
    @Override
    public void setInCombatTime(int inCombatTimee)
    {
        inCombatTime = inCombatTimee;
    }
    @Override
    public boolean getStyleWork() {
        return styleWork;
    }
    @Override
    public void setStyleWork(boolean styleWorkInput) {
        styleWork = styleWorkInput;
    }
    @Override
    public void setOutCombatTimer(int outCombatTimer) {
        this.outCombatTimer = outCombatTimer;
    }
    @Override
    public void tick() {
        if (outCombatTimer > 0) {
            outCombatTimer--;
            if (outCombatTimer == 0) {
                isInCombat = false;
                styleWork = false;
                latestTriggersList = new BoundedList<>(5);
            }
        }
        if (isInCombat)
        {
            inCombatTime++;
            if (inCombatTime > 30)
            {
                styleWork = true;
                if (style.getStyleScore() > 0) style.setStyleScore(style.getStyleScore() - 1);
            }
        }
    }
}