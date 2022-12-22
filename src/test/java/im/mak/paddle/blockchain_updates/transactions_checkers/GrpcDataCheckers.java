package im.mak.paddle.blockchain_updates.transactions_checkers;

import com.wavesplatform.transactions.DataTransaction;
import com.wavesplatform.transactions.data.*;
import im.mak.paddle.Account;
import im.mak.paddle.helpers.transaction_senders.DataTransactionsSender;

import static com.wavesplatform.transactions.DataTransaction.LATEST_VERSION;
import static im.mak.paddle.blockchain_updates.BaseGrpcTest.CHAIN_ID;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.DataEntries.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.DataTransactionHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.getTransactionVersion;
import static im.mak.paddle.util.Constants.MIN_FEE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class GrpcDataCheckers {
    private final int txIndex;

    private final String dataTxId;
    private final DataTransaction dataTx;

    private final String senderPublicKey;
    private final String senderAddress;

    private final DataEntry[] dataEntries;
    private final long amountBefore;
    private final long amountAfter;

    public GrpcDataCheckers(int txIndex, Account sender, DataTransactionsSender dataTxSender) {
        this.txIndex = txIndex;

        dataTxId = dataTxSender.getDataTx().id().toString();
        dataTx = dataTxSender.getDataTx();
        amountBefore = dataTxSender.getBalanceBeforeDataTransaction();
        amountAfter = dataTxSender.getBalanceAfterTransaction();

        senderPublicKey = sender.publicKey().toString();
        senderAddress = sender.address().toString();
        dataEntries = dataTxSender.getDataEntries();
    }

    public void checkDataTransactionGrpc() {
        assertAll(
                () -> assertThat(getChainId(txIndex)).isEqualTo(CHAIN_ID),
                () -> assertThat(getSenderPublicKeyFromTransaction(txIndex)).isEqualTo(senderPublicKey),
                () -> assertThat(getTransactionFeeAmount(txIndex)).isEqualTo(MIN_FEE),
                () -> assertThat(getTransactionVersion(txIndex)).isEqualTo(LATEST_VERSION),
                () -> assertThat(getTxId(txIndex)).isEqualTo(dataTxId),
                () -> assertThat(getKeyFromDataTx(txIndex, 0)).isEqualTo(dataEntries[0].key()),
                () -> assertThat(getIntValueFromDataTx(txIndex, 0)).isEqualTo(dataEntries[0].valueAsObject()),
                () -> assertThat(getKeyFromDataTx(txIndex, 1)).isEqualTo(dataEntries[1].key()),
                () -> assertThat(getByteStringValueFromDataTx(txIndex, 1)).isEqualTo(dataEntries[1].valueAsObject().toString()),
                () -> assertThat(getKeyFromDataTx(txIndex, 2)).isEqualTo(dataEntries[2].key()),
                () -> assertThat(getBooleanValueFromDataTx(txIndex, 2)).isEqualTo(dataEntries[2].valueAsObject()),
                () -> assertThat(getKeyFromDataTx(txIndex, 3)).isEqualTo(dataEntries[3].key()),
                () -> assertThat(getStringValueFromDataTx(txIndex, 3)).isEqualTo(dataEntries[3].valueAsObject()),
                // check waves balance
                () -> assertThat(getAddress(txIndex, 0)).isEqualTo(senderAddress),
                () -> assertThat(getAmountBefore(txIndex, 0)).isEqualTo(amountBefore),
                () -> assertThat(getAmountAfter(txIndex, 0)).isEqualTo(amountAfter),
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

                () -> assertThat(getAfterIntValueForStateUpdates(txIndex, 0)).isEqualTo(dataEntries[0].valueAsObject()),
                () -> assertThat(getAfterByteValueForStateUpdates(txIndex, 1)).isEqualTo(dataEntries[1].valueAsObject().toString()),
                () -> assertThat(getAfterBoolValueForStateUpdates(txIndex, 2)).isEqualTo(dataEntries[2].valueAsObject()),
                () -> assertThat(getAfterStringValueForStateUpdates(txIndex, 3)).isEqualTo(dataEntries[3].valueAsObject())
        );
    }
}
