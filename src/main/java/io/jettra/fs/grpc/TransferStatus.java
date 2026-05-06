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

    public static TransferStatus newBuilder() { return new TransferStatus(); }
    public TransferStatus build() { return this; }
}
