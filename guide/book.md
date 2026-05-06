# JettraChunks: File Fragmentation & Transfer Objects

JettraChunks is a standalone library that provides the core data structures and logic for high-speed file transfer within the Jettra ecosystem. It handles fragmentation, compression, and the definition of transfer services.

## 🏗️ Architecture

JettraChunks sits between the application layer (like JettraFileSystem) and the transport layer (JettraGRPC):

1.  **Message Models**: Native POJOs (`JettraChunk`, `TransferStatus`) that represent the data sent over the wire.
2.  **Service Definition**: The `JettraTransferService` interface, which defines the contract for sending file chunks.
3.  **Data Management (`ChunkManager`)**: A utility class that handles:
    - Calculation of total chunks based on file size.
    - GZIP compression/decompression of chunk data.
    - Constant definitions for chunk sizing (default: 1MB).

## 🚀 Functionality

- **Independent Fragmentation**: Ability to split large files into manageable, identifiable chunks.
- **Integrated Compression**: Automatic GZIP support to reduce network bandwidth.
- **Service Contract**: A unified interface for receptors to implement and senders to target.
- **Native-gRPC Ready**: Designed to be serialized via `JettraGRPC` without external protobuf dependencies.

## 💻 Usage Examples

### Creating a Chunk
```java
JettraChunk chunk = JettraChunk.newBuilder()
        .setFileId("uuid-123")
        .setFileName("data.zip")
        .setChunkIndex(0)
        .setTotalChunks(10)
        .setData(compressedBytes)
        .setIsCompressed(true)
        .build();
```

### Using ChunkManager
```java
// Compress data
byte[] compressed = ChunkManager.compress(rawData);

// Calculate chunks
int total = ChunkManager.calculateTotalChunks(fileSize);
```

### Implementing the Receptor
```java
public class MyReceptor implements JettraTransferService {
    @Override
    public void sendChunk(JettraChunk chunk, JettraObserver<TransferStatus> observer) {
        // Logic to write chunk to disk...
        observer.onNext(TransferStatus.newBuilder().setSuccess(true).build());
        observer.onCompleted();
    }
}
```

## 🛠️ Build Requirements
- **Java 25**
- **JettraGRPC** (for the service interfaces)
