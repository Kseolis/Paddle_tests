package im.mak.paddle.e2e.transactions;

import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.common.Id;
import im.mak.paddle.Account;
import im.mak.paddle.helpers.transaction_senders.LeaseTransactionSender;
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

            LeaseTransactionSender txSender = new LeaseTransactionSender(stan, eric);

            txSender.leaseCancelTransactionSender(minLeaseTx, MIN_TRANSACTION_SUM, MIN_FEE, v);

            checkCancelLeaseTransaction(txSender);
        }
    }

    @Test
    @DisplayName("cancel lease of the maximum available amount")
    void leaseMaxAssets() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            long leaseSum = kenny.getWavesBalance() - MIN_FEE;
            Id maxLeaseTx = kenny.lease(kyle, leaseSum).tx().id();

            LeaseTransactionSender txSender = new LeaseTransactionSender(kenny, kyle);

            txSender.leaseCancelTransactionSender(maxLeaseTx, leaseSum, MIN_FEE, v);

            checkCancelLeaseTransaction(txSender);
        }
    }

    private void checkCancelLeaseTransaction(LeaseTransactionSender txSender) {
        assertAll(
                () -> assertThat(txSender.getTxInfo().applicationStatus()).isEqualTo(SUCCEEDED),
                () -> assertThat(txSender.getLeaseCancelTx().fee().assetId()).isEqualTo(AssetId.WAVES),
                () -> assertThat(txSender.getLeaseCancelTx().fee().value()).isEqualTo(MIN_FEE),
                () -> assertThat(txSender.getLeaseCancelTx().sender()).isEqualTo(txSender.getFrom().publicKey()),
                () -> assertThat(txSender.getFrom().getWavesBalanceDetails().effective())
                        .isEqualTo(txSender.getEffectiveBalanceAfterSendTransaction()),
                () -> assertThat(txSender.getTo().getWavesBalanceDetails().effective())
                        .isEqualTo(txSender.getBalanceAfterReceiving()),
                () -> assertThat(txSender.getLeaseCancelTx().type()).isEqualTo(9)
        );
    }
}
