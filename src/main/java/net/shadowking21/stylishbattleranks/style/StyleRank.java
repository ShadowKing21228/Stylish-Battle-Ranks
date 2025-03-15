package net.shadowking21.stylishbattleranks.style;

import java.util.Objects;

public class StyleRank
{
public enum StyleRankEnum {
    NOTHING,
    D,
    C,
    B,
    A,
    S,
    SS,
    SSS
    }
    public static StyleRankEnum valueOf(String string)
    {
        if (Objects.equals(StyleRankEnum.D.name(), string)) {
            return StyleRankEnum.D;
        } else if (Objects.equals(StyleRankEnum.C.name(), string)) {
            return StyleRankEnum.C;
        } else if (Objects.equals(StyleRankEnum.B.name(), string)) {
            return StyleRankEnum.B;
        } else if (Objects.equals(StyleRankEnum.A.name(), string)) {
            return StyleRankEnum.A;
        } else if (Objects.equals(StyleRankEnum.S.name(), string)) {
            return StyleRankEnum.S;
        } else if (Objects.equals(StyleRankEnum.SS.name(), string)) {
            return StyleRankEnum.SS;
        } else if (Objects.equals(StyleRankEnum.SSS.name(), string)) {
            return StyleRankEnum.SSS;
        }
        return StyleRankEnum.NOTHING;
    }
}
