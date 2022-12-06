package im.mak.paddle.blockchain_updates.transactions_checkers;

import com.wavesplatform.transactions.common.Id;
import im.mak.paddle.helpers.transaction_senders.EthereumTransactionSender;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.EthereumTransactionMetadataHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.TransactionMetadataHandler.getSenderAddressMetadata;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.getAmountAfter;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.getTxId;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class GrpcEthereumTransferCheckers {
    private final int txIndex;
    private final Id ethTxId;
    private final String senderPublicKey;
    private final String senderAddress;
    private final long senderBalanceBeforeTx;
    private final long senderBalanceAfterTx;
    private final long recipientBalanceBeforeTx;
    private final long recipientBalanceAfterTx;
    private final long timestamp;
    private final long fee;
    private final long amountVal;
    private final String recipientAddress;

    public GrpcEthereumTransferCheckers(int txIndex, EthereumTransactionSender txSender, long amountVal) {
        this.txIndex = txIndex;
        this.amountVal = amountVal;
        this.senderPublicKey = txSender.getEthTx().sender().toString();
        this.senderAddress = txSender.getEthTx().sender().address().toString();
        this.senderBalanceBeforeTx = txSender.getSenderBalanceBeforeEthTransaction();
        this.senderBalanceAfterTx = txSender.getSenderBalanceAfterEthTransaction();
        this.recipientBalanceBeforeTx = txSender.getRecipientBalanceBeforeEthTransaction();
        this.recipientBalanceAfterTx = txSender.getRecipientBalanceAfterEthTransaction();
        this.ethTxId = txSender.getEthTx().id();
        this.fee = txSender.getEthTx().fee().value();
        this.timestamp = txSender.getEthTx().timestamp();
        this.recipientAddress = txSender.getRecipientAddress().toString();
    }

    public void checkEthereumTransfer() {
        assertAll(
                () -> assertThat(getTxId(txIndex)).isEqualTo(ethTxId.toString()),
                () -> assertThat(getSenderAddressMetadata(txIndex)).isEqualTo(senderAddress),
                () -> assertThat(getEthereumTransactionTimestampMetadata(txIndex)).isEqualTo(timestamp),
                () -> assertThat(getEthereumTransactionFeeMetadata(txIndex)).isEqualTo(fee),
                () -> assertThat(getEthereumTransactionSenderPublicKeyMetadata(txIndex)).isEqualTo(senderPublicKey),
                () -> assertThat(getEthereumTransferRecipientAddressMetadata(txIndex)).isEqualTo(recipientAddress),
                () -> assertThat(getEthereumTransferAmountMetadata(txIndex)).isEqualTo(amountVal),
                // check sender asset balance
                () -> assertThat(getAddress(txIndex, 0)).isEqualTo(senderAddress),
                () -> assertThat(getAmountBefore(txIndex, 0)).isEqualTo(senderBalanceBeforeTx),
                () -> assertThat(getAmountAfter(txIndex, 0)).isEqualTo(senderBalanceAfterTx),
                // check recipient balance
                () -> assertThat(getAddress(txIndex, 1)).isEqualTo(recipientAddress),
                () -> assertThat(getAmountBefore(txIndex, 1)).isEqualTo(recipientBalanceBeforeTx),
                () -> assertThat(getAmountAfter(txIndex, 1)).isEqualTo(recipientBalanceAfterTx)
        );
    }
}
