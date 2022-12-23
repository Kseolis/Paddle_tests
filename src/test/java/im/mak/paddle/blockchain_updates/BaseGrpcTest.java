package im.mak.paddle.blockchain_updates;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;

import static im.mak.paddle.Node.*;

@Execution(ExecutionMode.SAME_THREAD)
public class BaseGrpcTest {
    private static final long devNetChainId = 68;
    private static final long dockerChainId = 82;

    protected int height;
    protected int fromHeight;
    protected int toHeight;
    public static final long CHAIN_ID = dockerChainId;

    protected final Channel CHANNEL = ManagedChannelBuilder
            .forAddress(dockerGRPCAddress, dockerGRPCPort)
            .usePlaintext()
            .build();
}
