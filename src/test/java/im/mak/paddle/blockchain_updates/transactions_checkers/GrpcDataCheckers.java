package im.mak.paddle.blockchain_updates.transactions_checkers;

import com.wavesplatform.transactions.DataTransaction;
import im.mak.paddle.Account;
import im.mak.paddle.helpers.transaction_senders.DataTransactionsSender;

import static com.wavesplatform.transactions.DataTransaction.LATEST_VERSION;
import static im.mak.paddle.blockchain_updates.BaseGrpcTest.CHAIN_ID;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.DataEntries.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.DataTransactionHandler.getIntValueFromDataTx;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.DataTransactionHandler.getKeyFromDataTx;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.getTransactionVersion;
import static im.mak.paddle.util.Constants.DEFAULT_FAUCET;
import static im.mak.paddle.util.Constants.MIN_FEE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class GrpcDataCheckers {
    private final int txIndex;

    private final String dataTxId;
    private final DataTransaction dataTx;

    private final String senderPublicKey;
    private final String senderAddress;

    public GrpcDataCheckers(int txIndex, Account sender, DataTransactionsSender txSender) {
        this.txIndex = txIndex;

        dataTxId = txSender.getDataTx().id().toString();
        dataTx = txSender.getDataTx();

        senderPublicKey = sender.publicKey().toString();
        senderAddress = sender.address().toString();
    }

    private void checkDataTransactionSubscribe() {
        assertAll(
                () -> assertThat(getChainId(txIndex)).isEqualTo(CHAIN_ID),
                () -> assertThat(getSenderPublicKeyFromTransaction(txIndex)).isEqualTo(senderPublicKey),
                () -> assertThat(getTransactionFeeAmount(txIndex)).isEqualTo(MIN_FEE),
                () -> assertThat(getTransactionVersion(txIndex)).isEqualTo(LATEST_VERSION),
                () -> assertThat(getTxId(txIndex)).isEqualTo(dataTxId),

                () -> assertThat(getKeyFromDataTx(txIndex, 0)).isEqualTo(integerEntry.key()),
                () -> assertThat(getIntValueFromDataTx(txIndex, 0)).isEqualTo(integerEntry.value()),
                // check waves balance
                () -> assertThat(getAddress(txIndex, 0)).isEqualTo(senderAddress),
                () -> assertThat(getAmountBefore(txIndex, 0)).isEqualTo(DEFAULT_FAUCET),
                () -> assertThat(getAmountAfter(txIndex, 0)).isEqualTo(DEFAULT_FAUCET - MIN_FEE),
                this::checkDataEntries
        );
    }

    private void checkDataEntries() {
        assertAll(
                () -> dataTx.data().forEach(
                        data -> assertThat
                                (getBeforeDataEntries(txIndex, dataTx.data().indexOf(data)).getKey())
                                .isEqualTo(data.key())
                ),
                () -> dataTx.data().forEach(
                        data -> assertThat(
                                (getBeforeDataEntries(txIndex, dataTx.data().indexOf(data))
                                        .getValueCase()
                                        .toString()))
                                .isEqualTo("VALUE_NOT_SET")
                ),
                () -> dataTx.data().forEach(
                        data -> assertThat(
                                (getSenderAddress(txIndex, dataTx.data().indexOf(data))))
                                .isEqualTo(senderAddress)
                ),
                () -> dataTx.data().forEach(
                        data -> assertThat(
                                (getAfterKeyForStateUpdates(txIndex, dataTx.data().indexOf(data))))
                                .isEqualTo(data.key())
                ),

                () -> assertThat(getAfterIntValueForStateUpdates(txIndex, 0)).isEqualTo(integerEntry.value()),
                () -> assertThat(getAfterByteValueForStateUpdates(txIndex, 1)).isEqualTo(binaryEntry.value().toString()),
                () -> assertThat(getAfterBoolValueForStateUpdates(txIndex, 2)).isEqualTo(booleanEntry.value()),
                () -> assertThat(getAfterStringValueForStateUpdates(txIndex, 3)).isEqualTo(stringEntry.value())
        );
    }
}
