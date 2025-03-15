package net.shadowking21.stylishbattleranks.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class SBRConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.DoubleValue volumeValue;
    public static final ForgeConfigSpec.ConfigValue<String> selectedTrack;

    static {
        BUILDER.push("General");

        volumeValue = BUILDER
                .comment("This is volume of all tracks. For example, 1.0 value it`s 100% of volume and 0.38 is 38%. \nDefault: 1.0")
                .defineInRange("cooldownValue", 1.0, 0, 1.0);

        selectedTrack = BUILDER
                .comment("Write the track that you want it to play. It must be in the *minecraft folder*/config/stylishbattleranks/music folder in .ogg or .flac format (other formats are not accepted, this may be expanded in the future).\n Also, in addition to the track, it can be stored in the same folder with the same name.a json file with playback details, in which you can understand how to make it using the example in config.\nThe value must take the name of the ogg and json files. \nDefault:\"\"")
                .define("selectedTrack", "");

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
