package im.mak.paddle.blockchain_updates.subscribe_tests;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseTest;
import im.mak.paddle.dapp.DAppCall;
import im.mak.paddle.dapps.AssetDAppAccount;
import im.mak.paddle.dapps.DataDApp;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.ConstructorRideFunctions.assetsFunctionBuilder;
import static im.mak.paddle.helpers.ConstructorRideFunctions.defaultFunctionBuilder;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.helpers.Randomizer.randomNumAndLetterString;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.invokeTransactionMetadata.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.DataEntries.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.InvokeScriptTransactionHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.*;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.*;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTransaction.*;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

public class InvokeScriptTransactionSubscriptionTest extends BaseTest {
    private static Account callerAccount;
    private static byte[] callerAddress;

    private static DataDApp dAppAccount;
    private static byte[] dAppAddress;

    private static AssetDAppAccount assetDAppAccount;
    private static byte[] assetDAppAddress;

    private static AssetId assetId;

    private static int intArg;
    private static String binArg;
    private static boolean boolArg;
    private static String stringArg;

    private static Amount wavesAmount;
    private static Amount assetAmount;

    private DAppCall dAppCall;
    private String dAppAccountAddress;
    private String dAppAccountPublicKey;
    private String dAppAccountPublicKeyHash;
    private String dAppFunctionName;
    private String dAppScript;
    private String dAppAssetId;

    private static final String args = "assetId:ByteVector";
    private static final List<Amount> amounts = new ArrayList<>();

    @BeforeAll
    static void before() {
        async(
                () -> {
                    callerAccount = new Account(DEFAULT_FAUCET);
                    callerAddress = Base58.decode(callerAccount.address().toString());
                },
                () -> binArg = randomNumAndLetterString(10),
                () -> stringArg = randomNumAndLetterString(10),
                () -> {
                    intArg = getRandomInt(1, 999999);
                    boolArg = intArg % 2 == 0;
                },
                () -> {
                    assetDAppAccount = new AssetDAppAccount(DEFAULT_FAUCET, "true");
                    assetDAppAddress = Base58.decode(assetDAppAccount.address().toString());
                    assetId = assetDAppAccount.issue(i -> i.name("outside Asset").quantity(900_000_000L))
                            .tx().assetId();
                },
                () -> {
                    dAppAccount = new DataDApp(DEFAULT_FAUCET, "true");
                    dAppAddress = Base58.decode(dAppAccount.address().toString());
                }
        );
        assetDAppAccount.transfer(callerAccount, Amount.of(300_000_000L, assetId));
        assetDAppAccount.transfer(dAppAccount, Amount.of(300_000_000L, assetId));
        wavesAmount = Amount.of(getRandomInt(100, 100000));
        assetAmount = Amount.of(getRandomInt(100, 100000), assetId);
    }

    @Test
    @DisplayName("subscribe invoke with DataDApp")
    void subscribeInvokeWithDataDApp() {
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functionArgs = "intVal:Int, binVal:ByteVector, boolVal:Boolean, strVal:String";
        final String functions = "[\nIntegerEntry(\"int\", intVal),\nBinaryEntry(\"byte\", binVal),\n" +
                "BooleanEntry(\"bool\", boolVal),\nStringEntry(\"str\", strVal)\n]\n";
        final String script = defaultFunctionBuilder(functionArgs, functions, libVersion);
        dAppAccount.setScript(script);

        dAppCall = dAppAccount.setData(intArg, binArg, boolArg, stringArg);

        amounts.clear();
        amounts.add(wavesAmount);

        setFee(SUM_FEE);
        setExtraFee(0);

        setVersion(LATEST_VERSION);
        balancesAfterPaymentInvoke(callerAccount, dAppAccount, amounts, assetId);
        invokeSenderWithPayment(callerAccount, dAppAccount, dAppCall, amounts);

        height = node().getHeight();
        subscribeResponseHandler(channel, dAppAccount, height, height);
        prepareInvoke(dAppAccount);
        System.out.println(getAppend());
        checkInvokeSubscribe(wavesAmount.value(), "ByteVector", dAppAssetId, SUM_FEE);
    }

