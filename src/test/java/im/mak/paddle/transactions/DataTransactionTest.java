package im.mak.paddle.transactions;

import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.common.Base64String;
import com.wavesplatform.transactions.data.*;
import im.mak.paddle.Account;
import im.mak.paddle.helpers.transaction_senders.DataTransactionsSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.DataTransaction.LATEST_VERSION;
import static com.wavesplatform.wavesj.ApplicationStatus.SUCCEEDED;
import static im.mak.paddle.helpers.Randomizer.randomNumAndLetterString;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class DataTransactionTest {

    private static Account account;

    private final Base64String base64String = new Base64String(randomNumAndLetterString(6));
    private final BinaryEntry binaryEntry = BinaryEntry.as("BinEntry", base64String);
    private final BooleanEntry booleanEntry = BooleanEntry.as("Boolean", true);
    private final IntegerEntry integerEntry = IntegerEntry.as("Integer", 210);
    private final StringEntry stringEntry = StringEntry.as("String", "string");

    @BeforeAll
    static void before() {
        account = new Account(DEFAULT_FAUCET);
    }

    @Test
    @DisplayName("transaction of all data types on dataTransaction")
    void allTypesDataTransactionTest() {
        DataEntry[] dataEntries = new DataEntry[]{binaryEntry, booleanEntry, integerEntry, stringEntry};
        for (int v = 1; v <= LATEST_VERSION; v++) {
            final DataTransactionsSender txSender = new DataTransactionsSender(account, dataEntries);
            txSender.dataEntryTransactionSender(account, v);
            checkAssertsForDataTransaction(txSender);
        }
    }

    @Test
    @DisplayName("transaction integer dataTransaction")
    void intTypeDataTransactionTest() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            final DataTransactionsSender txSender = new DataTransactionsSender(account, integerEntry);
            txSender.dataEntryTransactionSender(account, v);
            checkAssertsForDataTransaction(txSender);
        }
    }

    @Test
    @DisplayName("transaction string dataTransaction")
    void stringTypeDataTransactionTest() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            final DataTransactionsSender txSender = new DataTransactionsSender(account, stringEntry);
            txSender.dataEntryTransactionSender(account, v);
            checkAssertsForDataTransaction(txSender);
        }
    }

    @Test
    @DisplayName("transaction binary dataTransaction")
    void binaryTypeDataTransactionTest() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            final DataTransactionsSender txSender = new DataTransactionsSender(account, binaryEntry);
            txSender.dataEntryTransactionSender(account, v);
            checkAssertsForDataTransaction(txSender);
        }
    }

    @Test
    @DisplayName("transaction boolean dataTransaction")
    void booleanTypeDataTransactionTest() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            final DataTransactionsSender txSender = new DataTransactionsSender(account, booleanEntry);
            txSender.dataEntryTransactionSender(account, v);
            checkAssertsForDataTransaction(txSender);
        }
    }

    private void checkAssertsForDataTransaction(DataTransactionsSender txSender) {
        assertAll(
                () -> assertThat(txSender.getTxInfo().applicationStatus()).isEqualTo(SUCCEEDED),
                () -> assertThat(txSender.getSender().getWavesBalance()).isEqualTo(txSender.getBalanceAfterTransaction()),
                () -> assertThat(txSender.getDataTx().fee().value()).isEqualTo(MIN_FEE),
                () -> assertThat(txSender.getDataTx().fee().assetId()).isEqualTo(AssetId.WAVES),
                () -> assertThat(txSender.getDataTx().sender()).isEqualTo(txSender.getSender().publicKey()),
                () -> assertThat(txSender.getDataTx().type()).isEqualTo(12),
                () -> txSender.getDataTx().data().forEach(
                        data -> assertThat(txSender.getDataTxEntryMap().get(data.key())).isEqualTo(data.type())
                )
        );
    }
}
