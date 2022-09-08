package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers;

import com.google.protobuf.ByteString;
import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.protobuf.transaction.TransactionOuterClass.Transaction;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.getMicroBlockInfo;

public class TransactionsHandler {

    public static Transaction getWavesTransactionAtIndex(int index) {
        return getMicroBlockInfo().getTransactions(index).getWavesTransaction();
    }

    public static ByteString getEthereumTransactionAtIndex(int index) {
        return getMicroBlockInfo().getTransactions(index).getEthereumTransaction();
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
}
