package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.crypto.base.Base64;
import com.wavesplatform.events.protobuf.Events;
import com.wavesplatform.protobuf.transaction.TransactionOuterClass;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.TransactionStateUpdates.getTransactionStateUpdate;

public class DataEntries {
    public static String getSenderAddress(int txStateUpdIndex, int index) {
        return Base58.encode(getDataEntries(txStateUpdIndex, index).getAddress().toByteArray());
    }

    public static String getAfterKeyForStateUpdates(int txStateUpdIndex, int index) {
        return getDataEntries(txStateUpdIndex, index).getDataEntry().getKey();
    }

    public static long getAfterIntValueForStateUpdates(int txStateUpdIndex, int index) {
        return getDataEntries(txStateUpdIndex, index).getDataEntry().getIntValue();
    }

    public static String getAfterStringValueForStateUpdates(int txStateUpdIndex, int index) {
        return getDataEntries(txStateUpdIndex, index).getDataEntry().getStringValue();
    }

    public static boolean getAfterBoolValueForStateUpdates(int txStateUpdIndex, int index) {
        return getDataEntries(txStateUpdIndex, index).getDataEntry().getBoolValue();
    }

    public static String getAfterByteValueForStateUpdates(int txStateUpdIndex, int index) {
        return Base64.encode(getDataEntries(txStateUpdIndex, index).getDataEntry().getBinaryValue().toByteArray());
    }

    public static long getBeforeDataEntriesIntegerValue(int txStateUpdIndex, int index) {
        return getBeforeDataEntries(txStateUpdIndex, index).getIntValue();
    }

    public static String getBeforeDataEntriesStringValue(int txStateUpdIndex, int index) {
        return getBeforeDataEntries(txStateUpdIndex, index).getStringValue();
    }

    public static String getBeforeDataEntriesKey(int txStateUpdIndex, int index) {
        return getBeforeDataEntries(txStateUpdIndex, index).getKey();
    }

    public static TransactionOuterClass.DataTransactionData.DataEntry getBeforeDataEntries(int txStateUpdIndex, int index) {
        return getDataEntries(txStateUpdIndex, index).getDataEntryBefore();
    }

    private static Events.StateUpdate.DataEntryUpdate getDataEntries(int txStateUpdIndex, int index) {
        return getTransactionStateUpdate(txStateUpdIndex).getDataEntries(index);
    }
}
