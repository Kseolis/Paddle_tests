package im.mak.paddle.transactions;

import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.common.Id;
import im.mak.paddle.Account;
import im.mak.paddle.helpers.transaction_senders.LeaseCancelTransactionSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.LeaseCancelTransaction.LATEST_VERSION;
import static com.wavesplatform.wavesj.ApplicationStatus.SUCCEEDED;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.Constants.MIN_FEE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LeaseCancelTransactionTest {
    private static Account stan;
    private static Account eric;
    private static Account kenny;
    private static Account kyle;

    @BeforeAll
    static void before() {
        async(
                () -> stan = new Account(DEFAULT_FAUCET),
                () -> eric = new Account(DEFAULT_FAUCET),
                () -> kenny = new Account(DEFAULT_FAUCET),
                () -> kyle = new Account(DEFAULT_FAUCET)
        );
    }

    @Test
    @DisplayName("cancel lease of the minimum available amount")
    void leaseMinAssets() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            Id minLeaseTx = stan.lease(eric, MIN_TRANSACTION_SUM).tx().id();

            LeaseCancelTransactionSender txSender = new LeaseCancelTransactionSender(stan, eric, MIN_FEE);

            txSender.leaseCancelTransactionSender(minLeaseTx, MIN_TRANSACTION_SUM, v);

            checkCancelLeaseTransaction(txSender);
        }
    }

    @Test
    @DisplayName("cancel lease of the maximum available amount")
    void leaseMaxAssets() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            long leaseSum = kenny.getWavesBalance() - MIN_FEE;
            Id maxLeaseTx = kenny.lease(kyle, leaseSum).tx().id();

            LeaseCancelTransactionSender txSender = new LeaseCancelTransactionSender(kenny, kyle, MIN_FEE);

            txSender.leaseCancelTransactionSender(maxLeaseTx, leaseSum, v);

            checkCancelLeaseTransaction(txSender);
        }
    }

    private void checkCancelLeaseTransaction(LeaseCancelTransactionSender txSender) {
        assertAll(
                () -> assertThat(txSender.getTxInfo().applicationStatus()).isEqualTo(SUCCEEDED),
                () -> assertThat(txSender.getLeaseCancelTx().fee().assetId()).isEqualTo(AssetId.WAVES),
                () -> assertThat(txSender.getLeaseCancelTx().fee().value()).isEqualTo(MIN_FEE),
                () -> assertThat(txSender.getLeaseCancelTx().sender()).isEqualTo(txSender.getFrom().publicKey()),
                () -> assertThat(txSender.getTo().getWavesBalanceDetails().effective())
                        .isEqualTo(txSender.getBalanceAfterReceiving()),
                () -> assertThat(txSender.getFrom().getWavesBalanceDetails().effective())
                        .isEqualTo(txSender.getEffectiveBalanceAfterSendTransaction()),
                () -> assertThat(txSender.getLeaseCancelTx().type()).isEqualTo(9)
        );
    }
}
