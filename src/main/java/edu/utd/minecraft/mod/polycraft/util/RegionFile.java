package edu.utd.minecraft.mod.polycraft.util;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by jmcandrew on 7/14/15.
 */
public class RegionFile {

    public static String chunkDelimeter = ",";

    public static String getDirFromWorld(final String worldDir) {
        return worldDir + File.separator + "region";
    }

    public static Map<String, RegionFile> find(final String worldDir, final Date lastModifiedMin) {
        final Map<String, RegionFile> regionFiles = new HashMap<String, RegionFile>();
        for (final File regionFileRaw : new File(getDirFromWorld(worldDir)).listFiles(new FileFilter() {
			@Override
			public boolean accept(final File pathname) {
				return ((lastModifiedMin == null) || pathname.lastModified() > lastModifiedMin.getTime()) && pathname.getName().endsWith("mca");
			}})) {
            final RegionFile regionFile = new RegionFile(regionFileRaw);
            regionFiles.put(regionFile.getIdentifier(), regionFile);
        }
        return regionFiles;
    }

    public static String getChunkIdentifier(final int chunkX, final int chunkZ) {
        return String.format("%d%s%d", chunkX, chunkDelimeter, chunkZ);
    }

    public static String getIdentifierForChunk(final int chunkX, final int chunkZ) {
        return getIdentifier((int) Math.floor(chunkX / 32d), (int) Math.floor(chunkZ / 32d));
    }

    public static String getIdentifier(final int x, final int z) {
        return String.format("%d,%d", x, z);
    }

