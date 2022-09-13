package im.mak.paddle.blockchain_updates.get_block_update_tests;

import com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdates.GetBlockUpdateRequest;
import com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdates.GetBlockUpdateResponse;
import com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdatesApiGrpc.BlockchainUpdatesApiBlockingStub;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;

import static com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdatesApiGrpc.newBlockingStub;

class GetBlockUpdateTest extends BaseGrpcTest {

    @Test
    void getBlockUpdateBaseTest() throws UnsupportedOperationException {
        GetBlockUpdateRequest request = GetBlockUpdateRequest
                .newBuilder()
                .setHeight(height)
                .build();

        BlockchainUpdatesApiBlockingStub stub = newBlockingStub(CHANNEL);

        GetBlockUpdateResponse response = stub.getBlockUpdate(request);

        assertThat(response).isNotNull();
    }
}
