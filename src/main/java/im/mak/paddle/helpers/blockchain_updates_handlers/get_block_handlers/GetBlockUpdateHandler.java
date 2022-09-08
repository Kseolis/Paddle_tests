package im.mak.paddle.helpers.blockchain_updates_handlers.get_block_handlers;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdates.GetBlockUpdateRequest;
import com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdates.GetBlockUpdateResponse;
import com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdatesApiGrpc;
import com.wavesplatform.events.protobuf.Events.BlockchainUpdated.Append;
import com.wavesplatform.protobuf.block.BlockOuterClass.MicroBlock;
import com.wavesplatform.protobuf.transaction.TransactionOuterClass;
import im.mak.paddle.Account;
import io.grpc.Channel;

import static com.wavesplatform.events.api.grpc.protobuf.BlockchainUpdatesApiGrpc.newBlockingStub;

public class GetBlockUpdateHandler {
    private Append append;
    private MicroBlock microBlockInfo;
    private TransactionOuterClass.Transaction firstTransaction;
    private String transactionId;

    public void getBlockUpdateResponseHandler(Channel channel, Account account, int height, String txId) {
        GetBlockUpdateRequest request = GetBlockUpdateRequest
                .newBuilder()
                .setHeight(height)
                .build();

        BlockchainUpdatesApiGrpc.BlockchainUpdatesApiBlockingStub stub = newBlockingStub(channel);
        GetBlockUpdateResponse response = stub.getBlockUpdate(request);
        blockUpdateEventHandler(response, account, txId);
    }

    public Append getAppend() {
        return append;
    }

    public MicroBlock getMicroBlockInfo() {
        return microBlockInfo;
    }

    public TransactionOuterClass.Transaction getFirstTransaction() {
        return firstTransaction;
    }

    public String getTransactionId() {
        return transactionId;
    }

    private void blockUpdateEventHandler(GetBlockUpdateResponse response, Account account, String txId) {
        final String accPublicKey = account.publicKey().toString();
        append = response.getUpdate().getAppend();
        microBlockInfo = append
                .getMicroBlock()
                .getMicroBlock()
                .getMicroBlock();
        System.out.println(append);
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
