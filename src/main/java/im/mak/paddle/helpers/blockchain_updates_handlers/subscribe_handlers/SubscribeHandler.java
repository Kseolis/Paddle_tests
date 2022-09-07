package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdates;
import com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdatesApiGrpc;
import com.wavesplatform.events.protobuf.Events.BlockchainUpdated;
import com.wavesplatform.protobuf.block.BlockOuterClass;
import com.wavesplatform.protobuf.transaction.TransactionOuterClass;
import im.mak.paddle.Account;
import io.grpc.Channel;
import io.grpc.StatusRuntimeException;

import java.util.Iterator;

import static com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdatesApiGrpc.newBlockingStub;

public class SubscribeHandler {

    private static BlockchainUpdated.Append append;
    private static BlockOuterClass.MicroBlock microBlockInfo;
    private static TransactionOuterClass.Transaction firstTransaction;
    private static String transactionId;

    public static void subscribeResponseHandler
            (Channel channel, Account account, int fromHeight, int toHeight, String txId) {
        BlockchainUpdates.SubscribeRequest request = BlockchainUpdates.SubscribeRequest
                .newBuilder()
                .setFromHeight(fromHeight)
                .setToHeight(toHeight)
                .build();

        BlockchainUpdatesApiGrpc.BlockchainUpdatesApiBlockingStub stub = newBlockingStub(channel);
        Iterator<BlockchainUpdates.SubscribeEvent> subscribe = stub.subscribe(request);

        try {
            while (subscribe.hasNext()) {
                subscribeEventHandler(subscribe.next().getUpdate(), account, txId);
            }
        } catch (StatusRuntimeException ignored) {
        }
    }

    public static BlockchainUpdated.Append getAppend() {
        return append;
    }

    public static BlockOuterClass.MicroBlock getMicroBlockInfo() {
        return microBlockInfo;
    }

    public static TransactionOuterClass.Transaction getFirstTransaction() {
        return firstTransaction;
    }

    public static String getTransactionId() {
        return transactionId;
    }

    private static void subscribeEventHandler(BlockchainUpdated subscribeEventUpdate, Account account, String txId) {
        final String accPublicKey = account.publicKey().toString();
        append = subscribeEventUpdate.getAppend();
        microBlockInfo = append
                .getMicroBlock()
                .getMicroBlock()
                .getMicroBlock();

        if (microBlockInfo.getTransactionsCount() > 0) {

            String transactionSenderPublicKey = Base58.encode(microBlockInfo
                    .getTransactions(0)
                    .getWavesTransaction()
                    .getSenderPublicKey()
                    .toByteArray());

            transactionId = Base58.encode(append.getTransactionIds(0).toByteArray());
            if (transactionSenderPublicKey.equalsIgnoreCase(accPublicKey) && transactionId.equals(txId)) {
                firstTransaction = microBlockInfo.getTransactions(0).getWavesTransaction();
            }
        }
    }
}
