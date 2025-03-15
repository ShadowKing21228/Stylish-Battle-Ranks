package net.shadowking21.stylishbattleranks.capability;

import net.shadowking21.stylishbattleranks.style.Style;
import net.shadowking21.stylishbattleranks.utils.BoundedList;

public interface ICombatCapability {
    BoundedList<Object> getLatestTriggersList();
    void setLatestTriggersList(BoundedList<Object> triggersList);
    Style getStyle();
    void setStyle(Style style);
    boolean isInCombat();
    void setInCombat(boolean inCombat);
    int getOutCombatTimer();
    void setOutCombatTimer(int outCombatTimer);
    int getInCombatTime();
    void setInCombatTime(int inCombatTimee);
    boolean getStyleWork();
    void setStyleWork(boolean styleWork);
    void tick();
}