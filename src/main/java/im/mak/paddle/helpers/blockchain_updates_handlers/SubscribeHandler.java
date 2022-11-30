package im.mak.paddle.helpers.blockchain_updates_handlers;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdates.SubscribeEvent;
import com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdates.SubscribeRequest;
import com.wavesplatform.events.protobuf.Events.BlockchainUpdated;
import com.wavesplatform.events.protobuf.Events.BlockchainUpdated.Append;
import com.wavesplatform.protobuf.block.BlockOuterClass.MicroBlock;
import io.grpc.Channel;
import io.grpc.StatusRuntimeException;

import java.util.Iterator;

import static com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdatesApiGrpc.newBlockingStub;
import static com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdatesApiGrpc.BlockchainUpdatesApiBlockingStub;
import static im.mak.paddle.helpers.blockchain_updates_handlers.AppendHandler.setAppend;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.setBlockInfo;

public class SubscribeHandler {
    public static void subscribeResponseHandler(Channel channel, int fromHeight, int toHeight, String txId) {
        SubscribeRequest request = SubscribeRequest
                .newBuilder()
                .setFromHeight(fromHeight)
                .setToHeight(toHeight)
                .build();

        BlockchainUpdatesApiBlockingStub stub = newBlockingStub(channel);
        Iterator<SubscribeEvent> subscribe = stub.subscribe(request);

        try {
            while (subscribe.hasNext()) {
                subscribeEventHandler(subscribe.next().getUpdate(), txId);
            }
        } catch (StatusRuntimeException ignored) {
        }
    }

    private static void subscribeEventHandler(BlockchainUpdated subscribeEventUpdate, String txId) {
        Append append = subscribeEventUpdate.getAppend();

        System.out.println(subscribeEventUpdate);

        MicroBlock microBlockInfo = append
                .getMicroBlock()
                .getMicroBlock()
                .getMicroBlock();
        int transactionCount = microBlockInfo.getTransactionsCount();

        if (transactionCount > 0) {
            for (int i = 0; i < transactionCount; i++) {
                String transactionId = Base58.encode(append.getTransactionIds(i).toByteArray());
                if (transactionId.equals(txId)) {
                    System.out.println("append " + append);
                    setBlockInfo(append);
                    setAppend(append);
                }
            }
        }
    }
}