    @Test
    @DisplayName("subscribe invoke with DeleteEntry")
    void subscribeInvokeWithDeleteEntry() {
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functionArgs = "intVal:Int";
        final String functions = "[\nIntegerEntry(\"int\", intVal),\nDeleteEntry(\"int\")\n]\n";
        final String script = defaultFunctionBuilder(functionArgs, functions, libVersion);
        dAppAccount.setScript(script);

        dAppCall = dAppAccount.setData(intArg);

        amounts.clear();
        amounts.add(wavesAmount);

        setFee(SUM_FEE);
        setExtraFee(0);
        setVersion(LATEST_VERSION);
        balancesAfterPaymentInvoke(callerAccount, dAppAccount, amounts, assetId);
        invokeSenderWithPayment(callerAccount, dAppAccount, dAppCall, amounts);

        height = node().getHeight();
        subscribeResponseHandler(channel, dAppAccount, height, height);
        prepareInvoke(dAppAccount);
        System.out.println(getAppend());
        checkInvokeSubscribe(wavesAmount.value(), "ByteVector", dAppAssetId, SUM_FEE);
    }

    @Test
    @DisplayName("subscribe invoke with Burn")
    void subscribeInvokeWithBurn() {
        long fee = ONE_WAVES + SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functions = "Burn(assetId, " + assetAmount.value() + "),\nBurn(issueAssetId, 1)";
        final String script = assetsFunctionBuilder(libVersion, "unit", functions, args);
        assetDAppAccount.setScript(script);

        final DAppCall dAppCall = assetDAppAccount.setDataAssetId(Base58.decode(assetId.toString()));

        amounts.clear();
        amounts.add(assetAmount);

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);
        setVersion(LATEST_VERSION);
        balancesAfterBurnAssetInvoke(callerAccount, assetDAppAccount, amounts, assetId);
        invokeSender(callerAccount, assetDAppAccount, dAppCall);

