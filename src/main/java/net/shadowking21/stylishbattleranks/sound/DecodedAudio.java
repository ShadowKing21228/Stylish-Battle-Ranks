package net.shadowking21.stylishbattleranks.sound;

import java.nio.ShortBuffer;

public record DecodedAudio(ShortBuffer pcm, int channels, int sampleRate) {
}
