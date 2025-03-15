package net.shadowking21.stylishbattleranks.sound;

import com.github.kokorin.jaffree.ffmpeg.*;
import com.github.kokorin.jaffree.ffprobe.FFprobe;
import com.github.kokorin.jaffree.ffprobe.FFprobeResult;
import net.minecraft.client.Minecraft;
import net.shadowking21.stylishbattleranks.SupportCombatCapability;
import net.shadowking21.stylishbattleranks.config.ConfigData;
import net.shadowking21.stylishbattleranks.config.SBRConfig;
import net.shadowking21.stylishbattleranks.config.SBRJsonReader;
import net.shadowking21.stylishbattleranks.utils.BattleUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;
import org.lwjgl.openal.AL11;
import org.lwjgl.stb.STBVorbis;
import org.lwjgl.stb.STBVorbisInfo;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

public class BattleMusicPlayer {
    private static final AtomicBoolean pauseBuffer = new AtomicBoolean(false);
    //int buffer = AL10.alGenBuffers();
    //int source = AL10.alGenSources();
    //ByteBuffer vorbisData = BufferUtils.createByteBuffer(8192);
    public static final Executor executor = Executors.newSingleThreadExecutor();
    public static void startMusic() {
        CompletableFuture.runAsync(()->
        {
            try {
                System.out.println("startMusic is active");
                Path absoluteMusicPath = BattleUtils.MusicPath;
                ByteBuffer byteData;
                DecodedAudio decodedAudio;
                ConfigData configData;
                boolean dynamicSystem = false;
                List<Float> controlPointList = null;
                Map<String, Float> rankMap = null;
                //float trackDuration;
                if (Files.exists(absoluteMusicPath.resolve(SBRConfig.selectedTrack.get() + ".flac"))) {
                    //trackDuration = getAudioDuration(absoluteMusicPath.resolve(SBRConfig.selectedTrack.get() + ".flac"));
                    byteData = loadAudioToByteBuffer(absoluteMusicPath.resolve(SBRConfig.selectedTrack.get() + ".flac"));
                    decodedAudio = decodeFlacToPCM(byteData);
                }
                else if (Files.exists(absoluteMusicPath.resolve(SBRConfig.selectedTrack.get() + ".ogg"))) {
                    //trackDuration = getAudioDuration(absoluteMusicPath.resolve(SBRConfig.selectedTrack.get() + ".ogg"));
                    byteData = loadAudioToByteBuffer(absoluteMusicPath.resolve(SBRConfig.selectedTrack.get() + ".ogg"));
                    decodedAudio = decodeOggToPCM(byteData);
                }
                else throw new FileNotFoundException(SBRConfig.selectedTrack.get() + " not found! Add track to config/stylishbattleranks/music directory");
                if (Files.exists(absoluteMusicPath.resolve(SBRConfig.selectedTrack.get() + ".json"))) {
                    configData = SBRJsonReader.read();
                    controlPointList = configData.controlpoints;
                    rankMap = configData.styleranks;
                    dynamicSystem = true;
                }
                //controlPointList.add(trackDuration);

                int sourceId = AL10.alGenSources();
                int bufferId = AL10.alGenBuffers();
                int channels = decodedAudio.channels();
                int sampleRate = decodedAudio.sampleRate();
                int format = channels == 1 ? AL10.AL_FORMAT_MONO16 : AL10.AL_FORMAT_STEREO16;

                AL10.alBufferData(bufferId, format, decodedAudio.pcm(), sampleRate);
                AL10.alSourcei(sourceId, AL10.AL_BUFFER, bufferId);
                AL10.alSourcei(sourceId, AL10.AL_LOOPING, AL10.AL_TRUE);
                AL10.alSourcef(sourceId, AL10.AL_GAIN, 0.0f);
                float stepTime = 0.1f;
                int steps = (int) (5 / stepTime);
                float maxVolume = SBRConfig.volumeValue.get().floatValue();
                float stepSize = maxVolume / steps;
                final Map<String, Float> finalRankMap = rankMap;
                final List<Float> finalControlPointList = controlPointList;
                final boolean finalDynamicSystem = dynamicSystem;
                Minecraft.getInstance().player.getCapability(SupportCombatCapability.COMBAT_CAPABILITY).ifPresent(combat -> {
                    String currentRank = combat.getStyle().getStyleRank().name();
                    if (Files.exists(absoluteMusicPath.resolve(SBRConfig.selectedTrack.get() + ".json"))) {
                        playMusicFromTime(sourceId, finalRankMap.get(currentRank));
                    } else {
                        AL10.alSourcePlay(sourceId);
                    }
                    for (int i = 1; i <= steps; i++) {
                        float volume = i * stepSize;
                        AL10.alSourcef(sourceId, AL10.AL_GAIN, volume);
                        try {
                            Thread.sleep((long) (stepTime * 1000));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            break;
                        }
                    }
                    AL10.alSourcef(sourceId, AL10.AL_GAIN, maxVolume);
                    while (combat.getStyleWork()) {
                        Minecraft.getInstance().getMusicManager().stopPlaying();
                        float currentTime = getSecond(sourceId);
                        float nearestControlPoint = BattleUtils.nearestPointInList(finalControlPointList, currentTime);
                        currentRank = combat.getStyle().getStyleRank().name();
                        if (Minecraft.getInstance().isPaused()) {
                            stopMusic(sourceId);
                            pauseBuffer.set(true);
                        } else if (pauseBuffer.get()) {
                            AL10.alSourcePlay(sourceId);
                            pauseBuffer.set(false);
                        }
                        if (finalDynamicSystem && AL10.alGetSourcei(sourceId, AL10.AL_SOURCE_STATE) == AL10.AL_PLAYING) { // If track is playing // true
                            int currentIndex = BattleUtils.styleRankList.indexOf(currentRank); //
                            if (Math.abs(nearestControlPoint - currentTime) < 0.000001f) {

                                if (currentTime <= finalRankMap.get(currentRank)) { // If current time < start time of current rank
                                    AL10.alSourcef(sourceId, AL11.AL_SEC_OFFSET, finalRankMap.get(currentRank)); // Move track to correct position
                                    //System.out.println("Forward. From " + currentTime + " to " + finalRankMap.get(currentRank));
                                }
                                else if (currentIndex + 1 < BattleUtils.styleRankList.size()) { // Check next rank
                                    float nextRankTime = finalRankMap.get(BattleUtils.styleRankList.get(currentIndex + 1));
                                    if (currentTime > nextRankTime) { // If time > next rank time → demote
                                        if (currentIndex > 0) { // Check lower bound
                                            AL10.alSourcef(sourceId, AL11.AL_SEC_OFFSET, finalRankMap.get(BattleUtils.styleRankList.get(currentIndex - 1)));
                                            //System.out.println("Back. From " + currentTime + " to " + finalRankMap.get(BattleUtils.styleRankList.get(currentIndex - 1)));
                                        }
                                    }
                                } else if (currentTime == finalRankMap.get(BattleUtils.styleRankList.get(currentIndex + 1))) { // If next rank time is reached
                                    AL10.alSourcef(sourceId, AL11.AL_SEC_OFFSET, finalRankMap.get(currentRank)); // Keep player on current rank
                                    //System.out.println("Need LOOP. From " + currentTime + " to " + finalRankMap.get(currentRank));
                                }
                            }
                            else if (currentTime == 0)
                            {
                                AL10.alSourcef(sourceId, AL11.AL_SEC_OFFSET, finalRankMap.get(currentRank));
                            }
                        }
                    }
                });
                for (int i = steps; i >= 0; i--) {
                    float volume = i * stepSize;
                    AL10.alSourcef(sourceId, AL10.AL_GAIN, volume);
                    try {
                        Thread.sleep((long) (stepTime * 1000));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }
                }
                AL10.alSourceStop(sourceId);
                AL10.alDeleteSources(sourceId);
                AL10.alDeleteBuffers(bufferId);
                BattleUtils.isMusicPlay.set(false);
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println("BattleMusicPlayer error: " + e.getMessage());
            }
        });
    }
    public static void playMusicFromTime(int sourceId, float startTime) {
        AL10.alSourcef(sourceId, AL11.AL_SEC_OFFSET, startTime);
        AL10.alSourcePlay(sourceId);
    }
    private static DecodedAudio decodeOggToPCM(ByteBuffer vorbisData) {
        IntBuffer error = BufferUtils.createIntBuffer(1);

        long decoder = STBVorbis.stb_vorbis_open_memory(vorbisData, error, null);
        if (decoder == 0L) {
            throw new RuntimeException("Failed to open OGG: " + error.get(0));
        }

        STBVorbisInfo info = STBVorbisInfo.malloc();
        STBVorbis.stb_vorbis_get_info(decoder, info);
        int channels = info.channels();;
        int sampleRate = info.sample_rate();
        int totalSamples = STBVorbis.stb_vorbis_stream_length_in_samples(decoder);

        ShortBuffer pcm = BufferUtils.createShortBuffer(totalSamples * channels);

        ShortBuffer tempBuffer = BufferUtils.createShortBuffer(4096);
        int samples;
        while ((samples = STBVorbis.stb_vorbis_get_samples_short_interleaved(decoder, channels, tempBuffer)) > 0) {
            tempBuffer.limit(samples * channels);
            pcm.put(tempBuffer);
            tempBuffer.clear();
        }

        pcm.flip();
        STBVorbis.stb_vorbis_close(decoder);
        info.free();
        return new DecodedAudio(pcm, channels, sampleRate);
    }
    public static DecodedAudio decodeFlacToPCM(ByteBuffer buffer) {

        byte[] inputBytes = new byte[buffer.remaining()];
        buffer.duplicate().get(inputBytes);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(inputBytes);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        try {
            FFmpegResult result = FFmpeg.atPath()
                    .addInput(PipeInput.pumpFrom(inputStream).setFormat("flac"))
                    .addOutput(PipeOutput.pumpTo(outputStream)
                            .setFormat("s16le")
                            .addArguments("-ac", "2")
                            .addArguments("-ar", "44100")
                    )
                    .execute();
        } catch (Exception e) {
            e.printStackTrace();
        }
        byte[] pcmData = outputStream.toByteArray();
        ByteBuffer pcmByteBuffer = ByteBuffer.wrap(pcmData);
        ShortBuffer pcmBuffer = pcmByteBuffer.asShortBuffer();

        return new DecodedAudio(pcmBuffer, 2, 44100);
    }

