package im.mak.paddle.helpers.blockchain_updates_handlers;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdates.GetBlockUpdatesRangeRequest;
import com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdates.GetBlockUpdatesRangeResponse;
import com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdatesApiGrpc.BlockchainUpdatesApiBlockingStub;
import com.wavesplatform.events.protobuf.Events.BlockchainUpdated;
import com.wavesplatform.events.protobuf.Events.BlockchainUpdated.Append;
import io.grpc.Channel;

import java.util.List;

import static com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdatesApiGrpc.newBlockingStub;
import static im.mak.paddle.helpers.blockchain_updates_handlers.AppendHandler.setAppend;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.BlockInfo.setBlockInfo;

public class GetBlockUpdatesRangeHandler {
    private int txIndex;

    public void getBlockUpdateRangeResponseHandler(Channel channel, int fromHeight, int toHeight, String txId) {
        GetBlockUpdatesRangeRequest request = GetBlockUpdatesRangeRequest
                .newBuilder()
                .setFromHeight(fromHeight)
                .setToHeight(toHeight)
                .build();
        BlockchainUpdatesApiBlockingStub stub = newBlockingStub(channel);
        GetBlockUpdatesRangeResponse response = stub.getBlockUpdatesRange(request);

        Append append = searchingForTransactionInBlock(response, txId);
        setAppend(append);
    }

    private Append searchingForTransactionInBlock(GetBlockUpdatesRangeResponse response, String txId) {
        List<BlockchainUpdated> updatesList = response.getUpdatesList();
        for (BlockchainUpdated updates : updatesList) {
            Append append = updates.getAppend();
            long txIdsCount = append.getTransactionIdsCount();

            for (int i = 0; i < txIdsCount; i++) {
                String transactionId = Base58.encode(append.getTransactionIds(i).toByteArray());
                if (transactionId.equals(txId)) {
                    txIndex = i;
                    setBlockInfo(append);
                    return append;
                }
            }
        }
        return null;
    }

    public int getTxIndex() {
        return txIndex;
    }
}
