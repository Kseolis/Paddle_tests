package im.mak.paddle.helpers.transaction_senders.invoke;

import com.wavesplatform.transactions.InvokeScriptTransaction;
import com.wavesplatform.transactions.common.Amount;
import im.mak.paddle.Account;
import im.mak.paddle.dapp.DAppCall;
import im.mak.paddle.helpers.transaction_senders.BaseTransactionSender;

import java.util.ArrayList;
import java.util.List;

import static im.mak.paddle.Node.node;

public class InvokeScriptTransactionSender extends BaseTransactionSender {
    private InvokeScriptTransaction invokeScriptTx;
    private final DAppCall dAppCall;
    private final Account caller;
    private final Account dAppAccount;
    private List<Amount> amounts = new ArrayList<>();

    public InvokeScriptTransactionSender(Account caller, Account dAppAccount, DAppCall call, List<Amount> amounts) {
        this.dAppCall = call;
        this.caller = caller;
        this.dAppAccount = dAppAccount;
        this.amounts = amounts;
    }

    public InvokeScriptTransactionSender(Account caller, Account dAppAccount, DAppCall call) {
        this.caller = caller;
        this.dAppAccount = dAppAccount;
        this.dAppCall = call;
    }

    public void invokeSenderWithPayment() {
        invokeScriptTx = InvokeScriptTransaction
                .builder(dAppAccount.address(), dAppCall.getFunction())
                .payments(amounts)
                .version(version)
                .extraFee(extraFee)
                .getSignedWith(caller.privateKey());

        node().waitForTransaction(node().broadcast(invokeScriptTx).id());

        txInfo = node().getTransactionInfo(invokeScriptTx.id());
    }

    public void invokeSender() {
        invokeScriptTx = InvokeScriptTransaction
                .builder(dAppAccount.address(), dAppCall.getFunction())
                .version(version)
                .extraFee(extraFee)
                .getSignedWith(caller.privateKey());
        node().waitForTransaction(node().broadcast(invokeScriptTx).id());

        txInfo = node().getTransactionInfo(invokeScriptTx.id());
    }

    public InvokeScriptTransaction getInvokeScriptTx() {
        return invokeScriptTx;
    }

    public String getInvokeScriptId() {
        return invokeScriptTx.id().toString();
    }
}
