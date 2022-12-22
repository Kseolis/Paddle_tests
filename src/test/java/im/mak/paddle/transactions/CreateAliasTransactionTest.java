package im.mak.paddle.transactions;

import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.helpers.dapps.DefaultDApp420Complexity;
import im.mak.paddle.helpers.transaction_senders.CreateAliasTransactionSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.CreateAliasTransaction.LATEST_VERSION;
import static com.wavesplatform.wavesj.ApplicationStatus.SUCCEEDED;
import static im.mak.paddle.helpers.Randomizer.randomNumAndLetterString;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.Constants.MIN_FEE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class CreateAliasTransactionTest {
    private static Account account;
    private static String accountAlias;
    private static DefaultDApp420Complexity dAppAccount;

    @BeforeAll
    static void before() {
        async(
                () -> dAppAccount = new DefaultDApp420Complexity(DEFAULT_FAUCET),
                () -> account = new Account(DEFAULT_FAUCET)
        );
    }

    @Test
    @DisplayName("test create minimally short alias")
    void createMinShortAlias() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            accountAlias = randomNumAndLetterString(4);
            CreateAliasTransactionSender txSender =
                    new CreateAliasTransactionSender(account, accountAlias, MIN_FEE, v);

            txSender.createAliasTransactionSender();
            checkAssertsForCreateAliasTransaction(accountAlias, account, MIN_FEE, txSender);
        }
    }

    @Test
    @DisplayName("test create maximum long alias")
    void createMaxLongAlias() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            accountAlias = randomNumAndLetterString(30);
            CreateAliasTransactionSender txSender =
                    new CreateAliasTransactionSender(account, accountAlias, MIN_FEE, v);

            txSender.createAliasTransactionSender();
            checkAssertsForCreateAliasTransaction(accountAlias, account, MIN_FEE, txSender);
        }
    }

    @Test
    @DisplayName("test create alias for DApp account")
    void createAliasForDAppAccount() {
        accountAlias = randomNumAndLetterString(17);
        CreateAliasTransactionSender txSender =
                new CreateAliasTransactionSender(dAppAccount, accountAlias, SUM_FEE, LATEST_VERSION);

        txSender.createAliasTransactionSender();
        checkAssertsForCreateAliasTransaction(accountAlias, dAppAccount, SUM_FEE, txSender);
    }

    private void checkAssertsForCreateAliasTransaction
            (String alias, Account acc, long fee, CreateAliasTransactionSender txSender) {

        assertThat(acc.getWavesBalance()).isEqualTo(txSender.getBalanceAfterTransaction());

        assertAll(
                () -> assertThat(txSender.getTxInfo().applicationStatus()).isEqualTo(SUCCEEDED),
                () -> assertThat(txSender.getCreateAliasTx().sender()).isEqualTo(acc.publicKey()),
                () -> assertThat(txSender.getCreateAliasTx().alias().name()).isEqualTo(alias),
                () -> assertThat(txSender.getCreateAliasTx().fee().assetId()).isEqualTo(AssetId.WAVES),
                () -> assertThat(txSender.getCreateAliasTx().fee().value()).isEqualTo(fee),
                () -> assertThat(txSender.getCreateAliasTx().type()).isEqualTo(10)
        );
    }
}