    public static int[] parseIdentifier(final String identifier) {
        final String[] parts = identifier.split(",");
        return new int[] { Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) };
    }

    private static final int chunksPerRow = 32;
    private static final int chunkCount = chunksPerRow * chunksPerRow;

    public final File file;
    public final int x, z;

    public RegionFile(final String dir, final int x, final int z) {
        this.file = new File(dir + File.separator + String.format("r.%d.%d.mca", x, z));
        this.x = x;
        this.z = z;
    }

    public RegionFile(final File file) {
        this.file = file;
        final String fileName = file.getName();
        final int regionXTermIndex = fileName.indexOf(".", 2);
        this.x = Integer.parseInt(fileName.substring(2, regionXTermIndex));
        this.z = Integer.parseInt(fileName.substring(regionXTermIndex + 1, fileName.indexOf(".", regionXTermIndex + 1)));
    }

    public String getIdentifier() {
        return getIdentifier(x, z);
    }

    public byte[] read() throws IOException {
        if (file.exists()) {
            return Files.readAllBytes(file.toPath());
        }
        return null;
    }

    public int write(final byte[] bytes) throws IOException {
        System.out.println(String.format("Writing region file (%s): %s", Format.humanReadableByteCount(bytes.length, false), file.getAbsolutePath()));
        Files.write(file.toPath(), bytes, StandardOpenOption.CREATE);
        return bytes.length;
    }

    public static byte[] merge(
            final int regionX, final int regionY,
            final byte[] currentBytes, final byte[] newBytes, final Set<String> newChunks,
            boolean verbose) throws IOException {
        final Processor mergeWriter = new Processor();
        final Processor currentReader = new Processor(currentBytes);
        final Processor newReader = new Processor(newBytes);

        System.out.println("Merging " + newChunks.size() + " chunk(s) for region " + getIdentifier(regionX, regionY));
        final int chunkOffsetX = regionX * chunksPerRow;
        final int chunkOffsetZ = regionY * chunksPerRow;
        int currentChunkDataOffset = 2;
        for (int x = 0; x < chunksPerRow; x++) {
            for (int z = 0; z < chunksPerRow; z++) {
                final int chunkX = x + chunkOffsetX;
                final int chunkZ = z + chunkOffsetZ;
                final int chunkIndex = Processor.getChunkIndex(chunkX, chunkZ);

                boolean newSource = newChunks.contains(getChunkIdentifier(chunkX, chunkZ));
                final Processor sourceReader = newSource ? newReader : currentReader;
                if (sourceReader.bytes != null) {
                    final int chunkOffset = sourceReader.getChunkLocationOffset(chunkIndex);
                    if (chunkOffset > 0) {
                        if (verbose) {
                            System.out.println(String.format("Chunk: %d\tCoords: (%d, %d)\tSource: %s\tOffset: %d\tSectors: %d\tLength: %d", chunkIndex, chunkX, chunkZ,
                                    newSource ? "New" : "Current", chunkOffset, sourceReader.getChunkLocationSectorCount(chunkIndex), sourceReader.getChunkDataLength(chunkOffset)));
                        }
                        final int chunkSectorCount = sourceReader.getChunkLocationSectorCount(chunkIndex);
                        mergeWriter.putChunkLocation(chunkIndex, currentChunkDataOffset, chunkSectorCount);
                        mergeWriter.putChunkTimestamp(chunkIndex, sourceReader.getChunkTimestamp(chunkIndex));
                        final int chunkDataLength = sourceReader.getChunkDataLength(chunkOffset);
                        mergeWriter.putChunkData(currentChunkDataOffset,
                                chunkDataLength, sourceReader.getChunkDataCompressionFormat(chunkOffset),
                                sourceReader.bytes, Processor.getChunkOffsetIndex(chunkOffset));
                        currentChunkDataOffset += chunkSectorCount;
                    }
                }
            }
        }

        return Arrays.copyOf(mergeWriter.bytes, Processor.getChunkOffsetIndex(currentChunkDataOffset));
    }

    //http://minecraft.gamepedia.com/Region_file_format
    private static class Processor {
        protected static final int maxSize = 1024 * 1024 * 1024;

        public static int getChunkIndex(final int x, final int z) {
            return (x & (chunksPerRow - 1)) + (z & (chunksPerRow - 1)) * chunksPerRow;
        }

        public static int getChunkOffsetIndex(final int offset) {
            return offset * 4096;
        }

        public final byte[] bytes;
        protected final ByteBuffer byteBuffer;

        protected Processor(final byte[] bytes) {
            this.bytes = bytes;
            this.byteBuffer = bytes == null ? null : ByteBuffer.wrap(bytes);
        }

        protected Processor() {
            this(new byte[maxSize]);
        }

        public int getChunkLocation(final int chunkIndex) {
            return byteBuffer.getInt(chunkIndex * 4);
        }

        public int getChunkLocationOffset(final int chunkIndex) {
            return getChunkLocation(chunkIndex) >> 8;
        }

        public int getChunkLocationSectorCount(final int chunkIndex) {
            return byteBuffer.get((chunkIndex * 4) + 3);
        }

        public void putChunkLocation(final int chunkIndex, final int offset, final int sectorCount) {
            byteBuffer.putInt(chunkIndex * 4, (offset << 8) | sectorCount);
        }

        public int getChunkTimestamp(final int chunkIndex) {
            return byteBuffer.getInt((chunkIndex * 4) + (chunkCount * 4));
        }

        public void putChunkTimestamp(final int chunkIndex, final int timestamp) {
            byteBuffer.putInt((chunkIndex * 4) + (chunkCount * 4), timestamp);
        }

        public int getChunkDataLength(final int offset) {
            return byteBuffer.getInt(getChunkOffsetIndex(offset));
        }

        public int getChunkDataCompressionFormat(final int offset) {
            return byteBuffer.get(getChunkOffsetIndex(offset) + 4);
        }

        public void putChunkData(final int targetOffset, final int length, final int compressionFormat, final byte[] data, final int sourceIndex) {
            final int targetIndex = getChunkOffsetIndex(targetOffset);
            byteBuffer.putInt(targetIndex, length);
            byteBuffer.put(targetIndex + 4, (byte) compressionFormat);
            for (int i = 0; i < length - 1; i++) {
                byteBuffer.put(targetIndex + 5 + i, data[sourceIndex + 5 + i]);
            }
        }
    }
}
