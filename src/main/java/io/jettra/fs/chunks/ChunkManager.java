package io.jettra.fs.chunks;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.*;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.Inflater;
import java.util.zip.InflaterInputStream;

public class ChunkManager {
    public static final int CHUNK_SIZE = 32 * 1024 * 1024; // 32MB por trozo para velocidad sónica

    public static byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(data.length);
        Deflater deflater = new Deflater(Deflater.BEST_SPEED);
        try (DeflaterOutputStream dos = new DeflaterOutputStream(baos, deflater, 8192)) {
            dos.write(data);
        }
        return baos.toByteArray();
    }

    public static byte[] decompress(byte[] compressedData) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        Inflater inflater = new Inflater();
        try (InflaterInputStream iis = new InflaterInputStream(bais, inflater, 8192);
             ByteArrayOutputStream baos = new ByteArrayOutputStream(compressedData.length * 2)) {
            byte[] buffer = new byte[8192];
            int len;
            while ((len = iis.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();
        }
    }

    public static int calculateTotalChunks(long fileSize) {
        return (int) Math.ceil((double) fileSize / CHUNK_SIZE);
    }

    public static void splitFile(File src, File destDir) throws IOException {
        if (!destDir.exists()) destDir.mkdirs();
        long size = src.length();
        int chunks = calculateTotalChunks(size);
        
        try (FileChannel srcChannel = new FileInputStream(src).getChannel();
             java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < chunks; i++) {
                final int idx = i;
                executor.submit(() -> {
                    long pos = (long) idx * CHUNK_SIZE;
                    long len = Math.min(CHUNK_SIZE, size - pos);
                    File chunkFile = new File(destDir, "chunk_" + idx + ".jtra");
                    try (FileChannel destChannel = new FileOutputStream(chunkFile).getChannel()) {
                        srcChannel.transferTo(pos, len, destChannel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        }
    }

    public static void mergeFiles(File dest, File srcDir, int totalChunks) throws IOException {
        if (dest.getParentFile() != null) dest.getParentFile().mkdirs();
        try (FileChannel destChannel = new FileOutputStream(dest).getChannel()) {
            long pos = 0;
            for (int i = 0; i < totalChunks; i++) {
                File chunkFile = new File(srcDir, "chunk_" + i + ".jtra");
                if (!chunkFile.exists()) throw new FileNotFoundException("Falta trozo: " + chunkFile.getName());
                try (FileChannel srcChannel = new FileInputStream(chunkFile).getChannel()) {
                    long size = srcChannel.size();
                    destChannel.transferFrom(srcChannel, pos, size);
                    pos += size;
                }
            }
        }
    }
}
