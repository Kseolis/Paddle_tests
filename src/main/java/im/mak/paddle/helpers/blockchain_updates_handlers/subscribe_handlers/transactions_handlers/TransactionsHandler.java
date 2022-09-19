package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers;

import com.google.protobuf.ByteString;
import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.protobuf.block.BlockOuterClass;
import com.wavesplatform.protobuf.transaction.TransactionOuterClass.Transaction;

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

    public static long getChainId(int index) {
        return getWavesTransactionAtIndex(index).getChainId();
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

    public static void setBlockInfo(BlockOuterClass.Block block) {
        blockInfo = block;
    }

    public static void setMicroBlockInfo(BlockOuterClass.MicroBlock microBlock) {
        microBlockInfo = microBlock;
    }
}
