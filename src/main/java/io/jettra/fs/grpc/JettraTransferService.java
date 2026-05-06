package io.jettra.fs.grpc;

import io.jettra.grpc.JettraObserver;
import io.jettra.grpc.JettraService;

/**
 * Jettra-native service interface for file transfers.
 * Replaces the generated gRPC interface.
 */
public interface JettraTransferService extends JettraService {
    @Override
    default String getServiceName() { return "JettraTransferService"; }

    void sendChunk(JettraChunk chunk, JettraObserver<TransferStatus> responseObserver);
}
