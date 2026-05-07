package io.jettra.fs.grpc;

/**
 * Jettra-native implementation of TransferStatus.
 * Replaces the generated Protobuf class with builder pattern.
 */
public class TransferStatus {
    private boolean success;
    private String message;
    private int progress;

    public boolean getSuccess() { return success; }
    public TransferStatus setSuccess(boolean success) { this.success = success; return this; }
    
    public String getMessage() { return message; }
    public TransferStatus setMessage(String message) { this.message = message; return this; }
    
    public int getProgress() { return progress; }
    public TransferStatus setProgress(int progress) { this.progress = progress; return this; }

    public byte[] toByteArray() throws java.io.IOException {
        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.DataOutputStream dos = new java.io.DataOutputStream(baos);
        dos.writeBoolean(success);
        dos.writeUTF(message != null ? message : "");
        dos.writeInt(progress);
        return baos.toByteArray();
    }

    public static TransferStatus fromByteArray(byte[] bytes) throws java.io.IOException {
        java.io.DataInputStream dis = new java.io.DataInputStream(new java.io.ByteArrayInputStream(bytes));
        TransferStatus status = new TransferStatus();
        status.success = dis.readBoolean();
        status.message = dis.readUTF();
        status.progress = dis.readInt();
        return status;
    }

    public static TransferStatus newBuilder() { return new TransferStatus(); }
    public TransferStatus build() { return this; }
}
