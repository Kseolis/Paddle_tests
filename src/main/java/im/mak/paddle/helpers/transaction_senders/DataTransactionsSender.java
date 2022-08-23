package im.mak.paddle.helpers.transaction_senders;

import com.wavesplatform.transactions.DataTransaction;
import com.wavesplatform.transactions.data.DataEntry;
import com.wavesplatform.transactions.data.EntryType;
import im.mak.paddle.Account;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static im.mak.paddle.Node.node;
import static im.mak.paddle.util.Constants.MIN_FEE;

public class DataTransactionsSender extends BaseTransactionSender {
    private static DataTransaction dataTx;
    private static final Map<String, EntryType> dataTxEntryMap = new HashMap<>();
    private final DataEntry[] dataEntries;
    private final Account sender;

    public DataTransactionsSender(Account sender, DataEntry... dataEntries) {
        this.dataEntries = dataEntries;
        this.sender = sender;

        balanceAfterTransaction = sender.getWavesBalance() - MIN_FEE;
        List<DataEntry> dataEntriesAsList = Arrays.asList(dataEntries);
        dataEntriesAsList.forEach(a -> dataTxEntryMap.put(a.key(), a.type()));
    }

    public void dataEntryTransactionSender(Account account, int version) {
        dataTx = DataTransaction.builder(dataEntries)
                .version(version)
                .getSignedWith(account.privateKey());
        node().waitForTransaction(node().broadcast(dataTx).id());
        txInfo = node().getTransactionInfo(dataTx.id());
    }

    public DataTransaction getDataTx() {
        return dataTx;
    }

    public Account getSender() {
        return sender;
    }

    public Map<String, EntryType> getDataTxEntryMap() {
        return dataTxEntryMap;
    }
}
