package im.mak.paddle.blockchain_updates.get_block_update_range_tests;

import com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdates.GetBlockUpdatesRangeRequest;
import com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdates.GetBlockUpdatesRangeResponse;

import com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdatesApiGrpc.BlockchainUpdatesApiBlockingStub;
import im.mak.paddle.blockchain_updates.BaseSubscribeTest;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdatesApiGrpc.newBlockingStub;

class GetBlockUpdatesRangeSubscribeTest extends BaseSubscribeTest {
    @Test
    void getBlockUpdatesRangeTest() {
        GetBlockUpdatesRangeRequest request = GetBlockUpdatesRangeRequest
                .newBuilder()
                .setFromHeight(height - 10)
                .setToHeight(height)
                .build();

        BlockchainUpdatesApiBlockingStub stub = newBlockingStub(CHANNEL);

        GetBlockUpdatesRangeResponse response = stub.getBlockUpdatesRange(request);

        assertThat(response).isNotNull();
    }
}