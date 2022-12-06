package im.mak.paddle.transactions;

import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.helpers.dapps.DefaultDApp420Complexity;
import im.mak.paddle.helpers.transaction_senders.LeaseTransactionSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.LeaseTransaction.LATEST_VERSION;
import static com.wavesplatform.wavesj.ApplicationStatus.SUCCEEDED;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class LeaseTransactionTest {
    private static Account alice;
    private static Account bob;
    private static DefaultDApp420Complexity smartAcc;

    @BeforeAll
    static void before() {
        async(
                () -> alice = new Account(DEFAULT_FAUCET),
                () -> bob = new Account(DEFAULT_FAUCET),
                () -> smartAcc = new DefaultDApp420Complexity(DEFAULT_FAUCET)
        );
    }

    @Test
    @DisplayName("Minimum lease sum transaction")
    void leaseMinimumWavesAssets() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            LeaseTransactionSender txSender = new LeaseTransactionSender(bob, alice, MIN_FEE);
            txSender.leaseTransactionSender(MIN_TRANSACTION_SUM, v);
            leaseTransactionCheck(MIN_TRANSACTION_SUM, MIN_FEE, txSender);
        }
    }

    @Test
    @DisplayName("lease asset transaction random WAVES")
    void leaseOneWavesAssets() {
        long amount = getRandomInt(1000, 1_000_000);
        for (int v = 1; v <= LATEST_VERSION; v++) {
            LeaseTransactionSender txSender = new LeaseTransactionSender(bob, alice, MIN_FEE);
            txSender.leaseTransactionSender(amount, v);
            leaseTransactionCheck(amount, MIN_FEE, txSender);
        }
    }

    @Test
    @DisplayName("Maximum lease sum transaction")
    void leaseMaximumAssets() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            long amount = alice.getWavesBalance() - MIN_FEE;
            LeaseTransactionSender txSender = new LeaseTransactionSender(alice, bob, MIN_FEE);
            txSender.leaseTransactionSender(amount, v);
            leaseTransactionCheck(amount, MIN_FEE, txSender);
            alice.cancelLease(txSender.getLeaseTx().id());
        }
    }

    @Test
    @DisplayName("Maximum lease sum transaction from DApp account")
    void leaseFromDAppAccount() {
        long amount = smartAcc.getWavesBalance() - SUM_FEE;
        LeaseTransactionSender txSender = new LeaseTransactionSender(smartAcc, bob, SUM_FEE);
        txSender.leaseTransactionSender(amount, LATEST_VERSION);
        leaseTransactionCheck(amount, SUM_FEE, txSender);
        node().faucet().transfer(smartAcc, DEFAULT_FAUCET, AssetId.WAVES);
    }

    private void leaseTransactionCheck(long amount, long fee, LeaseTransactionSender txSender) {
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