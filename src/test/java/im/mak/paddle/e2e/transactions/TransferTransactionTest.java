package im.mak.paddle.e2e.transactions;

import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;

import im.mak.paddle.helpers.dapps.DefaultDApp420Complexity;
import im.mak.paddle.helpers.transaction_senders.TransferTransactionSender;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.TransferTransaction.LATEST_VERSION;
import static com.wavesplatform.transactions.common.AssetId.WAVES;
import static com.wavesplatform.wavesj.ApplicationStatus.SUCCEEDED;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Randomizer.randomNumAndLetterString;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class TransferTransactionTest {
    private static Account alice;
    private static long aliceBalance;

    private static Account bob;
    private static long bobBalance;

    private static DefaultDApp420Complexity dAppAccount;

    private static AssetId issuedAssetId;
    private static AssetId issuedSmartAssetId;

    @BeforeAll
    static void before() {
        async(
                () -> {
                    alice = new Account(DEFAULT_FAUCET);
                    alice.createAlias(randomNumAndLetterString(15));
                    issuedAssetId = alice.issue(i -> i.name("Test_Asset").quantity(1000).decimals(8)).tx().assetId();
                    aliceBalance = alice.getWavesBalance();
                },
                () -> {
                    bob = new Account(DEFAULT_FAUCET);
                    bob.createAlias(randomNumAndLetterString(15));
                    bobBalance = bob.getWavesBalance() - MIN_FEE;
                },
                () -> {
                    dAppAccount = new DefaultDApp420Complexity(DEFAULT_FAUCET);
                    dAppAccount.createAlias(randomNumAndLetterString(15));
                    issuedSmartAssetId = dAppAccount.issue(i -> i.name("T_smart")
                            .quantity(1000)
                            .decimals(8)
                            .script(SCRIPT_PERMITTING_OPERATIONS)).tx().assetId();
                }
        );
    }

    @Test
    @DisplayName("min transfer issued asset on address")
    void transferTransactionIssuedAssetByAddressTest() {
        Amount amount = Amount.of(MIN_TRANSACTION_SUM, issuedAssetId);
        for (int v = 1; v <= LATEST_VERSION; v++) {
            TransferTransactionSender txSender = new TransferTransactionSender(amount, alice, bob, MIN_FEE);

            txSender.transferTransactionSender(ADDRESS, v);
            checkTransferTransaction(txSender);
        }
    }

    @Test
    @DisplayName("min transfer WAVES on alias")
    void transferTransactionWavesByAliasTest() {
        Amount amount = Amount.of(MIN_TRANSACTION_SUM, WAVES);
        for (int v = 1; v <= LATEST_VERSION; v++) {
            TransferTransactionSender txSender = new TransferTransactionSender(amount, alice, bob, MIN_FEE);

            txSender.transferTransactionSender(ALIAS, v);
            checkTransferTransaction(txSender);
        }
    }

    @Test
    @DisplayName("transfer all WAVES on address")
    void transferTransactionWavesByAddressTest() {
        node().faucet().transfer(alice, DEFAULT_FAUCET, WAVES);
        Amount amount = Amount.of(aliceBalance - MIN_FEE, WAVES);

        TransferTransactionSender txSender = new TransferTransactionSender(amount, alice, bob, MIN_FEE);
        txSender.transferTransactionSender(ADDRESS, 1);

        checkTransferTransaction(txSender);
    }

    @Test
    @DisplayName("transfer all issued asset on alias")
    void transferTransactionIssuedAssetByAliasTest() {
        Amount amount = Amount.of(alice.getBalance(issuedAssetId), issuedAssetId);

        TransferTransactionSender txSender = new TransferTransactionSender(amount, alice, bob, MIN_FEE);
        txSender.transferTransactionSender(ALIAS, 3);

        checkTransferTransaction(txSender);
    }

    @Test
    @DisplayName("transfer all WAVES on alias")
    void transferTransactionAllWavesByAliasTest() {
        Amount amount = Amount.of(bobBalance - MIN_FEE, WAVES);
        TransferTransactionSender txSender = new TransferTransactionSender(amount, bob, alice, MIN_FEE);
        txSender.transferTransactionSender(ALIAS, LATEST_VERSION);
        checkTransferTransaction(txSender);
    }

    @Test
    @DisplayName("transfer minimum smart asset on address from dAppAccount high complexity")
    void transferMinSmartAsset() {
        Amount amount = Amount.of(MIN_TRANSACTION_SUM, issuedSmartAssetId);
        TransferTransactionSender txSender = new TransferTransactionSender(amount, dAppAccount, alice, FEE_FOR_DAPP_ACC);
        txSender.transferTransactionSender(ADDRESS, 2);
        checkTransferTransaction(txSender);
    }

    @Test
    @DisplayName("transfer almost all smart asset on alias for smart account")
    void transferMaxSmartAsset() {
        long transferSum = dAppAccount.getBalance(issuedSmartAssetId) - MIN_TRANSACTION_SUM;
        Amount amount = Amount.of(transferSum, issuedSmartAssetId);
        TransferTransactionSender txSender = new TransferTransactionSender(amount, dAppAccount, alice, FEE_FOR_DAPP_ACC);
        txSender.transferTransactionSender(ALIAS, 2);

        checkTransferTransaction(txSender);
    }

    private void checkTransferTransaction(TransferTransactionSender txSender) {
        assertAll(
                () -> assertThat(txSender.getTxInfo().applicationStatus()).isEqualTo(SUCCEEDED),
                () -> assertThat(txSender.getSender().getBalance(txSender.getAsset()))
                        .isEqualTo(txSender.getSenderBalanceAfterTransaction()),
                () -> assertThat(txSender.getRecipient().getBalance(txSender.getAsset()))
                        .isEqualTo(txSender.getRecipientBalanceAfterTransaction()),

                () -> assertThat(txSender.getTransferTx().fee().value()).isEqualTo(txSender.getFee()),
                () -> assertThat(txSender.getTransferTx().fee().assetId()).isEqualTo(WAVES),
                () -> assertThat(txSender.getTransferTx().attachment()).isEqualTo(txSender.getBase58StringAttachment()),
                () -> assertThat(txSender.getTransferTx().sender()).isEqualTo(txSender.getSender().publicKey()),
                () -> assertThat(txSender.getTransferTx().amount()).isEqualTo(txSender.getAmount()),
                () -> assertThat(txSender.getTransferTx().type()).isEqualTo(4)
        );
    }

    @AfterEach
    void after() {
        aliceBalance = alice.getWavesBalance();
        bobBalance = bob.getWavesBalance();
    }
}
