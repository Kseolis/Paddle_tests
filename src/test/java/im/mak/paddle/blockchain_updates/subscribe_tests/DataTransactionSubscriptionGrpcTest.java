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
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.getTransactionId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.DataEntries.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.DataTransactionHandler.getKeyFromDataTx;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.DataTransactionHandler.getIntValueFromDataTx;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.*;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class DataTransactionSubscriptionGrpcTest extends BaseGrpcTest {
    private String senderAddress;
    private String senderPublicKey;
    private Account senderAccount;
    private final Base64String base64String = new Base64String(randomNumAndLetterString(6));
    private final BinaryEntry binaryEntry = BinaryEntry.as("BinEntry", base64String);
    private final BooleanEntry booleanEntry = BooleanEntry.as("Boolean", true);
    private final IntegerEntry integerEntry = IntegerEntry.as("Integer", getRandomInt(100, 100000));
    private final StringEntry stringEntry = StringEntry.as("String", "string");

    @BeforeEach
    void setUp() {
        async(
                () -> {
                    senderAccount = new Account(DEFAULT_FAUCET);
                    senderAddress = senderAccount.address().toString();
                    senderPublicKey = senderAccount.publicKey().toString();
                }
        );
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

    private void checkDataTransactionSubscribe(DataTransactionsSender dataTxSender) {
        assertAll(
                () -> assertThat(getChainId(0)).isEqualTo(CHAIN_ID),
                () -> assertThat(getSenderPublicKeyFromTransaction(0)).isEqualTo(senderPublicKey),
                () -> assertThat(getTransactionFeeAmount(0)).isEqualTo(MIN_FEE),
                () -> assertThat(getTransactionVersion(0)).isEqualTo(LATEST_VERSION),
                () -> assertThat(getTransactionId()).isEqualTo(dataTxSender.getTxInfo().tx().id().toString()),

                () -> assertThat(getKeyFromDataTx(0, 0)).isEqualTo(integerEntry.key()),
                () -> assertThat(getIntValueFromDataTx(0, 0)).isEqualTo(integerEntry.value()),
                // check waves balance
                () -> assertThat(getAddress(0, 0)).isEqualTo(senderAddress),
                () -> assertThat(getAmountBefore(0, 0)).isEqualTo(DEFAULT_FAUCET),
                () -> assertThat(getAmountAfter(0, 0)).isEqualTo(DEFAULT_FAUCET - MIN_FEE)
        );
    }

    private void checkDataEntries(DataTransaction dataTx) {
        assertAll(
                () -> dataTx.data().forEach(
                        data -> assertThat
                                (getBeforeDataEntries(0, dataTx.data().indexOf(data)).getKey())
                                .isEqualTo(data.key())
                ),
                () -> dataTx.data().forEach(
                        data -> assertThat(
                                (getBeforeDataEntries(0, dataTx.data().indexOf(data))
                                        .getValueCase()
                                        .toString()))
                                .isEqualTo("VALUE_NOT_SET")
                ),
                () -> dataTx.data().forEach(
                        data -> assertThat(
                                (getSenderAddress(0, dataTx.data().indexOf(data))))
                                .isEqualTo(senderAddress)
                ),
                () -> dataTx.data().forEach(
                        data -> assertThat(
                                (getAfterKeyForStateUpdates(0, dataTx.data().indexOf(data))))
                                .isEqualTo(data.key())
                ),
                () -> assertThat(getAfterIntValueForStateUpdates(0, 0)).isEqualTo(integerEntry.value()),
                () -> assertThat(getAfterByteValueForStateUpdates(0, 1)).isEqualTo(binaryEntry.value().toString()),
                () -> assertThat(getAfterBoolValueForStateUpdates(0, 2)).isEqualTo(booleanEntry.value()),
                () -> assertThat(getAfterStringValueForStateUpdates(0, 3)).isEqualTo(stringEntry.value())
        );
    }
}
