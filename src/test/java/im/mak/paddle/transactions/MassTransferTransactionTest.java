package im.mak.paddle.transactions;

import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.helpers.dapps.DefaultDApp420Complexity;
import im.mak.paddle.helpers.transaction_senders.MassTransferTransactionSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

import static com.wavesplatform.transactions.MassTransferTransaction.LATEST_VERSION;
import static com.wavesplatform.transactions.common.AssetId.WAVES;
import static com.wavesplatform.wavesj.ApplicationStatus.SUCCEEDED;
import static im.mak.paddle.helpers.Calculations.getTransactionCommission;
import static im.mak.paddle.helpers.Randomizer.*;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MassTransferTransactionTest {
    private static Account account;

    private static AssetId issuedAsset;
    private static List<Account> minimumAccountsList;
    private static List<Account> maximumAccountsList;

    private static DefaultDApp420Complexity dAppAccount;
    private static AssetId issuedSmartAssetId;
    private static AssetId checkedAsset;

    @BeforeAll
    static void before() {
        async(
                () -> {
                    account = new Account(DEFAULT_FAUCET);
                    issuedAsset = account.issue(i -> i.name("Test_Asset").quantity(900_000_000_000L).script(null)
                    ).tx().assetId();
                },
                () -> {
                    dAppAccount = new DefaultDApp420Complexity(DEFAULT_FAUCET);
                    dAppAccount.createAlias(randomNumAndLetterString(15));
                    issuedSmartAssetId = dAppAccount.issue(i -> i.name("Smart")
                            .quantity(900_000_000_000L)
                            .script(SCRIPT_PERMITTING_OPERATIONS)).tx().assetId();
                },
                () -> minimumAccountsList = accountListGenerator(MIN_NUM_ACCOUNT_FOR_MASS_TRANSFER),
                () -> maximumAccountsList = accountListGenerator(MAX_NUM_ACCOUNT_FOR_MASS_TRANSFER)
        );
    }

    @Test
    @DisplayName("for maximum Accounts")
    void massTransferForMaximumCountAccounts() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            int amount = getRandomInt(MIN_TRANSACTION_SUM, 100);

            MassTransferTransactionSender txSender =
                    new MassTransferTransactionSender(account, WAVES, amount, maximumAccountsList);
            checkedAsset = AssetId.as(txSender.getAssetId());
            txSender.massTransferTransactionSender(v);
            checkMassTransferTransaction(txSender);
        }
    }

    @Test
    @DisplayName("for minimum Accounts")
    void massTransferForMinimumCountAccounts() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            int amount = getRandomInt(MIN_TRANSACTION_SUM, 100);

            MassTransferTransactionSender txSender =
                    new MassTransferTransactionSender(account, WAVES, amount, minimumAccountsList);
            checkedAsset = AssetId.as(txSender.getAssetId());
            txSender.massTransferTransactionSender(v);
            checkMassTransferTransaction(txSender);
        }
    }

    @Test
    @DisplayName("issued asset for maximum Accounts")
    void massTransferForMaximumAccountsForIssueAsset() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            int amount = getRandomInt(MIN_TRANSACTION_SUM, 100);

            MassTransferTransactionSender txSender =
                    new MassTransferTransactionSender(account, issuedAsset, amount, maximumAccountsList);
            checkedAsset = AssetId.as(txSender.getAssetId());
            txSender.massTransferTransactionSender(v);
            checkMassTransferTransaction(txSender);
        }
    }

    @Test
    @DisplayName("issued asset for minimum Accounts")
    void massTransferForMinimumAccountsForIssueAsset() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            int amount = getRandomInt(MIN_TRANSACTION_SUM, 100);

            MassTransferTransactionSender txSender =
                    new MassTransferTransactionSender(account, issuedAsset, amount, minimumAccountsList);
            checkedAsset = AssetId.as(txSender.getAssetId());
            txSender.massTransferTransactionSender(v);
            checkMassTransferTransaction(txSender);
        }
    }

    @Test
    @DisplayName("issued smart asset for maximum Accounts")
    void massTransferForMaximumAccountsForIssueSmartAsset() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            int amount = getRandomInt(MIN_TRANSACTION_SUM, 100);

            MassTransferTransactionSender txSender =
                    new MassTransferTransactionSender(dAppAccount, issuedSmartAssetId, amount, maximumAccountsList);
            checkedAsset = AssetId.as(txSender.getAssetId());
            txSender.massTransferTransactionSender(v);
            checkMassTransferTransaction(txSender);
        }
    }

    @Test
    @DisplayName("issued smart asset for minimum Accounts")
    void massTransferForMinimumAccountsForIssueSmartAsset() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            int amount = getRandomInt(MIN_TRANSACTION_SUM, 100);

            MassTransferTransactionSender txSender =
                    new MassTransferTransactionSender(dAppAccount, issuedSmartAssetId, amount, minimumAccountsList);
            checkedAsset = AssetId.as(txSender.getAssetId());
            txSender.massTransferTransactionSender(v);
            checkMassTransferTransaction(txSender);
        }
    }

    private void checkMassTransferTransaction(MassTransferTransactionSender txSender) {
        assertAll(
                () -> assertThat(txSender.getTxInfo().applicationStatus()).isEqualTo(SUCCEEDED),
                () -> assertThat(txSender.getSender().getBalance(checkedAsset))
                        .isEqualTo(txSender.getSenderBalanceAfterMassTransfer()),
                () -> assertThat(txSender.getMassTransferTx().attachment()).isEqualTo(txSender.getAttach()),
                () -> assertThat(txSender.getMassTransferTx().assetId().toString()).isEqualTo(checkedAsset.toString()),
                () -> assertThat(txSender.getMassTransferTx().fee().assetId()).isEqualTo(WAVES),
                () -> assertThat(txSender.getMassTransferTx().fee().value()).isEqualTo(getTransactionCommission()),
                () -> assertThat(txSender.getMassTransferTx().sender()).isEqualTo(txSender.getSender().publicKey()),
                () -> assertThat(txSender.getMassTransferTx().transfers().size()).isEqualTo(txSender.getAccountsSize()),
                () -> assertThat(txSender.getMassTransferTx().type()).isEqualTo(11),
                () -> txSender.getMassTransferTx().transfers().forEach(
                        transfer -> assertThat(transfer.amount()).isEqualTo(txSender.getAmount())),
                () -> txSender.getAccounts().forEach(account ->
                        assertThat(txSender.getBalancesAfterTransaction().get(account.address()))
                                .isEqualTo(account.getBalance(checkedAsset)))
        );
    }
}


