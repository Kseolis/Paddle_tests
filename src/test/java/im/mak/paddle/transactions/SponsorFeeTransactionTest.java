package im.mak.paddle.transactions;

import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.helpers.dapps.DefaultDApp420Complexity;
import im.mak.paddle.helpers.transaction_senders.SponsorFeeTransactionSender;
import im.mak.paddle.helpers.transaction_senders.TransferTransactionSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.SponsorFeeTransaction.LATEST_VERSION;
import static com.wavesplatform.wavesj.ApplicationStatus.SUCCEEDED;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class SponsorFeeTransactionTest {
    private static Account acc;

    private static DefaultDApp420Complexity assetOwner;
    private static AssetId assetOwnerAssetId;

    private static Account alice;
    private static AssetId aliceAssetId;

    @BeforeAll
    static void before() {
        async(
                () -> {
                    alice = new Account(DEFAULT_FAUCET);
                    aliceAssetId = alice.issue(i -> i.name("Alice_Asset").quantity(1000L).decimals(8)).tx().assetId();
                },
                () -> {
                    assetOwner = new DefaultDApp420Complexity(DEFAULT_FAUCET);
                    assetOwnerAssetId = assetOwner.issue(i -> i.name("Bob_Asset").quantity(1000L).decimals(8)).tx().assetId();
                },
                () -> acc = new Account(DEFAULT_FAUCET)
        );
        alice.transfer(assetOwner, alice.getBalance(aliceAssetId) / 2, aliceAssetId);
        assetOwner.transfer(acc, assetOwner.getBalance(assetOwnerAssetId) / 2, assetOwnerAssetId);
    }

    @Test
    @DisplayName("Sponsor transaction with minimal sponsored fee")
    void sponsorMinAssets() {
        long sponsorFee = 1;
        for (int v = 1; v <= LATEST_VERSION; v++) {
            long amountValue = assetOwner.getBalance(aliceAssetId) / 2;

            Amount amount = Amount.of(amountValue, aliceAssetId);

            SponsorFeeTransactionSender txSender = new SponsorFeeTransactionSender(alice, sponsorFee, aliceAssetId);
            txSender.sponsorFeeTransactionSender(SUM_FEE, v);

            TransferTransactionSender transferTxSender = new TransferTransactionSender(amount, assetOwner, acc, SUM_FEE);
            transferTxSender.transferTransactionSender(ADDRESS, 2);

            checkSponsorTransaction(txSender, transferTxSender, sponsorFee, SUM_FEE);
        }
    }

    @Test
    @DisplayName("Sponsor transaction with dApp account fee")
    void sponsorDAppAccAssets() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            long sponsorFee = getRandomInt(10, 50);
            long amountValue = acc.getBalance(assetOwnerAssetId) - sponsorFee;
            Amount amount = Amount.of(amountValue, assetOwnerAssetId);

            SponsorFeeTransactionSender txSender = new SponsorFeeTransactionSender(assetOwner, sponsorFee, assetOwnerAssetId);
            txSender.sponsorFeeTransactionSender(SUM_FEE, v);

            TransferTransactionSender transferTxSender = new TransferTransactionSender(amount, acc, alice, SUM_FEE);
            transferTxSender.transferTransactionSender(ADDRESS, v);

            checkSponsorTransaction(txSender, transferTxSender, sponsorFee, SUM_FEE);

            assetOwner.reissue(500, assetOwnerAssetId);
            assetOwner.transfer(acc, assetOwner.getBalance(assetOwnerAssetId) / 2, assetOwnerAssetId);
        }
    }

    @Test
    @DisplayName("Cancel sponsored fee")
    void cancelAliceSponsorFee() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            SponsorFeeTransactionSender txSender = new SponsorFeeTransactionSender(assetOwner, 100, assetOwnerAssetId);
            txSender.sponsorFeeTransactionSender(SUM_FEE, v);

            txSender.cancelSponsorFeeSender(assetOwner, alice, acc, v, EXTRA_FEE);
            checkCancelSponsorFee(txSender);
        }
    }

    private void checkSponsorTransaction
            (SponsorFeeTransactionSender txSender, TransferTransactionSender transferTxSender, long sponsorFee, long fee) {
        assertAll(
                () -> assertThat(txSender.getTxInfo().applicationStatus()).isEqualTo(SUCCEEDED),
                () -> assertThat(txSender.getSponsorTx().sender()).isEqualTo(txSender.getAssetOwner().publicKey()),
                () -> assertThat(txSender.getSponsorTx().assetId()).isEqualTo(txSender.getAssetId()),
                () -> assertThat(txSender.getSponsorTx().fee().value()).isEqualTo(fee),
                () -> assertThat(txSender.getSponsorTx().minSponsoredFee()).isEqualTo(sponsorFee),
                () -> assertThat(txSender.getSponsorTx().type()).isEqualTo(14),

                () -> assertThat(transferTxSender.getTxInfo().applicationStatus()).isEqualTo(SUCCEEDED),
                () -> assertThat(transferTxSender.getTransferTx().sender())
                        .isEqualTo(transferTxSender.getSender().publicKey()),
                () -> assertThat(transferTxSender.getTransferTx().fee().assetId()).isEqualTo(AssetId.WAVES),
                () -> assertThat(transferTxSender.getTransferTx().fee().value()).isEqualTo(SUM_FEE),

                () -> assertThat(transferTxSender.getSender().getBalance(transferTxSender.getAsset()))
                        .isEqualTo(transferTxSender.getSenderBalanceAfterTransaction()),
                () -> assertThat(transferTxSender.getRecipient().getBalance(transferTxSender.getAsset()))
                        .isEqualTo(transferTxSender.getRecipientBalanceAfterTransaction()),

                () -> assertThat(transferTxSender.getSender().getWavesBalance())
                        .isEqualTo(transferTxSender.getSenderWavesBalanceAfterTransaction()),
                () -> assertThat(transferTxSender.getRecipient().getWavesBalance())
                        .isEqualTo(transferTxSender.getRecipientWavesBalanceAfterTransaction())
        );
    }

    private void checkCancelSponsorFee(SponsorFeeTransactionSender txSender) {
        assertAll(
                () -> assertThat(txSender.getTxInfo().applicationStatus()).isEqualTo(SUCCEEDED),
                () -> assertThat(txSender.getSponsorTx().sender()).isEqualTo(assetOwner.publicKey()),
                () -> assertThat(txSender.getSponsorTx().assetId()).isEqualTo(txSender.getAssetId()),
                () -> assertThat(txSender.getSponsorTx().minSponsoredFee()).isEqualTo(0),
                () -> assertThat(txSender.getSponsorTx().type()).isEqualTo(14)
        );
    }
}
