package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers;

import com.google.protobuf.ByteString;
import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.events.protobuf.Events.BlockchainUpdated.Append;
import com.wavesplatform.protobuf.block.BlockOuterClass;
import com.wavesplatform.protobuf.transaction.TransactionOuterClass.Transaction;

import static im.mak.paddle.helpers.blockchain_updates_handlers.AppendHandler.getAppend;

public class TransactionsHandler {
    private static BlockOuterClass.Block blockInfo;
    private static BlockOuterClass.MicroBlock microBlockInfo;

    public static Transaction getWavesTransactionAtIndex(int index) {
        if (blockInfo == null) {
            return microBlockInfo.getTransactions(index).getWavesTransaction();
        }
        return blockInfo.getTransactions(index).getWavesTransaction();
    }

    public static ByteString getEthereumTransactionAtIndex(int index) {
        return microBlockInfo.getTransactions(index).getEthereumTransaction();
    }

    public static String getTxId(int index) {
        return Base58.encode(getAppend().getTransactionIds(index).toByteArray());
    }

    public static long getChainId(int index) {
        return getWavesTransactionAtIndex(index).getChainId();
    }

    public static long getTxVersion(int index) {
        return getWavesTransactionAtIndex(index).getVersion();
    }

    public static long getTxFeeAmount(int index) {
        return getWavesTransactionAtIndex(index).getFee().getAmount();
    }

    public static String getSenderPublicKeyFromTransaction(int txIndex) {
        return Base58.encode(getWavesTransactionAtIndex(txIndex)
                .getSenderPublicKey()
                .toByteArray());
    }

    public static long getTransactionFeeAmount(int txIndex) {
        return getWavesTransactionAtIndex(txIndex).getFee().getAmount();
    }

    public static long getTransactionVersion(int txIndex) {
        return getWavesTransactionAtIndex(txIndex).getVersion();
    }

    public static void setBlockInfo(Append append) {
        blockInfo = append.getBlock().getBlock();
        if (blockInfo.toString().isBlank()) {
            blockInfo = null;
        }
        microBlockInfo = append.getMicroBlock().getMicroBlock().getMicroBlock();
    }
}
