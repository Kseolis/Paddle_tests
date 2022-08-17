package im.mak.paddle.blockchain_updates;

import io.grpc.Channel;
import io.grpc.ManagedChannelBuilder;

public class BaseTest {
    protected int height;
    public static final long CHAIN_ID = 68;

    protected final Channel CHANNEL = ManagedChannelBuilder
            .forAddress("devnet1-htz-nbg1-3.wavesnodes.com", 6881)
            .usePlaintext()
            .build();
}
