package im.mak.paddle.blockchain_updates.transactions_checkers;


import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.helpers.transaction_senders.TransferTransactionSender;

import static com.wavesplatform.transactions.TransferTransaction.LATEST_VERSION;
import static im.mak.paddle.blockchain_updates.BaseGrpcTest.CHAIN_ID;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.TransactionMetadataHandler.getTransferRecipientAddressFromTransactionMetadata;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.getAmountAfter;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.getTransactionFeeAmount;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransferTransactionHandler.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class GrpcTransferCheckers {
    private final int txIndex;
    private final Account sender;
    private final String senderAddress;
    private final String recipientPublicKeyHash;
    private final String recipientAddress;
    private final TransferTransactionSender txSender;

    public GrpcTransferCheckers(int txIndex, Account sender, Account recipient, TransferTransactionSender txSender) {
        this.txIndex = txIndex;
        this.txSender = txSender;

        this.sender = sender;
        senderAddress = sender.address().toString();

        recipientAddress = recipient.address().toString();
        recipientPublicKeyHash = Base58.encode(recipient.address().publicKeyHash());
    }

    public void checkTransferSubscribe(long amountBefore, long amountAfter, long quantity, long amountSecond) {
        assertAll(
                () -> assertThat(getChainId(txIndex)).isEqualTo(CHAIN_ID),
                () -> assertThat(getTransferAssetAmount(txIndex)).isEqualTo(txSender.getAmount().value()),
                () -> assertThat(getSenderPublicKeyFromTransaction(txIndex)).isEqualTo(sender.publicKey().toString()),
                () -> assertThat(getTransactionVersion(txIndex)).isEqualTo(LATEST_VERSION),
                () -> assertThat(getTransactionFeeAmount(txIndex)).isEqualTo(txSender.getFee()),
                () -> assertThat(getTransferAssetId(txIndex)).isEqualTo(txSender.getAsset().toString()),
                () -> assertThat(getTransferTransactionPublicKeyHash(txIndex)).isEqualTo(recipientPublicKeyHash),
                () -> assertThat(getTxId(txIndex)).isEqualTo(txSender.getTransferTx().id().toString()),
                // check sender WAVES balance
                () -> assertThat(getAddress(txIndex, 0)).isEqualTo(senderAddress),
                () -> assertThat(getAmountBefore(txIndex, 0)).isEqualTo(amountBefore),
                () -> assertThat(getAmountAfter(txIndex, 0)).isEqualTo(amountAfter),
                // check recipient address
                () -> assertThat(getTransferRecipientAddressFromTransactionMetadata(txIndex)).isEqualTo(recipientAddress)
        );
        if (txSender.getAsset().toString().equals(AssetId.WAVES.toString())) {
            // check recipient balance
            assertAll(
                    () -> assertThat(getAddress(txIndex, 1)).isEqualTo(recipientAddress),
                    () -> assertThat(getAmountBefore(txIndex, 1)).isEqualTo(txIndex),
                    () -> assertThat(getAmountAfter(txIndex, 1)).isEqualTo(txSender.getAmount().value())
            );
        } else {
            assertAll(
                    // check sender asset balance
                    () -> assertThat(getAddress(txIndex, 1)).isEqualTo(senderAddress),
                    () -> assertThat(getAmountBefore(txIndex, 1)).isEqualTo(quantity),
                    () -> assertThat(getAmountAfter(txIndex, 1)).isEqualTo(amountSecond),
                    // check recipient balance
                    () -> assertThat(getAddress(txIndex, 2)).isEqualTo(recipientAddress),
                    () -> assertThat(getAmountBefore(txIndex, 2)).isEqualTo(0),
                    () -> assertThat(getAmountAfter(txIndex, 2)).isEqualTo(txSender.getAmount().value())
            );
        }
    }
}