    private static ByteBuffer loadAudioToByteBuffer(Path path) throws IOException {
        try {
            try (InputStream inputStream = Files.newInputStream(path);
                 ReadableByteChannel channel = Channels.newChannel(inputStream)) {
                ByteBuffer buffer = BufferUtils.createByteBuffer(8192);

                while (true) {
                    int bytesRead = channel.read(buffer);
                    if (bytesRead == -1) break;
                    if (buffer.remaining() == 0) {
                        ByteBuffer newBuffer = BufferUtils.createByteBuffer(buffer.capacity() * 2);
                        buffer.flip();
                        newBuffer.put(buffer);
                        buffer = newBuffer;
                    }
                }
                buffer.flip();
                return buffer;
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            throw new IOException();
        }
    }
    //private static float getAudioDuration(Path filePath) {
    //    FFprobeResult probeResult = FFprobe.atPath()
    //            .setInput(filePath.toString())
    //            .setShowFormat(true)
    //            .execute();
    //    if (probeResult.getFormat() != null && probeResult.getFormat().getDuration() != null) {
    //        return probeResult.getFormat().getDuration();
    //    }
    //    throw new RuntimeException("Не удалось определить длительность файла: " + filePath);
    //}
    private static float getSecond(int sourceId)
    {
        return AL11.alGetSourcef(sourceId, AL11.AL_SEC_OFFSET);
    }
    private static void toSecond(int sourceId, float second)
    {
        AL10.alSourcef(sourceId, AL11.AL_SEC_OFFSET, second);
    }
    private static void stopMusic(int sourceId)
    {
        AL10.alSourcePause(sourceId);
    }
}