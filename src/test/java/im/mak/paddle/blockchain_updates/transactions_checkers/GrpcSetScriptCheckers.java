package im.mak.paddle.blockchain_updates.transactions_checkers;

import im.mak.paddle.helpers.transaction_senders.SetScriptTransactionSender;

import static com.wavesplatform.transactions.SetScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.blockchain_updates.BaseGrpcTest.CHAIN_ID;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.SetScriptTransactionHandler.getScriptFromSetScript;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.getTransactionVersion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class GrpcSetScriptCheckers {
    private final int txIndex;
    private final String txId;
    private final String address;
    private final String publicKey;
    private final byte[] script;
    private final long fee;
    private final long amountBefore;
    private final long amountAfter;

    public GrpcSetScriptCheckers(int txIndex, SetScriptTransactionSender txSender) {
        this.txIndex = txIndex;
        this.txId = txSender.getSetScriptTx().id().toString();
        this.address = txSender.getSetScriptTx().sender().address().toString();
        this.publicKey = txSender.getSetScriptTx().sender().toString();
        this.script = txSender.getScript().bytes();
        this.fee = txSender.getFee();
        this.amountBefore = txSender.getWavesBalanceBeforeSetScript();
        this.amountAfter = txSender.getBalanceAfterTransaction();
    }

    public void checkSetScriptGrpc() {
        assertAll(
                () -> assertThat(getChainId(txIndex)).isEqualTo(CHAIN_ID),
                () -> assertThat(getSenderPublicKeyFromTransaction(txIndex)).isEqualTo(publicKey),
                () -> assertThat(getTransactionFeeAmount(txIndex)).isEqualTo(fee),
                () -> assertThat(getTransactionVersion(txIndex)).isEqualTo(LATEST_VERSION),
                () -> assertThat(getScriptFromSetScript(txIndex)).isEqualTo(script),
                () -> assertThat(getTxId(txIndex)).isEqualTo(txId),
                // check waves balance
                () -> assertThat(getAddress(txIndex, 0)).isEqualTo(address),
                () -> assertThat(getAmountBefore(txIndex, 0)).isEqualTo(amountBefore),
                () -> assertThat(getAmountAfter(txIndex, 0)).isEqualTo(amountAfter)
        );
    }
}
