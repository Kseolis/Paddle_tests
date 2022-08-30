package im.mak.paddle.blockchain_updates.invoke_subscribe_helpers;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.transactions.common.Amount;
import im.mak.paddle.Account;
import im.mak.paddle.dapp.DAppCall;

import java.util.*;

import static im.mak.paddle.blockchain_updates.subscribe_invoke_tx_tests.SubscribeInvokeBaseTest.*;
import static im.mak.paddle.blockchain_updates.subscribe_invoke_tx_tests.SubscribeInvokeBaseTest.getIntArg;
import static im.mak.paddle.helpers.ConstructorRideFunctions.*;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.helpers.Randomizer.randomNumAndLetterString;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setExtraFee;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setFee;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.Constants.ONE_WAVES;

public class PrepareInvokeTestsData {
    private static String dAppAccountPublicKeyHash;
    private static String dAppAccountAddress;
    private static String dAppFunctionName;
    private static byte[] leaseId;

    private static long amountAfterInvokeIssuedAsset;
    private static long amountAfterInvokeDAppIssuedAsset;

    private static final String args = "assetId:ByteVector";
    private static final String key1ForDAppEqualBar = "bar";
    private static final String key2ForDAppEqualBalance = "balance";
    private static DAppCall dAppCall;
    private static long invokeFee;

    private static final Map<String, String> assetDataForIssue = new HashMap<>();
    private static final List<Amount> amounts = new ArrayList<>();

    public static  void prepareInvoke(Account dAppAccount) {
        dAppAccountPublicKeyHash = Base58.encode(dAppAccount.address().publicKeyHash());
        dAppAccountAddress = dAppAccount.address().toString();
        dAppFunctionName = getDAppCall().getFunction().name();
    }

    public static  void prepareDataForDataDAppTests() {
        invokeFee = SUM_FEE + ONE_WAVES;

        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functionArgs = "intVal:Int, binVal:ByteVector, boolVal:Boolean, strVal:String";
        final String functions = "[\nIntegerEntry(\"int\", intVal),\nBinaryEntry(\"byte\", binVal),\n" +
                "BooleanEntry(\"bool\", boolVal),\nStringEntry(\"str\", strVal)\n]\n";
        final String script = defaultFunctionBuilder(functionArgs, functions, libVersion);

        getDAppAccount().setScript(script);

        dAppCall = getDAppAccount().setData(getIntArg(), getBase64String(), getBoolArg(), getStringArg());

        amounts.clear();
        amounts.add(getWavesAmount());

        setFee(invokeFee);
        setExtraFee(ONE_WAVES);
    }

    public static  void prepareDataForDeleteEntryTests() {
        invokeFee = SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functionArgs = "intVal:Int";
        final String functions = "[\nIntegerEntry(\"int\", intVal),\nDeleteEntry(\"int\")\n]\n";
        final String script = defaultFunctionBuilder(functionArgs, functions, libVersion);
        getDAppAccount().setScript(script);

        dAppCall = getDAppAccount().setData(getIntArg());

        amounts.clear();
        amounts.add(getWavesAmount());

        setFee(invokeFee);
        setExtraFee(0);
    }

