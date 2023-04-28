package im.mak.paddle.blockchain_updates.transactions_checkers;

import com.wavesplatform.crypto.base.Base58;
import im.mak.paddle.Account;
import im.mak.paddle.helpers.transaction_senders.MassTransferTransactionSender;

import java.util.List;

import static com.wavesplatform.transactions.MassTransferTransaction.LATEST_VERSION;
import static im.mak.paddle.blockchain_updates.BaseGrpcTest.CHAIN_ID;
import static im.mak.paddle.helpers.Calculations.getTransactionCommission;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_metadata.TransactionMetadataHandler.getMassTransferFromTransactionMetadata;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.getAmountAfter;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.MassTransferTransactionHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.MassTransferTransactionHandler.getRecipientPublicKeyHashFromMassTransfer;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.getTransactionVersion;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class GrpcMassTransferCheckers {
    private final int txIndex;
    private final String senderAddress;
    private final String senderPublicKey;

    private final List<Account> accountList;
    private final MassTransferTransactionSender txSender;


    public GrpcMassTransferCheckers(int txIndex, Account sender, MassTransferTransactionSender txSender) {
        this.txIndex = txIndex;
        this.txSender = txSender;
        this.accountList = txSender.getAccounts();

        senderAddress = sender.address().toString();
        senderPublicKey = sender.publicKey().toString();
    }

    public void checkMassTransferGrpc(long amountValue, long balanceBefore) {
        assertAll(
                () -> assertThat(getChainId(txIndex)).isEqualTo(CHAIN_ID),
                () -> assertThat(getSenderPublicKeyFromTransaction(txIndex)).isEqualTo(senderPublicKey),
                () -> assertThat(getTransactionFeeAmount(txIndex)).isEqualTo(getTransactionCommission()),
                () -> assertThat(getTransactionVersion(txIndex)).isEqualTo(LATEST_VERSION),
                () -> assertThat(getAttachmentFromMassTransfer(txIndex)).isEqualTo(txSender.getAttach().toString()),
                () -> assertThat(getAssetIdFromMassTransfer(txIndex)).isEqualTo(txSender.getAssetId()),
                () -> checkBalances(amountValue, balanceBefore)
        );

    }

    private void checkBalances(long amountValue, long balanceBefore) {
        for (int i = 0; i < accountList.size(); i++) {
            long recipientAmount = getRecipientAmountFromMassTransfer(txIndex, i);
            String publicKeyHash = publicKeyHashFromList(i);
            String accountAddress = accountAddressFromList(i);
            String addressFromBalance = getAddress(txIndex, i);
            String assetFromBalance = getAssetIdAmountAfter(txIndex, i);

            assertThat(recipientAmount).isEqualTo(amountValue);
            assertThat(getRecipientPublicKeyHashFromMassTransfer(txIndex, i)).isEqualTo(publicKeyHash);
            assertThat(getMassTransferFromTransactionMetadata(txIndex, i)).isEqualTo(accountAddress);


            if (accountAddress.equals(addressFromBalance)) {
                assertThat(getAmountBefore(txIndex, i)).isEqualTo(0);
                assertThat(getAmountAfter(txIndex, i)).isEqualTo(amountValue);
            }

            if (senderAddress.equals(addressFromBalance) && txSender.getAssetId().equals(assetFromBalance)) {
                assertThat(getAmountBefore(txIndex, i)).isEqualTo(txSender.getSenderBalanceBeforeMassTransfer());
                assertThat(getAmountAfter(txIndex, i)).isEqualTo(txSender.getSenderBalanceAfterMassTransfer());
            } else if (!txSender.getAssetId().equals(assetFromBalance)) {
                assertThat(getAmountBefore(txIndex, i)).isEqualTo(balanceBefore);
                assertThat(getAmountAfter(txIndex, i)).isEqualTo(txSender.getSenderWavesBalanceAfterMassTransfer());
            }
        }
    }

    private String publicKeyHashFromList(int index) {
        return Base58.encode(accountList.get(index).address().publicKeyHash());
    }

    private String accountAddressFromList(int index) {
        return accountList.get(index).address().toString();
    }
}
