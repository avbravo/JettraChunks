package io.jettra.fs.chunks;

import java.io.*;
import java.nio.file.*;
import java.util.zip.GZIPOutputStream;
import java.util.zip.GZIPInputStream;

public class ChunkManager {
    public static final int CHUNK_SIZE = (int) (1.5 * 1024 * 1024); // 1.5MB por trozo

    public static byte[] compress(byte[] data) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (GZIPOutputStream gzos = new GZIPOutputStream(baos)) {
            gzos.write(data);
        }
        return baos.toByteArray();
    }

    public static byte[] decompress(byte[] compressedData) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(compressedData);
        try (GZIPInputStream gzis = new GZIPInputStream(bais);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int len;
            while ((len = gzis.read(buffer)) > 0) {
                baos.write(buffer, 0, len);
            }
            return baos.toByteArray();
        }
    }

    // Método para obtener el número total de trozos de un archivo
    public static int calculateTotalChunks(long fileSize) {
        return (int) Math.ceil((double) fileSize / CHUNK_SIZE);
    }

    public static void splitFile(File src, File destDir) throws IOException {
        if (!destDir.exists()) destDir.mkdirs();
        long size = src.length();
        int chunks = calculateTotalChunks(size);
        try (RandomAccessFile raf = new RandomAccessFile(src, "r")) {
            for (int i = 0; i < chunks; i++) {
                long pos = (long) i * CHUNK_SIZE;
                int len = (int) Math.min(CHUNK_SIZE, size - pos);
                byte[] buffer = new byte[len];
                raf.seek(pos);
                raf.readFully(buffer);
                File chunkFile = new File(destDir, "chunk_" + i + ".jtra");
                try (FileOutputStream fos = new FileOutputStream(chunkFile)) {
                    fos.write(buffer);
                }
            }
        }
    }

    public static void mergeFiles(File dest, File srcDir, int totalChunks) throws IOException {
        if (dest.getParentFile() != null) dest.getParentFile().mkdirs();
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            for (int i = 0; i < totalChunks; i++) {
                File chunkFile = new File(srcDir, "chunk_" + i + ".jtra");
                if (!chunkFile.exists()) throw new FileNotFoundException("Falta trozo: " + chunkFile.getName());
                Files.copy(chunkFile.toPath(), fos);
            }
        }
    }
}
