package im.mak.paddle.helpers.transaction_senders.invoke;

import com.wavesplatform.transactions.InvokeScriptTransaction;
import com.wavesplatform.transactions.common.Amount;
import im.mak.paddle.Account;
import im.mak.paddle.dapp.DAppCall;
import im.mak.paddle.helpers.transaction_senders.BaseTransactionSender;

import java.util.List;

import static im.mak.paddle.Node.node;

public class InvokeScriptTransactionSender extends BaseTransactionSender {
    private static InvokeScriptTransaction invokeScriptTx;
    private static DAppCall dAppCall;

    public static void invokeSenderWithPayment(Account caller, Account dAppAccount, DAppCall call, List<Amount> amounts) {
        dAppCall = call;

        invokeScriptTx = InvokeScriptTransaction
                .builder(dAppAccount.address(), dAppCall.getFunction())
                .payments(amounts)
                .version(version)
                .extraFee(extraFee)
                .getSignedWith(caller.privateKey());

        node().waitForTransaction(node().broadcast(invokeScriptTx).id());

        txInfo = node().getTransactionInfo(invokeScriptTx.id());
    }


    public static void invokeSender(Account caller, Account dAppAccount, DAppCall call) {
        dAppCall = call;

        invokeScriptTx = InvokeScriptTransaction
                .builder(dAppAccount.address(), dAppCall.getFunction())
                .version(version)
                .extraFee(extraFee)
                .getSignedWith(caller.privateKey());
        node().waitForTransaction(node().broadcast(invokeScriptTx).id());

        txInfo = node().getTransactionInfo(invokeScriptTx.id());
    }

    public static InvokeScriptTransaction getInvokeScriptTx() {
        return invokeScriptTx;
    }

    public static DAppCall getDAppCall() {
        return dAppCall;
    }

    public static String getInvokeScriptId() {
        return invokeScriptTx.id().toString();
    }
}
