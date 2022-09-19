package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdates.SubscribeEvent;
import com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdates.SubscribeRequest;
import com.wavesplatform.events.protobuf.Events.BlockchainUpdated;
import com.wavesplatform.protobuf.block.BlockOuterClass;
import com.wavesplatform.protobuf.transaction.TransactionOuterClass;
import io.grpc.Channel;
import io.grpc.StatusRuntimeException;

import java.util.Iterator;

import static com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdatesApiGrpc.newBlockingStub;
import static com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdatesApiGrpc.BlockchainUpdatesApiBlockingStub;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.setMicroBlockInfo;

public class SubscribeHandler {

    private static BlockchainUpdated.Append append;
    private static TransactionOuterClass.Transaction firstTransaction;
    private static String transactionId;

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

    public static BlockchainUpdated.Append getAppend() {
        return append;
    }

    public static TransactionOuterClass.Transaction getFirstTransaction() {
        return firstTransaction;
    }

    public static String getTransactionId() {
        return transactionId;
    }

    private static void subscribeEventHandler(BlockchainUpdated subscribeEventUpdate, String txId) {
        append = subscribeEventUpdate.getAppend();
        BlockOuterClass.MicroBlock microBlockInfo = append
                .getMicroBlock()
                .getMicroBlock()
                .getMicroBlock();

        if (microBlockInfo.getTransactionsCount() > 0) {
            transactionId = Base58.encode(append.getTransactionIds(0).toByteArray());
            if (transactionId.equals(txId)) {
                firstTransaction = microBlockInfo.getTransactions(0).getWavesTransaction();
                setMicroBlockInfo(microBlockInfo);
            }
        }
    }
}
