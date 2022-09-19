package im.mak.paddle.helpers.blockchain_updates_handlers.get_block_handlers;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdates.GetBlockUpdateRequest;
import com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdates.GetBlockUpdateResponse;
import com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdatesApiGrpc.BlockchainUpdatesApiBlockingStub;
import com.wavesplatform.events.protobuf.Events.BlockchainUpdated.Append;
import io.grpc.Channel;

import java.util.List;

import static com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdatesApiGrpc.newBlockingStub;

public class GetBlockUpdateHandler {
    private Append append;
    private String transactionId;
    private int txIndex;

    public void getBlockUpdateResponseHandler(Channel channel, List<Integer> heights, String txId) {
        for (int height : heights) {
            if (append == null) {
                GetBlockUpdateRequest request = GetBlockUpdateRequest
                        .newBuilder()
                        .setHeight(height)
                        .build();
                BlockchainUpdatesApiBlockingStub stub = newBlockingStub(channel);
                GetBlockUpdateResponse response = stub.getBlockUpdate(request);
                append = searchingForTransactionInBlock(response, txId);
            }
        }
    }

    public Append getAppend() {
        return append;
    }

    public String getTransactionId() {
        return transactionId;
    }

    private Append searchingForTransactionInBlock(GetBlockUpdateResponse response, String txId) {
        Append tempAppend = response.getUpdate().getAppend();
        long txIdsCount = tempAppend.getTransactionIdsCount() - 1;

        for (int i = 0; i <= txIdsCount; i++) {
            transactionId = Base58.encode(tempAppend.getTransactionIds(i).toByteArray());
            if (transactionId.equals(txId)) {
                txIndex = i;
                return tempAppend;
            }
        }
        return null;
    }

    public int getTxIndex() {
        return txIndex;
    }
}
