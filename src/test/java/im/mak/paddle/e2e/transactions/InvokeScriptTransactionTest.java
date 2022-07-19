package im.mak.paddle.e2e.transactions;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.dapp.DAppCall;
import im.mak.paddle.dapps.AssetDAppAccount;
import im.mak.paddle.dapps.DataDApp;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static com.wavesplatform.transactions.InvokeScriptTransaction.LATEST_VERSION;
import static com.wavesplatform.wavesj.ApplicationStatus.SUCCEEDED;
import static im.mak.paddle.helpers.ConstructorRideFunctions.assetsFunctionBuilder;
import static im.mak.paddle.helpers.ConstructorRideFunctions.defaultFunctionBuilder;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.helpers.Randomizer.randomNumAndLetterString;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.getTxInfo;
import static im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTransaction.*;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;

public class InvokeScriptTransactionTest {
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
    @DisplayName("invoke transaction with DataDApp and issue asset payment")
    void invokeScriptWithDataDAppTest() {
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functionArgs = "intVal:Int, binVal:ByteVector, boolVal:Boolean, strVal:String";
        final String functions = "[\nIntegerEntry(\"int\", intVal),\nBinaryEntry(\"byte\", binVal),\n" +
                "BooleanEntry(\"bool\", boolVal),\nStringEntry(\"str\", strVal)\n]\n";
        final String script = defaultFunctionBuilder(functionArgs, functions, libVersion);
        dAppAccount.setScript(script);

        final DAppCall dAppCall = dAppAccount.setData(intArg, binArg, boolArg, stringArg);

        amounts.clear();
        amounts.add(wavesAmount);

        setFee(SUM_FEE);
        setExtraFee(0);

        for (int v = 1; v <= LATEST_VERSION; v++) {
            setVersion(v);
            balancesAfterPaymentInvoke(callerAccount, dAppAccount, amounts, assetId);
            invokeSenderWithPayment(callerAccount, dAppAccount, dAppCall, amounts);
            checkInvokeTransaction(callerAccount, SUM_FEE);
            checkBalancesAfterInvoke(callerAccount, dAppAccount);
        }
    }

    @Test
    @DisplayName("invoke transaction with DeleteEntry")
    void invokeScriptDeleteEntryTest() {
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functionArgs = "intVal:Int";
        final String functions = "[\nIntegerEntry(\"int\", intVal),\nDeleteEntry(\"int\")\n]\n";
        final String script = defaultFunctionBuilder(functionArgs, functions, libVersion);
        dAppAccount.setScript(script);

        DAppCall dAppCall = dAppAccount.setData(intArg);

        amounts.clear();
        amounts.add(wavesAmount);

        setFee(SUM_FEE);
        setExtraFee(0);

        for (int v = 1; v <= LATEST_VERSION; v++) {
            setVersion(v);
            balancesAfterPaymentInvoke(callerAccount, dAppAccount, amounts, assetId);
            invokeSenderWithPayment(callerAccount, dAppAccount, dAppCall, amounts);
            checkInvokeTransaction(callerAccount, SUM_FEE);
            checkBalancesAfterInvoke(callerAccount, dAppAccount);
        }
    }

    @Test
    @DisplayName("invoke transaction with Burn transaction")
    void invokeScriptWithBurn() {
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

        for (int v = 1; v <= LATEST_VERSION; v++) {
            setVersion(v);
            balancesAfterBurnAssetInvoke(callerAccount, assetDAppAccount, amounts, assetId);
            invokeSender(callerAccount, assetDAppAccount, dAppCall);
            checkInvokeTransaction(callerAccount, fee);
            checkBalancesAfterInvoke(callerAccount, assetDAppAccount);
        }
    }

    @Test
    @DisplayName("invoke transaction with Reissue Transaction")
    void invokeScriptWithReissue() {
        long fee = ONE_WAVES + SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functions = "Reissue(assetId," + assetAmount.value() + ",true),\nReissue(issueAssetId,1,true)";
        final String script = assetsFunctionBuilder(libVersion, "unit", functions, args);
        assetDAppAccount.setScript(script);

        final DAppCall dAppCall = assetDAppAccount.setDataAssetId(Base58.decode(assetId.toString()));

        amounts.clear();
        amounts.add(assetAmount);

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);

