package im.mak.paddle.e2e.transactions;

import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.helpers.transaction_senders.BurnTransactionSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.BurnTransaction.LATEST_VERSION;
import static com.wavesplatform.wavesj.ApplicationStatus.SUCCEEDED;
import static im.mak.paddle.helpers.Randomizer.randomNumAndLetterString;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class BurnTransactionTest {
    private static Account account;
    private static AssetId issuedAsset;
    private static AssetId issuedSmartAsset;

    @BeforeAll
    static void before() {
        account = new Account(DEFAULT_FAUCET);
        async(
                () -> account.createAlias(randomNumAndLetterString(15)),
                () -> issuedAsset = account.issue(i -> i.name("Test_Asset").quantity(1000)).tx().assetId(),
                () -> issuedSmartAsset =
                        account.issue(i -> i.name("T_Smart_Asset")
                                .quantity(1000)
                                .script("2 * 2 == 4")).tx().assetId()
        );
    }

    @Test
    @DisplayName("burn minimum quantity asset")
    void burnMinimumAssets() {
        Amount amount = Amount.of(ASSET_QUANTITY_MIN, issuedAsset);

        for (int v = 1; v < LATEST_VERSION; v++) {
            BurnTransactionSender txSender = new BurnTransactionSender(account, amount, MIN_FEE, v);
            txSender.burnTransactionSender();
            checkAssertsForBurnTransaction(issuedAsset, txSender);
        }
    }

    @Test
    @DisplayName("burn almost maximum quantity asset")
    void burnMaximumAssets() {
        long burnSum = account.getAssetBalance(issuedAsset);
        Amount amount = Amount.of(burnSum, issuedAsset);

        for (int v = 1; v < LATEST_VERSION; v++) {
            BurnTransactionSender txSender = new BurnTransactionSender(account, amount, MIN_FEE, v);
            txSender.burnTransactionSender();
            checkAssertsForBurnTransaction(issuedAsset, txSender);
            account.reissue(1000, issuedAsset);
        }
    }

    @Test
    @DisplayName("burn minimum quantity smart asset")
    void burnMinimumSmartAssets() {
        Amount amount = Amount.of(ASSET_QUANTITY_MIN, issuedSmartAsset);

        for (int v = 1; v < LATEST_VERSION; v++) {
            BurnTransactionSender txSender = new BurnTransactionSender(account, amount, SUM_FEE, v);
            txSender.burnTransactionSender();
            checkAssertsForBurnTransaction(issuedSmartAsset, txSender);
        }
    }

    @Test
    @DisplayName("burn almost maximum quantity smart asset")
    void burnMaximumSmartAssets() {
        long burnSum = account.getAssetBalance(issuedSmartAsset);
        Amount amount = Amount.of(burnSum, issuedSmartAsset);

        for (int v = 1; v < LATEST_VERSION; v++) {
            BurnTransactionSender txSender = new BurnTransactionSender(account, amount, SUM_FEE, v);
            txSender.burnTransactionSender();
            checkAssertsForBurnTransaction(issuedSmartAsset, txSender);
            account.reissue(1000, issuedSmartAsset);
        }
    }

    private void checkAssertsForBurnTransaction(AssetId assetId, BurnTransactionSender txSender) {
        assertAll(
                () -> assertThat(txSender.getTxInfo().applicationStatus()).isEqualTo(SUCCEEDED),
                () -> assertThat(txSender.getSender().getAssetBalance(assetId))
                        .isEqualTo(txSender.getAssetBalanceAfterTransaction()),
                () -> assertThat(txSender.getSender().getWavesBalance()).isEqualTo(txSender.getBalanceAfterTransaction()),
                () -> assertThat(txSender.getBurnTx().fee().assetId()).isEqualTo(AssetId.WAVES),
                () -> assertThat(txSender.getBurnTx().fee().value()).isEqualTo(txSender.getFee()),
                () -> assertThat(txSender.getBurnTx().amount().value()).isEqualTo(txSender.getAmount().value()),
                () -> assertThat(txSender.getBurnTx().amount().assetId()).isEqualTo(assetId),
                () -> assertThat(txSender.getBurnTx().sender()).isEqualTo(account.publicKey()),
                () -> assertThat(txSender.getBurnTx().type()).isEqualTo(6)
        );
    }
}
