package im.mak.paddle.helpers.blockchain_updates_handlers;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdates.GetBlockUpdateRequest;
import com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdates.GetBlockUpdateResponse;
import com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdatesApiGrpc.BlockchainUpdatesApiBlockingStub;
import com.wavesplatform.events.protobuf.Events.BlockchainUpdated.Append;
import io.grpc.Channel;

import java.util.List;

import static com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdatesApiGrpc.newBlockingStub;
import static im.mak.paddle.helpers.blockchain_updates_handlers.AppendHandler.setAppend;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.BlockInfo.setBlockInfo;

public class GetBlockUpdateHandler {
    private Append append;
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
                setAppend(append);
            }
        }
    }

    private Append searchingForTransactionInBlock(GetBlockUpdateResponse response, String txId) {
        Append append = response.getUpdate().getAppend();
        long txIdsCount = append.getTransactionIdsCount() - 1;

        for (int i = 0; i <= txIdsCount; i++) {
            String transactionId = Base58.encode(append.getTransactionIds(i).toByteArray());
            if (transactionId.equals(txId)) {
                txIndex = i;
                setBlockInfo(append);
                return append;
            }
        }
        return null;
    }

    public int getTxIndex() {
        return txIndex;
    }
}