        for (int v = 1; v <= LATEST_VERSION; v++) {
            setVersion(v);
            balancesAfterReissueAssetInvoke(callerAccount, assetDAppAccount, amounts, assetId);
            invokeSender(callerAccount, assetDAppAccount, dAppCall);
            checkInvokeTransaction(callerAccount, fee);
            checkBalancesAfterInvoke(callerAccount, assetDAppAccount);
        }
    }

    @Test
    @DisplayName("invoke with Lease Transaction and WAVES payment")
    void invokeScriptWithLease() {
        final int libVersion = getRandomInt(5, MAX_LIB_VERSION);

        final String functionArgs = "address:ByteVector";
        final String functions = "[\nLease(Address(address), " + wavesAmount.value() + ")\n]\n";
        final String script = defaultFunctionBuilder(functionArgs, functions, libVersion);

        dAppAccount.setScript(script);

        final DAppCall dAppCall = dAppAccount.setData(callerAddress);

        amounts.clear();
        amounts.add(wavesAmount);

        setFee(SUM_FEE);
        setExtraFee(0);

        for (int v = 1; v <= LATEST_VERSION; v++) {
            setVersion(v);
            balancesAfterPaymentInvoke(callerAccount, dAppAccount, amounts, assetId);
            invokeSenderWithPayment(callerAccount, dAppAccount, dAppCall, amounts);
            checkInvokeTransaction(callerAccount, SUM_FEE);
            checkBalancesAfterInvoke(callerAccount, dAppAccount);
        }
    }

    @Test
    @DisplayName("invoke with LeaseCancel Transaction and WAVES payment")
    void invokeScriptWithLeaseCancel() {
        final int libVersion = getRandomInt(5, MAX_LIB_VERSION);

        final String functionArgs = "leaseId:ByteVector";
        final String functions = "[\nLeaseCancel(leaseId)\n]\n";
        final String script = defaultFunctionBuilder(functionArgs, functions, libVersion);

        dAppAccount.setScript(script);

        amounts.clear();
        amounts.add(wavesAmount);

        setFee(SUM_FEE);
        setExtraFee(0);

        for (int v = 1; v <= LATEST_VERSION; v++) {
            byte[] leaseId = Base58.decode(dAppAccount.lease(callerAccount, wavesAmount.value()).tx().id().toString());
            final DAppCall dAppCall = dAppAccount.setData(leaseId);
            setVersion(v);
            balancesAfterPaymentInvoke(callerAccount, dAppAccount, amounts, assetId);
            invokeSenderWithPayment(callerAccount, dAppAccount, dAppCall, amounts);
            checkInvokeTransaction(callerAccount, SUM_FEE);
            checkBalancesAfterInvoke(callerAccount, dAppAccount);
        }
    }

    @Test
    @DisplayName("invoke transaction with SponsorFee Transaction")
    void invokeScriptWithSponsorFee() {
        long fee = ONE_WAVES + SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functions = "SponsorFee(assetId, " + assetAmount.value() + "),\nSponsorFee(issueAssetId, 500)";
        final String script = assetsFunctionBuilder(libVersion, "unit", functions, args);
        assetDAppAccount.setScript(script);

        final DAppCall dAppCall = assetDAppAccount.setDataAssetId(Base58.decode(assetId.toString()));

        amounts.clear();

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);

        for (int v = 1; v <= LATEST_VERSION; v++) {
            setVersion(v);
            balancesAfterPaymentInvoke(callerAccount, assetDAppAccount, amounts, assetId);
            invokeSender(callerAccount, assetDAppAccount, dAppCall);
            checkInvokeTransaction(callerAccount, fee);
            checkBalancesAfterInvoke(callerAccount, assetDAppAccount);
        }
    }

    @Test
    @DisplayName("invoke transaction with ScriptTransfer")
    void invokeScriptWithScriptTransfer() {
        final long fee = ONE_WAVES + SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String currentArgs = args + ", " + "address:ByteVector";
        final String functions = "ScriptTransfer(Address(address), " + assetAmount.value() + ", assetId),\n" +
                "ScriptTransfer(Address(address), 500, issueAssetId),\n" +
                "ScriptTransfer(Address(address), " + wavesAmount.value() + ", unit)";
        final String script = assetsFunctionBuilder(libVersion, "unit", functions, currentArgs);

        assetDAppAccount.setScript(script);

        final DAppCall dAppCall = assetDAppAccount.setDataAssetAndAddress(Base58.decode(assetId.toString()), dAppAddress);

        amounts.clear();
        amounts.add(wavesAmount);
        amounts.add(assetAmount);

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);

        for (int v = 1; v <= LATEST_VERSION; v++) {
            setVersion(v);
            balancesAfterCallerInvokeAsset(assetDAppAccount, dAppAccount, amounts, assetId);
            invokeSender(assetDAppAccount, assetDAppAccount, dAppCall);
            checkInvokeTransaction(assetDAppAccount, fee);
            checkBalancesAfterInvoke(assetDAppAccount, dAppAccount);
        }
    }

    @Test
    @DisplayName("invoke with payments")
    void invokeScriptPayments() {
        final long fee = ONE_WAVES + SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String arg = "intVal:Int";
        final String functions = "IntegerEntry(\"int\", intVal)";
        final String script = assetsFunctionBuilder(libVersion, "unit", functions, arg);
        dAppAccount.setScript(script);

        final DAppCall dAppCall = dAppAccount.setData(getRandomInt(1, 1000));

        amounts.clear();
        amounts.add(wavesAmount);
        amounts.add(assetAmount);

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);

        for (int v = 1; v <= LATEST_VERSION; v++) {
            setVersion(v);
            balancesAfterCallerInvokeAsset(callerAccount, dAppAccount, amounts, assetId);
            invokeSenderWithPayment(callerAccount, dAppAccount, dAppCall, amounts);
            checkInvokeTransaction(callerAccount, fee);
            checkBalancesAfterInvoke(callerAccount, dAppAccount);
        }
    }

    @Test
    @DisplayName("invoke dApp to dApp")
    void invokeDAppToDApp() {
        final int libVersion = getRandomInt(5, MAX_LIB_VERSION);
        final Amount attachAmount = Amount.of(100000000, assetId);

        final String functionArgsDApp1 = "dapp2:ByteVector, a:Int, key1:String, key2:String, assetId:ByteVector";
        final String dApp1Body =
                "strict res = invoke(Address(dapp2),\"bar\",[a, assetId],[AttachedPayment(assetId," + attachAmount.value() + ")])\n" +
                        "match res {\ncase r : Int => \n(\n[\n" +
                        "IntegerEntry(key1, r),\n" +
                        "IntegerEntry(key2, wavesBalance(Address(dapp2)).regular)\n" +
                        "],\nunit\n)\ncase _ => throw(\"Incorrect invoke result\") }\n";
        final String dApp1 = defaultFunctionBuilder(functionArgsDApp1, dApp1Body, libVersion);

        final String dApp2 =
                "{-# STDLIB_VERSION 5 #-}\n{-# CONTENT_TYPE DAPP #-}\n{-# SCRIPT_TYPE ACCOUNT #-}\n" +
                        "\n@Callable(i)\n" +
                        "func bar(a: Int, assetId: ByteVector) = {\n" +
                        "   (\n" +
                        "      [\n" +
                        "         ScriptTransfer(i.caller, " + wavesAmount.value() + ", unit)\n" +
                        "      ],\n" +
                        "      a*2\n" +
                        "   )\n" +
                        "}";

        dAppAccount.setScript(dApp1);
        assetDAppAccount.setScript(dApp2);

        final DAppCall dAppCall = dAppAccount
                .setData(assetDAppAddress, 121, "bar", "balance", assetId.bytes());
        amounts.clear();
        amounts.add(wavesAmount);
        amounts.add(attachAmount);

        setFee(SUM_FEE);

        for (int v = 1; v <= LATEST_VERSION; v++) {
            setVersion(v);
            balancesAfterDAppToDApp(callerAccount, dAppAccount, assetDAppAccount, amounts, assetId);
            invokeSender(callerAccount, dAppAccount, dAppCall);
            checkInvokeTransaction(callerAccount, SUM_FEE);
            checkBalancesAfterDAppToDAppInvoke(callerAccount, dAppAccount, assetDAppAccount);
        }
    }

    private void checkInvokeTransaction(Account caller, long fee) {
        assertThat(getTxInfo().applicationStatus()).isEqualTo(SUCCEEDED);
        assertThat(getInvokeScriptTx().dApp()).isEqualTo(getDAppCall().getDApp());
        assertThat(getInvokeScriptTx().function()).isEqualTo(getDAppCall().getFunction());
        assertThat(getInvokeScriptTx().sender()).isEqualTo(caller.publicKey());
        assertThat(getInvokeScriptTx().fee().assetId()).isEqualTo(AssetId.WAVES);
        assertThat(getInvokeScriptTx().fee().value()).isEqualTo(fee);
        assertThat(getInvokeScriptTx().type()).isEqualTo(16);
    }

    private void checkBalancesAfterInvoke(Account caller, Account dApp) {
        assertThat(caller.getWavesBalance()).isEqualTo(getCallerBalanceWavesAfterTransaction());
        assertThat(dApp.getWavesBalance()).isEqualTo(getDAppBalanceWavesAfterTransaction());
        if (assetId != null) {
            assertThat(caller.getBalance(assetId)).isEqualTo(getCallerBalanceIssuedAssetsAfterTransaction());
            assertThat(dApp.getBalance(assetId)).isEqualTo(getDAppBalanceIssuedAssetsAfterTransaction());
        }
    }

    private void checkBalancesAfterDAppToDAppInvoke(Account caller, Account dApp1, Account dApp2) {
        assertThat(caller.getWavesBalance()).isEqualTo(getCallerBalanceWavesAfterTransaction());
        assertThat(dApp1.getWavesBalance()).isEqualTo(getDAppBalanceWavesAfterTransaction());
        assertThat(dApp2.getWavesBalance()).isEqualTo(getDApp2BalanceWavesAfterTransaction());
        if (assetId != null) {
            assertThat(caller.getBalance(assetId)).isEqualTo(getCallerBalanceIssuedAssetsAfterTransaction());
            assertThat(dApp1.getBalance(assetId)).isEqualTo(getDAppBalanceIssuedAssetsAfterTransaction());
            assertThat(dApp2.getBalance(assetId)).isEqualTo(getDApp2BalanceIssuedAssetsAfterTransaction());
        }
    }
}