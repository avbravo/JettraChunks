package io.jettra.fs.grpc;

/**
 * Jettra-native implementation of JettraChunk with builder pattern.
 */
public class JettraChunk {
    private String fileId;
    private String fileName;
    private long fileSize;
    private int totalChunks;
    private int chunkIndex;
    private byte[] data;
    private boolean isCompressed;

    public String getFileId() { return fileId; }
    public JettraChunk setFileId(String fileId) { this.fileId = fileId; return this; }
    
    public String getFileName() { return fileName; }
    public JettraChunk setFileName(String fileName) { this.fileName = fileName; return this; }
    
    public long getFileSize() { return fileSize; }
    public JettraChunk setFileSize(long fileSize) { this.fileSize = fileSize; return this; }
    
    public int getTotalChunks() { return totalChunks; }
    public JettraChunk setTotalChunks(int totalChunks) { this.totalChunks = totalChunks; return this; }
    
    public int getChunkIndex() { return chunkIndex; }
    public JettraChunk setChunkIndex(int chunkIndex) { this.chunkIndex = chunkIndex; return this; }
    
    public byte[] getData() { return data; }
    public JettraChunk setData(byte[] data) { this.data = data; return this; }
    
    public boolean getIsCompressed() { return isCompressed; }
    public JettraChunk setIsCompressed(boolean isCompressed) { this.isCompressed = isCompressed; return this; }

    public byte[] toByteArray() throws java.io.IOException {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.DataOutputStream dos = new java.io.DataOutputStream(baos);
        dos.writeUTF(fileId != null ? fileId : "");
        dos.writeUTF(fileName != null ? fileName : "");
        dos.writeLong(fileSize);
        dos.writeInt(totalChunks);
        dos.writeInt(chunkIndex);
        dos.writeBoolean(isCompressed);
        if (data != null) {
            dos.writeInt(data.length);
            dos.write(data);
        } else {
            dos.writeInt(0);
        }
        return baos.toByteArray();
    }

    public static JettraChunk fromByteArray(byte[] bytes) throws java.io.IOException {
        java.io.DataInputStream dis = new java.io.DataInputStream(new java.io.ByteArrayInputStream(bytes));
        JettraChunk chunk = new JettraChunk();
        chunk.fileId = dis.readUTF();
        chunk.fileName = dis.readUTF();
        chunk.fileSize = dis.readLong();
        chunk.totalChunks = dis.readInt();
        chunk.chunkIndex = dis.readInt();
        chunk.isCompressed = dis.readBoolean();
        int dataLen = dis.readInt();
        if (dataLen > 0) {
            chunk.data = new byte[dataLen];
            dis.readFully(chunk.data);
        }
        return chunk;
    }

    public static JettraChunk newBuilder() { return new JettraChunk(); }
    public JettraChunk build() { return this; }
}
