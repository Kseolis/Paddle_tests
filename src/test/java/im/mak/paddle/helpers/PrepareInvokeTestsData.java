package im.mak.paddle.helpers;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.common.Base64String;
import im.mak.paddle.Account;
import im.mak.paddle.dapp.DAppCall;
import im.mak.paddle.dapps.AssetDAppAccount;
import im.mak.paddle.dapps.DataDApp;

import java.util.*;

import static im.mak.paddle.helpers.ConstructorRideFunctions.*;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.helpers.Randomizer.randomNumAndLetterString;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setExtraFee;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setFee;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.Constants.ONE_WAVES;

public class PrepareInvokeTestsData {
    private static Account callerAccount;
    private static String callerAddress;
    private static String callerPublicKey;
    private static String callerPublicKeyHash;
    private static byte[] callerAddressBase58;

    private static DataDApp dAppAccount;
    private static String dAppAddress;
    private static String dAppPublicKey;
    private static byte[] dAppAddressBase58;

    private static AssetDAppAccount assetDAppAccount;
    private static String assetDAppAddress;
    private static String assetDAppPublicKey;
    private static byte[] assetDAppAddressBase58;
    private static AssetId assetId;

    private static int intArg;
    private static Base64String base64String;
    private static boolean boolArg;
    private static String stringArg;
    private static byte[] leaseId;

    private static Amount wavesAmount;
    private static Amount assetAmount;
    private static long amountAfterInvokeIssuedAsset;
    private static long amountAfterInvokeDAppIssuedAsset;

    private static final String args = "assetId:ByteVector";
    private static final String key1ForDAppEqualBar = "bar";
    private static final String key2ForDAppEqualBalance = "balance";
    private static DAppCall dAppCall;
    private static long fee;

    private static final Map<String, String> assetData =  new HashMap<>();
    private static final Map<String, String> assetDataForIssue =  new HashMap<>();
    private static final List<Amount> amounts = new ArrayList<>();

    public PrepareInvokeTestsData() {
        String name = randomNumAndLetterString(3) + "_asset";
        String description = name + "_dscrpt";
        assetData.put(DECIMALS, String.valueOf(getRandomInt(0, 8)));
        assetData.put(DESCRIPTION, description);
        assetData.put(NAME, name);
        assetData.put(REISSUE, "true");
        assetData.put(VOLUME, String.valueOf(getRandomInt(700_000_000, 900_000_000)));

        async(
                () -> {
                    callerAccount = new Account(DEFAULT_FAUCET);
                    callerAddress = callerAccount.address().toString();
                    callerPublicKey = callerAccount.publicKey().toString();
                    callerPublicKeyHash = Base58.encode(callerAccount.address().publicKeyHash());
                    callerAddressBase58 = Base58.decode(callerAddress);
                },
                () -> base64String = new Base64String(randomNumAndLetterString(6)),
                () -> stringArg = randomNumAndLetterString(10),
                () -> {
                    intArg = getRandomInt(1, 1000);
                    boolArg = intArg % 2 == 0;
                },
                () -> {
                    assetDAppAccount = new AssetDAppAccount(DEFAULT_FAUCET, "true");
                    assetDAppAddress = assetDAppAccount.address().toString();
                    assetDAppPublicKey = assetDAppAccount.publicKey().toString();
                    assetDAppAddressBase58 = Base58.decode(assetDAppAddress);
                    assetId = assetDAppAccount.issue(i -> i
                                    .decimals(Integer.parseInt(assetData.get(DECIMALS)))
                                    .description(description)
                                    .name(assetData.get(NAME))
                                    .quantity(Integer.parseInt(assetData.get(VOLUME)))
                                    .reissuable(Boolean.parseBoolean(assetData.get(REISSUE)))
                            ).tx().assetId();

                    assetData.put(ISSUER, assetDAppPublicKey);
                    assetData.put(ASSET_ID, assetId.toString());
                },
                () -> {
                    dAppAccount = new DataDApp(DEFAULT_FAUCET, "true");
                    dAppAddress = dAppAccount.address().toString();
                    dAppPublicKey = dAppAccount.publicKey().toString();
                    dAppAddressBase58 = Base58.decode(dAppAddress);
                }
        );
        assetDAppAccount.transfer(callerAccount, Amount.of(300_000_000L, assetId));
        assetDAppAccount.transfer(dAppAccount, Amount.of(300_000_000L, assetId));
        wavesAmount = Amount.of(getRandomInt(100, 100000));
        assetAmount = Amount.of(getRandomInt(100, 100000), assetId);
    }

    public void prepareDataForDataDAppTests() {
        fee = SUM_FEE;

        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functionArgs = "intVal:Int, binVal:ByteVector, boolVal:Boolean, strVal:String";
        final String functions = "[\nIntegerEntry(\"int\", intVal),\nBinaryEntry(\"byte\", binVal),\n" +
                "BooleanEntry(\"bool\", boolVal),\nStringEntry(\"str\", strVal)\n]\n";
        final String script = defaultFunctionBuilder(functionArgs, functions, libVersion);

        dAppAccount.setScript(script);

        dAppCall = dAppAccount.setData(intArg, base64String, boolArg, stringArg);

        amounts.clear();
        amounts.add(wavesAmount);

        setFee(fee);
        setExtraFee(0);
    }

    public void prepareDataForDeleteEntryTests() {
        fee = SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functionArgs = "intVal:Int";
        final String functions = "[\nIntegerEntry(\"int\", intVal),\nDeleteEntry(\"int\")\n]\n";
        final String script = defaultFunctionBuilder(functionArgs, functions, libVersion);
        dAppAccount.setScript(script);

        dAppCall = dAppAccount.setData(intArg);

        amounts.clear();
        amounts.add(wavesAmount);

        setFee(fee);
        setExtraFee(0);
    }

