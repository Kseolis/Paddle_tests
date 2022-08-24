package im.mak.paddle.helpers.transaction_senders;

import com.wavesplatform.transactions.SetScriptTransaction;
import com.wavesplatform.transactions.common.Base64String;
import im.mak.paddle.Account;

import static im.mak.paddle.Node.node;
import static im.mak.paddle.util.Constants.EXTRA_FEE_FOR_SET_SCRIPT;
import static im.mak.paddle.util.Constants.MIN_FEE_FOR_SET_SCRIPT;

public class SetScriptTransactionSender extends BaseTransactionSender {
    private SetScriptTransaction setScriptTx;
    private long fee;

    private final Account account;
    private final Base64String script;

    public SetScriptTransactionSender(Account account, Base64String script) {
        this.account = account;
        this.script = script;
    }

    public void setScriptTransactionSender(long moreFee, int version) {
        fee = MIN_FEE_FOR_SET_SCRIPT + moreFee + EXTRA_FEE_FOR_SET_SCRIPT;
        balanceAfterTransaction = account.getWavesBalance() - fee;

        setScriptTx = SetScriptTransaction
                .builder(script)
                .fee(fee)
                .version(version)
                .getSignedWith(account.privateKey());
        node().waitForTransaction(node().broadcast(setScriptTx).id());

        txInfo = node().getTransactionInfo(setScriptTx.id());
    }

    public SetScriptTransaction getSetScriptTx() {
        return setScriptTx;
    }

    public long getFee() {
        return fee;
    }

    public Account getAccount() {
        return account;
    }

    public Base64String getScript() {
        return script;
    }

}
