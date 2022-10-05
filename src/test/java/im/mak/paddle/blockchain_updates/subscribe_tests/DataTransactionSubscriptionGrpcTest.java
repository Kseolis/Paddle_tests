package im.mak.paddle.blockchain_updates.subscribe_tests;

import com.wavesplatform.transactions.DataTransaction;
import com.wavesplatform.transactions.common.Base64String;
import com.wavesplatform.transactions.data.*;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.helpers.transaction_senders.DataTransactionsSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.DataTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.helpers.Randomizer.randomNumAndLetterString;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.util.Constants.*;

public class DataTransactionSubscriptionGrpcTest extends BaseGrpcTest {
    private Account senderAccount;
    private final Base64String base64String = new Base64String(randomNumAndLetterString(6));
    private final BinaryEntry binaryEntry = BinaryEntry.as("BinEntry", base64String);
    private final BooleanEntry booleanEntry = BooleanEntry.as("Boolean", true);
    private final IntegerEntry integerEntry = IntegerEntry.as("Integer", getRandomInt(100, 100000));
    private final StringEntry stringEntry = StringEntry.as("String", "string");

    @BeforeEach
    void setUp() {
        senderAccount = new Account(DEFAULT_FAUCET);
    }

    @Test
    @DisplayName("Check subscription on data smart asset transaction")
    void subscribeTestForDataTransaction() {
        DataEntry[] dataEntries = new DataEntry[]{integerEntry, binaryEntry, booleanEntry, stringEntry};

        final DataTransactionsSender txSender = new DataTransactionsSender(senderAccount, dataEntries);

        txSender.dataEntryTransactionSender(senderAccount, LATEST_VERSION);
        String txId = txSender.getTxInfo().tx().id().toString();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);
        checkDataTransactionSubscribe(txSender);
        checkDataEntries(txSender.getDataTx());
    }

}
