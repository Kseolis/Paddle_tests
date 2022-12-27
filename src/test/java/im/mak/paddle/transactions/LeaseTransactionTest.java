package im.mak.paddle.transactions;

import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.helpers.dapps.DefaultDApp420Complexity;
import im.mak.paddle.helpers.transaction_senders.LeaseTransactionSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.LeaseTransaction.LATEST_VERSION;
import static com.wavesplatform.wavesj.ApplicationStatus.SUCCEEDED;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LeaseTransactionTest {
    private Account alice;
    private Account bob;
    private Account karl;
    private Account sara;
    private DefaultDApp420Complexity smartAcc;
    private LeaseTransactionSender txSender;

    @BeforeEach
    void before() {
        async(
                () -> alice = new Account(ONE_WAVES),
                () -> bob = new Account(ONE_WAVES),
                () -> karl = new Account(ONE_WAVES),
                () -> sara = new Account(ONE_WAVES),
                () -> smartAcc = new DefaultDApp420Complexity(ONE_WAVES)
        );
    }

    @Test
    @DisplayName("Minimum lease sum transaction")
    void leaseMinimumWavesAssets() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            txSender = new LeaseTransactionSender(sara, karl, MIN_FEE);
            txSender.leaseTransactionSender(MIN_TRANSACTION_SUM, v);
            leaseTransactionCheck(MIN_TRANSACTION_SUM, MIN_FEE);
        }
    }

    @Test
    @DisplayName("lease asset transaction random WAVES")
    void leaseOneWavesAssets() {
        long amount = getRandomInt(1000, 1_000_000);
        for (int v = 1; v <= LATEST_VERSION; v++) {
            txSender = new LeaseTransactionSender(bob, alice, MIN_FEE);
            txSender.leaseTransactionSender(amount, v);
            leaseTransactionCheck(amount, MIN_FEE);
        }
    }

    @Test
    @DisplayName("Maximum lease sum transaction")
    void leaseMaximumAssets() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            long amount = karl.getWavesBalance() - MIN_FEE;
            txSender = new LeaseTransactionSender(karl, bob, MIN_FEE);
            txSender.leaseTransactionSender(amount, LATEST_VERSION);
            leaseTransactionCheck(amount, MIN_FEE);
            karl.cancelLease(txSender.getLeaseTx().id());
        }
    }

    @Test
    @DisplayName("Maximum lease sum transaction from DApp account")
    void leaseFromDAppAccount() {
        long amount = smartAcc.getWavesBalance() - SUM_FEE;
        txSender = new LeaseTransactionSender(smartAcc, bob, SUM_FEE);
        txSender.leaseTransactionSender(amount, LATEST_VERSION);
        leaseTransactionCheck(amount, SUM_FEE);
    }

    private void leaseTransactionCheck(long amount, long fee) {
        assertAll(
                () -> assertThat(txSender.getTxInfo().applicationStatus()).isEqualTo(SUCCEEDED),
                () -> assertThat(txSender.getLeaseTx().sender()).isEqualTo(txSender.getFrom().publicKey()),
                () -> assertThat(txSender.getLeaseTx().recipient()).isEqualTo(txSender.getTo().address()),
                () -> assertThat(txSender.getLeaseTx().amount()).isEqualTo(amount),
                () -> assertThat(txSender.getLeaseTx().fee().assetId()).isEqualTo(AssetId.WAVES),
                () -> assertThat(txSender.getLeaseTx().fee().value()).isEqualTo(fee),
                () -> assertThat(txSender.getFrom().getWavesBalanceDetails().effective())
                        .isEqualTo(txSender.getEffectiveBalanceAfterSendTransaction()),
                () -> assertThat(txSender.getTo().getWavesBalanceDetails().effective())
                        .isEqualTo(txSender.getBalanceAfterReceiving()),
                () -> assertThat(txSender.getLeaseTx().type()).isEqualTo(8)
        );
    }
}