        height = node().getHeight();
        subscribeResponseHandler(channel, dAppAccount, height, height);
        prepareInvoke(dAppAccount);
        System.out.println(getAppend());
        checkInvokeSubscribe(wavesAmount.value(), "ByteVector", dAppAssetId, fee);
    }

    @Test
    @DisplayName("subscribe invoke with Reissue")
    void subscribeInvokeWithReissue() {
        long fee = ONE_WAVES + SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functions = "Reissue(assetId," + assetAmount.value() + ",true),\nReissue(issueAssetId,1,true)";
        final String script = assetsFunctionBuilder(libVersion, "unit", functions, args);
        assetDAppAccount.setScript(script);

        dAppCall = assetDAppAccount.setDataAssetId(Base58.decode(assetId.toString()));

        amounts.clear();
        amounts.add(assetAmount);

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);
        setVersion(LATEST_VERSION);
        balancesAfterPaymentInvoke(callerAccount, dAppAccount, amounts, assetId);
        invokeSenderWithPayment(callerAccount, dAppAccount, dAppCall, amounts);

        height = node().getHeight();
        subscribeResponseHandler(channel, dAppAccount, height, height);
        prepareInvoke(dAppAccount);
        System.out.println(getAppend());
        checkInvokeSubscribe(wavesAmount.value(), "ByteVector", dAppAssetId, fee);
    }

    @Test
    @DisplayName("subscribe invoke with Lease Transaction and WAVES payment")
    void subscribeInvokeWithLease() {
        final int libVersion = getRandomInt(5, MAX_LIB_VERSION);

        final String functionArgs = "address:ByteVector";
        final String functions = "[\nLease(Address(address), " + wavesAmount.value() + ")\n]\n";
        final String script = defaultFunctionBuilder(functionArgs, functions, libVersion);

        dAppAccount.setScript(script);

        dAppCall = dAppAccount.setData(callerAddress);

        amounts.clear();
        amounts.add(wavesAmount);

        setFee(SUM_FEE);
        setExtraFee(0);

        height = node().getHeight();
        subscribeResponseHandler(channel, dAppAccount, height, height);
        prepareInvoke(dAppAccount);
        System.out.println(getAppend());
        checkInvokeSubscribe(wavesAmount.value(), "ByteVector", dAppAssetId, SUM_FEE);
    }

    private void checkInvokeSubscribe(long amount, String dAppKey, String dAppValue, long fee) {
        assertThat(getChainId(0)).isEqualTo(DEVNET_CHAIN_ID);
        assertThat(getSenderPublicKeyFromTransaction(0)).isEqualTo(dAppAccountPublicKey);
        assertThat(getTransactionFeeAmount(0)).isEqualTo(fee);
        assertThat(getTransactionVersion(0)).isEqualTo(LATEST_VERSION);
        assertThat(getTransactionId()).isEqualTo(getInvokeScriptId());

        assertThat(getInvokeTransactionPaymentAmount(0, 0)).isEqualTo(amount);
        assertThat(getInvokeTransactionPublicKeyHash(0)).isEqualTo(dAppAccountPublicKeyHash);
        assertThat(getInvokeMetadataDAppAddress(0)).isEqualTo(dAppAccountAddress);
        assertThat(getInvokeMetadataFunctionName(0)).isEqualTo(dAppFunctionName);
        assertThat(getInvokeMetadataResultDataKey(0, 0)).isEqualTo(dAppKey);
        assertThat(getInvokeMetadataArgStringValue(0, 0)).isEqualTo(dAppValue);
        // check waves account balance
        assertThat(getAddress(0, 0)).isEqualTo(dAppAccountAddress);
        assertThat(getAmountBefore(0, 0)).isEqualTo(getAccountWavesBalance());
        assertThat(getAmountAfter(0, 0)).isEqualTo(getBalanceAfterTransaction());
        // check waves dAppAccount balance
        assertThat(getAddress(0, 1)).isEqualTo(dAppAccountAddress);
//        assertThat(getAmountBefore(0, 1)).isEqualTo(getDAppAccountBalance());
        assertThat(getAmountAfter(0, 1)).isEqualTo(getDAppBalanceWavesAfterTransaction());
        // data entries
        assertThat(getSenderAddress(0, 0)).isEqualTo(dAppAccountAddress);
        assertThat(getTxKeyForStateUpdates(0, 0)).isEqualTo(dAppKey);
        assertThat(getTxStringValueForStateUpdates(0, 0)).isEqualTo(dAppValue);

        assertThat(getBeforeDataEntriesKey(0, 0)).isEqualTo(dAppKey);
        assertThat(getBeforeDataEntriesStringValue(0, 0)).isEqualTo(dAppValue);
    }

    private void prepareInvoke(Account dAppAccount) {
        dAppAccountPublicKey = dAppAccount.publicKey().toString();
        dAppAccountPublicKeyHash = Base58.encode(dAppAccount.address().publicKeyHash());
        dAppAccountAddress = dAppAccount.address().toString();
        dAppFunctionName = dAppCall.getFunction().name();
    }
}
/*
*
        assertAll(
                () -> assertThat(getChainId(0)).isEqualTo(DEVNET_CHAIN_ID),
                () -> assertThat(getSenderPublicKeyFromTransaction(0)).isEqualTo(publicKey),
                () -> assertThat(getTransactionFeeAmount(0)).isEqualTo(SUM_FEE),
                () -> assertThat(getTransactionVersion(0)).isEqualTo(LATEST_VERSION),
                () -> assertThat(getTransactionId()).isEqualTo(getInvokeScriptId()),
                () -> assertThat(getInvokeTransactionAmount(0, 0)).isEqualTo(amount),
                () -> assertThat(getInvokeTransactionPublicKeyHash(0)).isEqualTo(accWithDAppPublicKeyHash),
                () -> assertThat(getInvokeMetadataDAppAddress(0)).isEqualTo(accWithDAppAddress),
                () -> assertThat(getInvokeMetadataFunctionName(0)).isEqualTo(accWithDAppFunctionName),
                () -> assertThat(getInvokeMetadataResultDataKey(0, 0)).isEqualTo(dAppKey),
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