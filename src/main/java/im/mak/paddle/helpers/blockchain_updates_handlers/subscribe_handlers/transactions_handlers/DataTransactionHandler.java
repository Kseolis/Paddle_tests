package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers;

import com.wavesplatform.crypto.base.Base64;

import static com.wavesplatform.protobuf.transaction.TransactionOuterClass.DataTransactionData.DataEntry;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.getWavesTransactionAtIndex;

public class DataTransactionHandler {
    public static String getKeyFromDataTx(int txIndex, int dataIndex) {
        return getDataEntryFromDataTx(txIndex, dataIndex).getKey();
    }

    public static long getIntValueFromDataTx(int txIndex, int dataIndex) {
        return getDataEntryFromDataTx(txIndex, dataIndex).getIntValue();
    }

    public static String getStringValueFromDataTx(int txIndex, int dataIndex) {
        return getDataEntryFromDataTx(txIndex, dataIndex).getStringValue();
    }

    public static boolean getBooleanValueFromDataTx(int txIndex, int dataIndex) {
        return getDataEntryFromDataTx(txIndex, dataIndex).getBoolValue();
    }

    public static String getByteStringValueFromDataTx(int txIndex, int dataIndex) {
        return Base64.encode(getDataEntryFromDataTx(txIndex, dataIndex).getBinaryValue().toByteArray());
    }

    private static DataEntry getDataEntryFromDataTx(int txIndex, int dataIndex) {
        return getWavesTransactionAtIndex(txIndex)
                .getDataTransaction()
                .getData(dataIndex);
    }
}