    public void prepareDataForBurnTests() {
        fee = ONE_WAVES + SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functions = "Burn(assetId, " + assetAmount.value() + ")," +
                "\nBurn(issueAssetId, " + assetAmount.value() + ")";
        final String script = assetsFunctionBuilder(libVersion, "unit", functions, args, getAssetDAppPublicKey());
        assetDAppAccount.setScript(script);

        dAppCall = assetDAppAccount.setDataAssetId(Base58.decode(assetId.toString()));
        amountAfterInvokeIssuedAsset = getIssueAssetVolume() - assetAmount.value();
        amountAfterInvokeDAppIssuedAsset = Integer.parseInt(assetData.get(VOLUME)) - assetAmount.value();

        amounts.clear();
        amounts.add(assetAmount);

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);
    }

    public void prepareDataForIssueTests() {
        final long extraFee = ONE_WAVES * 2;
        fee = SUM_FEE + extraFee;
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

        assetDAppAccount.setScript(script);

        dAppCall = assetDAppAccount.setDataAssetId();

        amounts.clear();

        setFee(SUM_FEE);
        setExtraFee(extraFee);
    }

    public void prepareDataForReissueTests() {
        fee = ONE_WAVES + SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functions = "Reissue(assetId," + assetAmount.value() + ",true),\n" +
                "Reissue(issueAssetId," + assetAmount.value() + ",true)";
        final String script = assetsFunctionBuilder(libVersion, "unit", functions, args, getAssetDAppPublicKey());

        assetDAppAccount.setScript(script);

        dAppCall = assetDAppAccount.setDataAssetId(Base58.decode(assetId.toString()));
        amountAfterInvokeIssuedAsset = getIssueAssetVolume() + assetAmount.value();
        amountAfterInvokeDAppIssuedAsset = Integer.parseInt(assetData.get(VOLUME)) + assetAmount.value();

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

        dAppCall = dAppAccount.setData(callerAddressBase58);

        amounts.clear();
        amounts.add(wavesAmount);
        setFee(SUM_FEE);
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

        leaseId = Base58.decode(dAppAccount.lease(callerAccount, wavesAmount.value()).tx().id().toString());
        dAppCall = dAppAccount.setData(leaseId);
    }

    public void prepareDataForSponsorFeeTests() {
        fee = ONE_WAVES + SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functions = "SponsorFee(assetId, " + assetAmount.value() + ")," +
                "\n\tSponsorFee(issueAssetId, " + assetAmount.value() + ")";
        final String script = assetsFunctionBuilder(libVersion, "unit", functions, args, getAssetDAppPublicKey());
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
                "ScriptTransfer(Address(address)," + assetAmount.value() + ", issueAssetId),\n" +
                "ScriptTransfer(Address(address), " + wavesAmount.value() + ", unit)";
        final String script = assetsFunctionBuilder(libVersion, "unit", functions, currentArgs, getAssetDAppPublicKey());
        assetDAppAccount.setScript(script);

        dAppCall = assetDAppAccount.setDataAssetAndAddress(Base58.decode(assetId.toString()), dAppAddressBase58);

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
        final String script = assetsFunctionBuilder(libVersion, "unit", functions, arg, getDAppPublicKey());
        dAppAccount.setScript(script);

        dAppCall = dAppAccount.setData(intArg);

        amounts.clear();
        amounts.add(wavesAmount);
        amounts.add(assetAmount);

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);
    }

    public void prepareDataForDAppToDAppTests() {
        final int libVersion = getRandomInt(5, MAX_LIB_VERSION);

        final String functionArgsDApp1 = "dapp2:ByteVector, a:Int, key1:String, key2:String, assetId:ByteVector";
        final String dApp1Body =
                "strict res = invoke(Address(dapp2),\"bar\",[a, assetId],[AttachedPayment(assetId," + assetAmount.value() + ")])\n" +
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

        dAppCall = dAppAccount.setData(assetDAppAddressBase58, intArg, key1ForDAppEqualBar, key2ForDAppEqualBalance, assetId.bytes());

        amounts.clear();
        amounts.add(wavesAmount);
        amounts.add(assetAmount);

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

    public static Base64String getBase64String() {
        return base64String;
    }

    public static boolean getBoolArg() {
        return boolArg;
    }

    public static String getStringArg() {
        return stringArg;
    }

    public static byte[] getLeaseId() {
        return leaseId;
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

    public static long getAmountAfterInvokeIssuedAsset() {
        return amountAfterInvokeIssuedAsset;
    }

    public static long getAmountAfterInvokeDAppIssuedAsset() {
        return amountAfterInvokeDAppIssuedAsset;
    }

    public static String getCallerAddress() {
        return callerAddress;
    }

    public static String getDAppAddress() {
        return dAppAddress;
    }

    public static String getAssetDAppAddress() {
        return assetDAppAddress;
    }

    public static String getCallerPublicKey() {
        return callerPublicKey;
    }

    public static String getCallerPublicKeyHash() {
        return callerPublicKeyHash;
    }

    public static String getDAppPublicKey() {
        return dAppPublicKey;
    }

    public static String getAssetDAppPublicKey() {
        return assetDAppPublicKey;
    }

    public static Map<String, String> getAssetData() {
        return assetData;
    }

    public static Map<String, String> getAssetDataForIssue() {
        return assetDataForIssue;
    }

    public static byte[] getDAppAddressBase58() {
        return dAppAddressBase58;
    }

    public static String getKey1ForDAppEqualBar() {
        return key1ForDAppEqualBar;
    }

    public static String getKey2ForDAppEqualBalance() {
        return key2ForDAppEqualBalance;
    }

}