    public static void prepareDataForBurnTests() {
        invokeFee = ONE_WAVES + SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functions = "Burn(assetId, " + getAssetAmount().value() + ")," +
                "\nBurn(issueAssetId, " + getAssetAmount().value() + ")";
        final String script = assetsFunctionBuilder(libVersion, "unit", functions, args, getAssetDAppPublicKey());
        getAssetDAppAccount().setScript(script);

        dAppCall = getAssetDAppAccount().setDataAssetId(Base58.decode(getAssetId().toString()));
        amountAfterInvokeIssuedAsset = getIssueAssetVolume() - getAssetAmount().value();
        amountAfterInvokeDAppIssuedAsset = Integer.parseInt(getAssetData().get(VOLUME)) - getAssetAmount().value();

        amounts.clear();
        amounts.add(getAssetAmount());

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);
    }

    public static  void prepareDataForIssueTests() {
        final long extraFee = ONE_WAVES * 2;
        invokeFee = SUM_FEE + extraFee;
        final String assetName = randomNumAndLetterString(3) + "assetName";
        final String assetDesc = randomNumAndLetterString(3) + "assetDescription";
        final int vol = getRandomInt(700_000_000, 900_000_000);
        final String reissue = String.valueOf(getRandomInt(700_000_000, 900_000_000) % 2 == 0);

        assetDataForIssue.put(ASSET_ID, null);
        assetDataForIssue.put(ISSUER, getAssetDAppPublicKey());
        assetDataForIssue.put(NAME, assetName);
        assetDataForIssue.put(DESCRIPTION, assetDesc);
        assetDataForIssue.put(VOLUME, String.valueOf(vol));
        assetDataForIssue.put(DECIMALS, String.valueOf(getRandomInt(0, 8)));
        assetDataForIssue.put(REISSUE, reissue);

        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);
        final String functions = "Issue(" +
                "\"" + assetName + "\"" + ", " +
                "\"" + assetDesc + "\"" + ", " +
                assetDataForIssue.get(VOLUME) + ", " +
                assetDataForIssue.get(DECIMALS) + ", " +
                reissue + ")";

        final String script = assetsFunctionBuilder(libVersion, "unit", functions, "", getAssetDAppPublicKey());

        getAssetDAppAccount().setScript(script);

        dAppCall = getAssetDAppAccount().setDataAssetId();

        amounts.clear();

        setFee(SUM_FEE);
        setExtraFee(extraFee);
    }

    public static  void prepareDataForReissueTests() {
        invokeFee = ONE_WAVES + SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functions = "Reissue(assetId," + getAssetAmount().value() + ",true),\n" +
                "Reissue(issueAssetId," + getAssetAmount().value() + ",true)";
        final String script = assetsFunctionBuilder(libVersion, "unit", functions, args, getAssetDAppPublicKey());

        getAssetDAppAccount().setScript(script);

        dAppCall = getAssetDAppAccount().setDataAssetId(Base58.decode(getAssetId().toString()));
        amountAfterInvokeIssuedAsset = getIssueAssetVolume() + getAssetAmount().value();
        amountAfterInvokeDAppIssuedAsset = Integer.parseInt(getAssetData().get(VOLUME)) + getAssetAmount().value();

        amounts.clear();
        amounts.add(getAssetAmount());

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);
    }

    public static  void prepareDataForLeaseTests() {
        final int libVersion = getRandomInt(5, MAX_LIB_VERSION);
        invokeFee = SUM_FEE + ONE_WAVES;

        final String functionArgs = "address:ByteVector";
        final String functions = "[\nLease(Address(address), " + getWavesAmount().value() + ")\n]\n";
        final String script = defaultFunctionBuilder(functionArgs, functions, libVersion);

        getDAppAccount().setScript(script);

        dAppCall = getDAppAccount().setData(getCallerAddressBase58());

        amounts.clear();
        amounts.add(getWavesAmount());
    }

    public static  void prepareDataForLeaseCancelTests() {
        final int libVersion = getRandomInt(5, MAX_LIB_VERSION);
        invokeFee = SUM_FEE + ONE_WAVES;

        final String functionArgs = "leaseId:ByteVector";
        final String functions = "[\nLeaseCancel(leaseId)\n]\n";
        final String script = defaultFunctionBuilder(functionArgs, functions, libVersion);

        getDAppAccount().setScript(script);

        amounts.clear();
        amounts.add(getWavesAmount());

        leaseId = Base58.decode(getDAppAccount().lease(getCallerAccount(), getWavesAmount().value()).tx().id().toString());
        dAppCall = getDAppAccount().setData(leaseId);
    }

    public static  void prepareDataForSponsorFeeTests() {
        invokeFee = ONE_WAVES + SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functions = "SponsorFee(assetId, " + getAssetAmount().value() + ")," +
                "\n\tSponsorFee(issueAssetId, " + getAssetAmount().value() + ")";
        final String script = assetsFunctionBuilder(libVersion, "unit", functions, args, getAssetDAppPublicKey());
        getAssetDAppAccount().setScript(script);

        dAppCall = getAssetDAppAccount().setDataAssetId(Base58.decode(getAssetId().toString()));

        amounts.clear();

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);
    }

    public static  void prepareDataForScriptTransferTests() {
        invokeFee = ONE_WAVES + SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String currentArgs = args + ", " + "address:ByteVector";
        final String functions = "ScriptTransfer(Address(address), " + getAssetAmount().value() + ", assetId),\n" +
                "ScriptTransfer(Address(address)," + getAssetAmount().value() + ", issueAssetId),\n" +
                "ScriptTransfer(Address(address), " + getWavesAmount().value() + ", unit)";
        final String script = assetsFunctionBuilder(libVersion, "unit", functions, currentArgs, getAssetDAppPublicKey());
        getAssetDAppAccount().setScript(script);

        dAppCall = getAssetDAppAccount().setDataAssetAndAddress(Base58.decode(getAssetId().toString()), getDAppAddressBase58());

        amounts.clear();
        amounts.add(getWavesAmount());
        amounts.add(getAssetAmount());

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);
    }

    public static  void prepareDataForPaymentsTests() {
        invokeFee = ONE_WAVES + SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String arg = "intVal:Int";
        final String functions = "IntegerEntry(\"int\", intVal)";
        final String script = assetsFunctionBuilder(libVersion, "unit", functions, arg, getDAppPublicKey());
        getDAppAccount().setScript(script);

        dAppCall = getDAppAccount().setData(getIntArg());

        amounts.clear();
        amounts.add(getWavesAmount());
        amounts.add(getAssetAmount());

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);
    }

    public static  void prepareDataForDAppToDAppTests() {
        invokeFee = SUM_FEE + ONE_WAVES;
        final int libVersion = getRandomInt(5, MAX_LIB_VERSION);

        final String functionArgsDApp1 = "dapp2:ByteVector, a:Int, key1:String, key2:String, assetId:ByteVector";
        final String dApp1Body =
                "strict res = invoke(Address(dapp2),\"bar\",[a, assetId],[AttachedPayment(assetId," + getAssetAmount().value() + ")])\n" +
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
                        "         ScriptTransfer(i.caller, " + getWavesAmount().value() + ", unit)\n" +
                        "      ],\n" +
                        "      a*2\n" +
                        "   )\n" +
                        "}";

        getDAppAccount().setScript(dApp1);
        getAssetDAppAccount().setScript(dApp2);

        dAppCall = getDAppAccount()
                .setData(
                        getAssetDAppAddressBase58(),
                        getIntArg(),
                        key1ForDAppEqualBar,
                        key2ForDAppEqualBalance,
                        getAssetId().bytes());

        amounts.clear();
        amounts.add(getWavesAmount());
        amounts.add(getAssetAmount());
    }

    public static DAppCall getDAppCall() {
        return dAppCall;
    }

    public static List<Amount> getAmounts() {
        return amounts;
    }

    public static long getInvokeFee() {
        return invokeFee;
    }

    public static byte[] getLeaseId() {
        return leaseId;
    }

    public static long getAmountAfterInvokeIssuedAsset() {
        return amountAfterInvokeIssuedAsset;
    }

    public static long getAmountAfterInvokeDAppIssuedAsset() {
        return amountAfterInvokeDAppIssuedAsset;
    }

    public static Map<String, String> getAssetDataForIssue() {
        return assetDataForIssue;
    }

    public static String getKey1ForDAppEqualBar() {
        return key1ForDAppEqualBar;
    }

    public static String getKey2ForDAppEqualBalance() {
        return key2ForDAppEqualBalance;
    }

    public static String getDAppAccountPublicKeyHash() {
        return dAppAccountPublicKeyHash;
    }

    public static String getDAppAccountAddress() {
        return dAppAccountAddress;
    }

    public static String getDAppFunctionName() {
        return dAppFunctionName;
    }
}
