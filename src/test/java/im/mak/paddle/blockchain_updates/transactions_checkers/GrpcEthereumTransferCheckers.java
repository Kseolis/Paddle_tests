package im.mak.paddle.blockchain_updates.transactions_checkers;

import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.Id;
import im.mak.paddle.helpers.transaction_senders.EthereumTransferTransactionSender;

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
    private final long senderWavesBalanceBeforeTx;
    private final long senderWavesBalanceAfterTx;
    private final long senderAssetBalanceBeforeTx;
    private final long senderAssetBalanceAfterTx;
    private final long recipientWavesBalanceBeforeTx;
    private final long recipientWavesBalanceAfterTx;
    private final long recipientAssetBalanceBeforeTx;
    private final long recipientAssetBalanceAfterTx;
    private final long timestamp;
    private final long fee;
    private final Amount amount;
    private final String recipientAddress;

    public GrpcEthereumTransferCheckers(int txIndex, EthereumTransferTransactionSender txSender, Amount amount) {
        this.txIndex = txIndex;
        this.amount = amount;
        this.senderPublicKey = txSender.getEthTx().sender().toString();
        this.senderAddress = txSender.getEthTx().sender().address().toString();

        this.senderWavesBalanceBeforeTx = txSender.getSenderBalanceBeforeEthTransaction();
        this.senderWavesBalanceAfterTx = txSender.getSenderBalanceAfterEthTransaction();
        this.senderAssetBalanceBeforeTx = txSender.getSenderAssetBalanceBeforeTransaction();
        this.senderAssetBalanceAfterTx = txSender.getSenderAssetBalanceAfterTransaction();

        this.recipientWavesBalanceBeforeTx = txSender.getRecipientBalanceBeforeEthTransaction();
        this.recipientWavesBalanceAfterTx = txSender.getRecipientBalanceAfterEthTransaction();
        this.recipientAssetBalanceBeforeTx = txSender.getRecipientAssetBalanceBeforeTransaction();
        this.recipientAssetBalanceAfterTx = txSender.getRecipientAssetBalanceAfterTransaction();

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
                () -> assertThat(getEthereumTransferAmountMetadata(txIndex)).isEqualTo(amount.value())
        );
    }

    public void checkEthereumTransferBalances() {
        if (amount.assetId().isWaves()) {
            assertAll(
                    // check sender asset balance
                    () -> assertThat(getAddress(txIndex, 0)).isEqualTo(senderAddress),
                    () -> assertThat(getAmountBefore(txIndex, 0)).isEqualTo(senderWavesBalanceBeforeTx),
                    () -> assertThat(getAmountAfter(txIndex, 0)).isEqualTo(senderWavesBalanceAfterTx),
                    // check recipient balance
                    () -> assertThat(getAddress(txIndex, 1)).isEqualTo(recipientAddress),
                    () -> assertThat(getAmountBefore(txIndex, 1)).isEqualTo(recipientWavesBalanceBeforeTx),
                    () -> assertThat(getAmountAfter(txIndex, 1)).isEqualTo(recipientWavesBalanceAfterTx)
            );
        } else {
            assertAll(
                    // check sender asset balance
                    () -> assertThat(getAddress(txIndex, 0)).isEqualTo(senderAddress),
                    () -> assertThat(getAmountBefore(txIndex, 0)).isEqualTo(senderWavesBalanceBeforeTx),
                    () -> assertThat(getAmountAfter(txIndex, 0)).isEqualTo(senderWavesBalanceAfterTx),
                    // check recipient balance
                    () -> assertThat(getAddress(txIndex, 1)).isEqualTo(senderAddress),
                    () -> assertThat(getAssetIdAmountAfter(txIndex, 1)).isEqualTo(amount.assetId().toString()),
                    () -> assertThat(getAmountBefore(txIndex, 1)).isEqualTo(senderAssetBalanceBeforeTx),
                    () -> assertThat(getAmountAfter(txIndex, 1)).isEqualTo(senderAssetBalanceAfterTx),
                    // check recipient balance
                    () -> assertThat(getAddress(txIndex, 2)).isEqualTo(recipientAddress),
                    () -> assertThat(getAssetIdAmountAfter(txIndex, 2)).isEqualTo(amount.assetId().toString()),
                    () -> assertThat(getAmountBefore(txIndex, 2)).isEqualTo(recipientAssetBalanceBeforeTx),
                    () -> assertThat(getAmountAfter(txIndex, 2)).isEqualTo(recipientAssetBalanceAfterTx)
            );
        }
    }
}
