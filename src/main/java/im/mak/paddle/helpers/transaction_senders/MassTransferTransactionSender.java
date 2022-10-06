package im.mak.paddle.helpers.transaction_senders;

import com.wavesplatform.transactions.MassTransferTransaction;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.common.Base58String;
import com.wavesplatform.transactions.mass.Transfer;
import im.mak.paddle.Account;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Calculations.*;

public class MassTransferTransactionSender extends BaseTransactionSender {
    private final Map<Address, Long> balancesAfterTransaction = new HashMap<>();

    private long senderBalanceAfterMassTransfer;
    private long senderBalanceBeforeMassTransfer;
    private long senderWavesBalanceAfterMassTransfer;

    private int accountsSize;

    private final Base58String attach = new Base58String("attachment");
    private MassTransferTransaction massTransferTx;

    private final Account sender;
    private final AssetId assetId;
    private final long amount;

    private final List<Account> accounts;
    private final List<Transfer> transfers = new ArrayList<>();

    public MassTransferTransactionSender(Account sender, AssetId id, long amount, List<Account> accounts) {
        this.sender = sender;
        this.assetId = id;
        this.amount = amount;
        this.accounts = accounts;
    }


    public void massTransferTransactionSender(int version) {
        prepareSender();

        massTransferTx = MassTransferTransaction
                .builder(transfers)
                .assetId(assetId)
                .fee(getTransactionCommission())
                .attachment(attach)
                .version(version)
                .getSignedWith(sender.privateKey());

        afterSend();
    }

    public Map<Address, Long> getBalancesAfterTransaction() {
        return balancesAfterTransaction;
    }

    public long getSenderBalanceBeforeMassTransfer() {
        return senderBalanceBeforeMassTransfer;
    }

    public long getSenderBalanceAfterMassTransfer() {
        return senderBalanceAfterMassTransfer;
    }

    public int getAccountsSize() {
        return accountsSize;
    }

    public MassTransferTransaction getMassTransferTx() {
        return massTransferTx;
    }

    public Base58String getAttach() {
        return attach;
    }

    public Account getSender() {
        return sender;
    }

    public String getAssetId() {
        return assetId.isWaves() ? "" : assetId.toString();
    }

    public long getAmount() {
        return amount;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public long getSenderWavesBalanceAfterMassTransfer() {
        return senderWavesBalanceAfterMassTransfer;
    }

    private void prepareSender() {
        accounts.forEach(a -> transfers.add(Transfer.to(a.address(), amount)));
        accounts.forEach(a -> balancesAfterTransaction.put(a.address(), a.getBalance(assetId) + amount));
        accountsSize = accounts.size();
        senderBalanceBeforeMassTransfer = sender.getBalance(assetId);
        senderBalanceAfterMassTransfer = calculateSenderBalanceAfterMassTransfer(sender, assetId, amount, accountsSize);
    }

    private void afterSend() {
        node().waitForTransaction(node().broadcast(massTransferTx).id());
        senderWavesBalanceAfterMassTransfer = sender.getWavesBalance();
        txInfo = node().getTransactionInfo(massTransferTx.id());
    }
}
