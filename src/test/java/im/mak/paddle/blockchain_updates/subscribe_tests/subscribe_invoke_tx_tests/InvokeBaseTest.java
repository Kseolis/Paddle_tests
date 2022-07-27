package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests;

import com.wavesplatform.crypto.base.Base58;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseTest;
import im.mak.paddle.helpers.PrepareInvokeTestsData;
import org.junit.jupiter.api.BeforeAll;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.helpers.PrepareInvokeTestsData.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.getTransactionId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.BaseInvokeMetadata.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invoke_transaction_metadata.InvokeMetadataResultIssue.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.InvokeTransactionHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.getTransactionVersion;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender.getInvokeScriptId;
import static im.mak.paddle.util.Constants.DEVNET_CHAIN_ID;
import static org.assertj.core.api.Assertions.assertThat;

public class InvokeBaseTest extends BaseTest {
    static PrepareInvokeTestsData testsData;
    static String dAppAccountPublicKey;
    static String dAppAccountPublicKeyHash;
    static String dAppAccountAddress;
    static String dAppFunctionName;

    @BeforeAll
    static void before() {
        testsData = new PrepareInvokeTestsData();
    }

    void prepareInvoke(Account dAppAccount) {
        dAppAccountPublicKey = dAppAccount.publicKey().toString();
        dAppAccountPublicKeyHash = Base58.encode(dAppAccount.address().publicKeyHash());
        dAppAccountAddress = dAppAccount.address().toString();
        dAppFunctionName = getDAppCall().getFunction().name();
    }

    void checkInvokeSubscribe(long amount, long fee) {
        assertThat(getChainId(0)).isEqualTo(DEVNET_CHAIN_ID);
        assertThat(getSenderPublicKeyFromTransaction(0)).isEqualTo(getCallerAccount().publicKey().toString());
        assertThat(getTransactionFeeAmount(0)).isEqualTo(fee);
        assertThat(getTransactionVersion(0)).isEqualTo(LATEST_VERSION);
        assertThat(getInvokeTransactionPublicKeyHash(0)).isEqualTo(dAppAccountPublicKeyHash);
      //  assertThat(getInvokeTransactionFunctionCall(0)).isEqualTo(getDAppCall().getFunction().toString());
        assertThat(getTransactionId()).isEqualTo(getInvokeScriptId());
    }

    void checkPaymentsSubscribe(long amount) {
        assertThat(getInvokeTransactionPaymentAmount(0, 0)).isEqualTo(getAssetAmount().value());
        assertThat(getInvokeTransactionPaymentAmount(0, 1)).isEqualTo(amount);
    }

    void checkMainMetadata(int index) {
        assertThat(getInvokeMetadataDAppAddress(index)).isEqualTo(dAppAccountAddress);
        assertThat(getInvokeMetadataFunctionName(index)).isEqualTo(dAppFunctionName);
    }

    void checkIssueAssetMetadata(int metadataIndex, int dataIndex, String name, String description,
                                long amount, int decimals, boolean reissue, long nonce) {
        assertThat(getInvokeMetadataResultIssueAssetId(metadataIndex, dataIndex)).isEqualTo(getAssetId().toString());
        assertThat(getInvokeMetadataResultIssueName(metadataIndex, dataIndex)).isEqualTo(name);
        assertThat(getInvokeMetadataResultIssueDescription(metadataIndex, dataIndex)).isEqualTo(description);
        assertThat(getInvokeMetadataResultIssueAmount(metadataIndex, dataIndex)).isEqualTo(amount);
        assertThat(getInvokeMetadataResultIssueDecimals(metadataIndex, dataIndex)).isEqualTo(decimals);
        assertThat(getInvokeMetadataResultIssueReissuable(metadataIndex, dataIndex)).isEqualTo(reissue);
        assertThat(getInvokeMetadataResultIssueNonce(metadataIndex, dataIndex)).isEqualTo(nonce);
    }
}

/* assertAll(
                () -> assertThat(getChainId(0)).isEqualTo(DEVNET_CHAIN_ID),
                () -> assertThat(getSenderPublicKeyFromTransaction(0)).isEqualTo(publicKey),
                () -> assertThat(getTransactionFeeAmount(0)).isEqualTo(SUM_FEE),
                () -> assertThat(getTransactionVersion(0)).isEqualTo(LATEST_VERSION),
                () -> assertThat(getTransactionId()).isEqualTo(getInvokeScriptId()),
                () -> assertThat(getInvokeTransactionAmount(0, 0)).isEqualTo(amount),
                () -> assertThat(getInvokeTransactionPublicKeyHash(0)).isEqualTo(accWithDAppPublicKeyHash),


                () -> assertThat(getInvokeMetadataArgStringValue(0, 0)).isEqualTo(dAppValue),
                // check waves account balance
                () -> assertThat(getAddress(0, 0)).isEqualTo(address),
                () -> assertThat(getAmountBefore(0, 0)).isEqualTo(getAccountWavesBalance()),
                () -> assertThat(getAmountAfter(0, 0)).isEqualTo(getBalanceAfterTransaction()),
                // check waves dAppAccount balance
                () -> assertThat(getAddress(0, 1)).isEqualTo(accWithDAppAddress),
                () -> assertThat(getAmountBefore(0, 1)).isEqualTo(getDAppAccountBalance()),
                () -> assertThat(getAmountAfter(0, 1)).isEqualTo(getDAppAccountBalanceAfterTransaction()),
                // data entries
                () -> assertThat(getSenderAddress(0, 0)).isEqualTo(accWithDAppAddress),
                () -> assertThat(getTxKeyForStateUpdates(0, 0)).isEqualTo(dAppKey),
                () -> assertThat(getTxStringValueForStateUpdates(0, 0)).isEqualTo(dAppValue),
                () -> assertThat(getBeforeDataEntriesKey(0, 0)).isEqualTo(dAppKey),
                () -> assertThat(getBeforeDataEntriesStringValue(0, 0)).isEqualTo(dAppBeforeValue)
        );*/