package im.mak.paddle.blockchain_updates.transactions_checkers;

import com.wavesplatform.crypto.base.Base58;
import im.mak.paddle.Account;
import im.mak.paddle.helpers.transaction_senders.LeaseTransactionSender;

import static com.wavesplatform.transactions.LeaseTransaction.LATEST_VERSION;
import static im.mak.paddle.blockchain_updates.BaseGrpcTest.CHAIN_ID;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.TransactionMetadataHandler.getLeaseTransactionMetadata;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Leasing.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.LeaseTransactionHandler.getLeaseAssetAmount;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.LeaseTransactionHandler.getLeaseTransactionPublicKeyHash;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.getTransactionVersion;
import static im.mak.paddle.util.Constants.ACTIVE_STATUS_LEASE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class GrpcLeaseCheckers {
    private final int txIndex;

    private final String senderAddress;
    private final String senderPublicKey;

    private final String recipientAddress;
    private final String recipientPublicKeyHash;

    private final String leaseId;
    private final long leaseSum;

    public GrpcLeaseCheckers(int txIndex, Account sender, Account recipient, LeaseTransactionSender leaseTxSender) {
        this.txIndex = txIndex;
        this.senderAddress = sender.address().toString();
        this.recipientAddress = recipient.address().toString();

        senderPublicKey = sender.publicKey().toString();
        recipientPublicKeyHash = Base58.encode(recipient.address().publicKeyHash());
        leaseId = leaseTxSender.getLeaseTx().id().toString();
        leaseSum = leaseTxSender.getLeaseTx().amount();
    }

    public void checkLeaseGrpc(long fee, long amountBefore, long amountAfter) {
        assertAll(
                // transaction
                () -> assertThat(getChainId(txIndex)).isEqualTo(CHAIN_ID),
                () -> assertThat(getSenderPublicKeyFromTransaction(txIndex)).isEqualTo(senderPublicKey),
                () -> assertThat(getTransactionFeeAmount(txIndex)).isEqualTo(fee),
                () -> assertThat(getTransactionVersion(txIndex)).isEqualTo(LATEST_VERSION),
                // lease
                () -> assertThat(getLeaseTransactionPublicKeyHash(txIndex)).isEqualTo(recipientPublicKeyHash),
                () -> assertThat(getLeaseAssetAmount(txIndex)).isEqualTo(leaseSum),
                // transaction_ids
                () -> assertThat(getTxId(txIndex)).isEqualTo(leaseId),
                // transactions_metadata
                () -> assertThat(getLeaseTransactionMetadata(txIndex)).isEqualTo(recipientAddress),
                // balances sender
                () -> assertThat(getAddress(txIndex, 0)).isEqualTo(senderAddress),
                () -> assertThat(getAmountBefore(txIndex, 0)).isEqualTo(amountBefore),
                () -> assertThat(getAmountAfter(txIndex, 0)).isEqualTo(amountAfter),
                // leasing_for_address sender
                () -> assertThat(getAddressFromLeasingForAddress(txIndex, 0)).isEqualTo(senderAddress),
                () -> assertThat(getOutAfterFromLeasingForAddress(txIndex, 0)).isEqualTo(leaseSum),
                // leasing_for_address recipient
                () -> assertThat(getAddressFromLeasingForAddress(txIndex, 1)).isEqualTo(recipientAddress),
                () -> assertThat(getInAfterFromLeasingForAddress(txIndex, 1)).isEqualTo(leaseSum),
                // individual_leases
                () -> assertThat(getLeaseIdFromIndividualLeases(txIndex, 0)).isEqualTo(leaseId),
                () -> assertThat(getStatusAfterFromIndividualLeases(txIndex, 0)).isEqualTo(ACTIVE_STATUS_LEASE),
                () -> assertThat(getAmountFromIndividualLeases(txIndex, 0)).isEqualTo(leaseSum),
                () -> assertThat(getSenderFromIndividualLeases(txIndex, 0)).isEqualTo(senderPublicKey),
                () -> assertThat(getRecipientFromIndividualLeases(txIndex, 0)).isEqualTo(recipientAddress),
                () -> assertThat(getOriginalTransactionIdFromIndividualLeases(txIndex, 0)).isEqualTo(leaseId)
        );
    }
}
