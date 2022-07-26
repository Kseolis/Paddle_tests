package im.mak.paddle.helpers;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import im.mak.paddle.Account;
import im.mak.paddle.dapp.DAppCall;
import im.mak.paddle.dapps.AssetDAppAccount;
import im.mak.paddle.dapps.DataDApp;

import java.util.ArrayList;
import java.util.List;

import static im.mak.paddle.helpers.ConstructorRideFunctions.assetsFunctionBuilder;
import static im.mak.paddle.helpers.ConstructorRideFunctions.defaultFunctionBuilder;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.helpers.Randomizer.randomNumAndLetterString;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setExtraFee;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setFee;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.Constants.ONE_WAVES;

public class PrepareInvokeTestsData {
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
    private static DAppCall dAppCall;
    private static long fee;

    public PrepareInvokeTestsData() {
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

    public void prepareDataForDataDAppTests() {
        binArg = randomNumAndLetterString(10);
        stringArg = randomNumAndLetterString(10);
        intArg = getRandomInt(1, 999999);
        boolArg = intArg % 2 == 0;


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
    }

    public void prepareDataForDeleteEntryTests() {
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
    }

    public void prepareDataForBurnTests() {
        fee = ONE_WAVES + SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functions = "Burn(assetId, " + assetAmount.value() + "),\nBurn(issueAssetId, 1)";
        final String script = assetsFunctionBuilder(libVersion, "unit", functions, args);
        assetDAppAccount.setScript(script);

        dAppCall = assetDAppAccount.setDataAssetId(Base58.decode(assetId.toString()));

        amounts.clear();
        amounts.add(assetAmount);

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);
    }

    public void prepareDataForReissueTests() {
        fee = ONE_WAVES + SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functions = "Reissue(assetId," + assetAmount.value() + ",true),\nReissue(issueAssetId,1,true)";
        final String script = assetsFunctionBuilder(libVersion, "unit", functions, args);
        assetDAppAccount.setScript(script);

        dAppCall = assetDAppAccount.setDataAssetId(Base58.decode(assetId.toString()));

        amounts.clear();
        amounts.add(assetAmount);

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);
    }

    public void prepareDataForLeaseTests() {

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
    }

    public void prepareDataForLeaseCancelTests() {

        final int libVersion = getRandomInt(5, MAX_LIB_VERSION);

        final String functionArgs = "leaseId:ByteVector";
        final String functions = "[\nLeaseCancel(leaseId)\n]\n";
        final String script = defaultFunctionBuilder(functionArgs, functions, libVersion);

        dAppAccount.setScript(script);

        amounts.clear();
        amounts.add(wavesAmount);

        setFee(SUM_FEE);
        setExtraFee(0);

        byte[] leaseId = Base58.decode(dAppAccount.lease(callerAccount, wavesAmount.value()).tx().id().toString());
        dAppCall = dAppAccount.setData(leaseId);
    }

    public void prepareDataForSponsorFeeTests() {
        fee = ONE_WAVES + SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functions = "SponsorFee(assetId, " + assetAmount.value() + "),\nSponsorFee(issueAssetId, 500)";
        final String script = assetsFunctionBuilder(libVersion, "unit", functions, args);
        assetDAppAccount.setScript(script);

        dAppCall = assetDAppAccount.setDataAssetId(Base58.decode(assetId.toString()));

        amounts.clear();

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);
    }

    public void prepareDataForScriptTransferTests() {
        fee = ONE_WAVES + SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String currentArgs = args + ", " + "address:ByteVector";
        final String functions = "ScriptTransfer(Address(address), " + assetAmount.value() + ", assetId),\n" +
                "ScriptTransfer(Address(address), 500, issueAssetId),\n" +
                "ScriptTransfer(Address(address), " + wavesAmount.value() + ", unit)";
        final String script = assetsFunctionBuilder(libVersion, "unit", functions, currentArgs);

        assetDAppAccount.setScript(script);

        dAppCall = assetDAppAccount.setDataAssetAndAddress(Base58.decode(assetId.toString()), dAppAddress);

        amounts.clear();
        amounts.add(wavesAmount);
        amounts.add(assetAmount);

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);
    }

    public void prepareDataForPaymentsTests() {
        fee = ONE_WAVES + SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String arg = "intVal:Int";
        final String functions = "IntegerEntry(\"int\", intVal)";
        final String script = assetsFunctionBuilder(libVersion, "unit", functions, arg);
        dAppAccount.setScript(script);

        dAppCall = dAppAccount.setData(getRandomInt(1, 1000));

        amounts.clear();
        amounts.add(wavesAmount);
        amounts.add(assetAmount);

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);
    }

    public void prepareDataForDAppToDAppTests() {
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

        dAppCall = dAppAccount.setData(assetDAppAddress, 121, "bar", "balance", assetId.bytes());

        amounts.clear();
        amounts.add(wavesAmount);
        amounts.add(attachAmount);

        setFee(SUM_FEE);
    }

    public static DAppCall getDAppCall() {
        return dAppCall;
    }

    public static List<Amount> getAmounts() {
        return amounts;
    }

    public static AssetId getAssetId() {
        return assetId;
    }

    public static long getFee() {
        return fee;
    }

    public static int getIntArg() {
        return intArg;
    }

    public static String getBinArg() {
        return binArg;
    }

    public static boolean isBoolArg() {
        return boolArg;
    }

    public static String getStringArg() {
        return stringArg;
    }

    public static Account getCallerAccount() {
        return callerAccount;
    }

    public static DataDApp getDAppAccount() {
        return dAppAccount;
    }

    public static AssetDAppAccount getAssetDAppAccount() {
        return assetDAppAccount;
    }

    public static Amount getWavesAmount() {
        return wavesAmount;
    }

    public static Amount getAssetAmount() {
        return assetAmount;
    }

}
