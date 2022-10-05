package im.mak.paddle.blockchain_updates.transactions_checkers;

import im.mak.paddle.Account;
import im.mak.paddle.helpers.transaction_senders.LeaseCancelTransactionSender;

import static com.wavesplatform.transactions.LeaseCancelTransaction.LATEST_VERSION;
import static im.mak.paddle.blockchain_updates.BaseGrpcTest.CHAIN_ID;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Leasing.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Leasing.getOriginalTransactionIdFromIndividualLeases;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.LeaseCancelTransactionHandler.getLeaseCancelLeaseId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.getTransactionVersion;
import static im.mak.paddle.util.Constants.INACTIVE_STATUS_LEASE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class GrpcLeaseCancelCheckers {
    private final int txIndex;

    private final String senderAddress;
    private final String senderPublicKey;

    private final String recipientAddress;

    private final String leaseCancelId;
    private final String leaseId;

    private final long fee;
    private final long amountBefore;
    private final long amountAfter;

    public GrpcLeaseCancelCheckers
            (int txIndex, Account sender, Account recipient, LeaseCancelTransactionSender leaseCancelTxSender) {
        this.txIndex = txIndex;
        this.senderAddress = sender.address().toString();
        this.recipientAddress = recipient.address().toString();

        senderPublicKey = sender.publicKey().toString();

        leaseCancelId = leaseCancelTxSender.getLeaseCancelTx().id().toString();
        leaseId = leaseCancelTxSender.getLeaseCancelTx().leaseId().toString();
        fee = leaseCancelTxSender.getFee();
        amountBefore = leaseCancelTxSender.getAmountBefore();
        amountAfter = leaseCancelTxSender.getAmountAfter();
    }

    public void checkLeaseCancelGrpc(long amountLease) {
        assertAll(
                // transaction
                () -> assertThat(getChainId(txIndex)).isEqualTo(CHAIN_ID),
                () -> assertThat(getSenderPublicKeyFromTransaction(txIndex)).isEqualTo(senderPublicKey),
                () -> assertThat(getTransactionFeeAmount(txIndex)).isEqualTo(fee),
                () -> assertThat(getTransactionVersion(txIndex)).isEqualTo(LATEST_VERSION),
                // lease cancel
                () -> assertThat(getLeaseCancelLeaseId(txIndex)).isEqualTo(leaseId),
                // transaction_ids
                () -> assertThat(getTxId(txIndex)).isEqualTo(leaseCancelId),
                // balances sender
                () -> assertThat(getAddress(txIndex, 0)).isEqualTo(senderAddress),
                () -> assertThat(getAmountBefore(txIndex, 0)).isEqualTo(amountBefore),
                () -> assertThat(getAmountAfter(txIndex, 0)).isEqualTo(amountAfter),
                // leasing_for_address sender
                () -> assertThat(getAddressFromLeasingForAddress(txIndex, 0)).isEqualTo(senderAddress),
                () -> assertThat(getOutBeforeFromLeasingForAddress(txIndex, 0)).isEqualTo(amountLease),
                () -> assertThat(getOutAfterFromLeasingForAddress(txIndex, 0)).isEqualTo(0),
                // leasing_for_address recipient
                () -> assertThat(getAddressFromLeasingForAddress(txIndex, 1)).isEqualTo(recipientAddress),
                () -> assertThat(getInBeforeFromLeasingForAddress(txIndex, 1)).isEqualTo(amountLease),
                () -> assertThat(getInAfterFromLeasingForAddress(txIndex, 1)).isEqualTo(0),
                // individual_leases
                () -> assertThat(getLeaseIdFromIndividualLeases(txIndex, 0)).isEqualTo(leaseId),
                () -> assertThat(getStatusAfterFromIndividualLeases(txIndex, 0)).isEqualTo(INACTIVE_STATUS_LEASE),
                () -> assertThat(getAmountFromIndividualLeases(txIndex, 0)).isEqualTo(amountLease),
                () -> assertThat(getSenderFromIndividualLeases(txIndex, 0)).isEqualTo(senderPublicKey),
                () -> assertThat(getRecipientFromIndividualLeases(txIndex, 0)).isEqualTo(recipientAddress),
                () -> assertThat(getOriginalTransactionIdFromIndividualLeases(txIndex, 0)).isEqualTo(leaseId)
        );
    }
}
