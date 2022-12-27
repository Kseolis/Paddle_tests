package im.mak.paddle.helpers;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.common.Base64String;
import im.mak.paddle.Account;
import im.mak.paddle.dapp.DAppCall;
import im.mak.paddle.helpers.dapps.AssetDAppAccount;
import im.mak.paddle.helpers.dapps.DataDApp;

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
    private Account callerAccount;
    private String callerAddress;
    private String callerPublicKey;
    private String callerPublicKeyHash;
    private byte[] callerAddressBytes;
    private Account secondCallerAccount;
    private Account thirdCallerAccount;
    private DataDApp dAppAccount;
    private String dAppAddress;
    private String dAppPublicKey;
    private String dAppPublicKeyHash;
    private byte[] dAppAddressBytes;
    private DataDApp otherDAppAccount;
    private String otherDAppAddress;
    private byte[] otherDAppAddressBytes;
    private AssetDAppAccount assetDAppAccount;
    private String assetDAppAddress;
    private String assetDAppPublicKey;
    private byte[] assetDAppAddressBytes;
    private AssetId assetId;
    private int intArg;
    private Base64String base64String;
    private boolean boolArg;
    private String stringArg;
    private byte[] leaseId;
    private long amountAfterInvokeIssuedAsset;
    private long amountAfterInvokeDAppIssuedAsset;
    private DAppCall dAppCall;
    private long invokeFee;
    private final Amount wavesAmount;
    private final Amount secondWavesAmount;
    private final Amount assetAmount;
    private final String args = "assetId:ByteVector";
    private final String keyForDAppEqualBar = "bar";
    private final String keyForDAppEqualBaz = "baz";
    private final String key2ForDAppEqualBalance = "balance";
    private final Map<String, String> assetData = new HashMap<>();
    private final Map<String, String> assetDataForIssue = new HashMap<>();
    private final List<Amount> payments = new ArrayList<>();
    private final List<Amount> otherAmounts = new ArrayList<>();

    public PrepareInvokeTestsData() {
        String name = randomNumAndLetterString(3) + "_asset";
        String description = name + "_dscrpt";
        assetData.put(DECIMALS, String.valueOf(getRandomInt(0, 8)));
        assetData.put(DESCRIPTION, description);
        assetData.put(NAME, name);
        assetData.put(REISSUE, "true");
        assetData.put(VOLUME, String.valueOf(900_000_000));

        async(
                () -> {
                    callerAccount = new Account(DEFAULT_FAUCET);
                    callerAddress = callerAccount.address().toString();
                    callerPublicKey = callerAccount.publicKey().toString();
                    callerPublicKeyHash = Base58.encode(callerAccount.address().publicKeyHash());
                    callerAddressBytes = Base58.decode(callerAddress);
                },
                () -> secondCallerAccount = new Account(FIVE_WAVES),
                () -> thirdCallerAccount = new Account(ONE_WAVES),
                () -> base64String = new Base64String(randomNumAndLetterString(6)),
                () -> stringArg = randomNumAndLetterString(10),
                () -> {
                    intArg = getRandomInt(1, 1000);
                    boolArg = intArg % 2 == 0;
                },
                () -> {
                    assetDAppAccount = new AssetDAppAccount(FIVE_WAVES, "true");
                    assetDAppAddress = assetDAppAccount.address().toString();
                    assetDAppPublicKey = assetDAppAccount.publicKey().toString();
                    assetDAppAddressBytes = Base58.decode(assetDAppAddress);
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
                    dAppAccount = new DataDApp(FIVE_WAVES, "true");
                    dAppAddress = dAppAccount.address().toString();
                    dAppPublicKey = dAppAccount.publicKey().toString();
                    dAppPublicKeyHash = Base58.encode(dAppAccount.address().publicKeyHash());
                    dAppAddressBytes = Base58.decode(dAppAddress);
                },
                () -> {
                    otherDAppAccount = new DataDApp(FIVE_WAVES, "true");
                    otherDAppAddress = otherDAppAccount.address().toString();
                    otherDAppAddressBytes = Base58.decode(otherDAppAddress);
                }
        );
        assetDAppAccount.transfer(callerAccount, Amount.of(200_000_000L, assetId));
        assetDAppAccount.transfer(dAppAccount, Amount.of(200_000_000L, assetId));
        wavesAmount = Amount.of(getRandomInt(10, 10000));
        secondWavesAmount = Amount.of(getRandomInt(10001, 20000));
        assetAmount = Amount.of(getRandomInt(10, 10000), assetId);
    }

    public void prepareDataForDataDAppTests(long fee, long extraFee) {
        invokeFee = fee + extraFee;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);
        final String functionArgs = "intVal:Int, binVal:ByteVector, boolVal:Boolean, strVal:String";
        final String functions = "[\nIntegerEntry(\"int\", intVal),\nBinaryEntry(\"byte\", binVal),\n" +
                "BooleanEntry(\"bool\", boolVal),\nStringEntry(\"str\", strVal)\n]\n";
        final String script = defaultFunctionBuilder(functionArgs, functions, libVersion);

        dAppAccount.setScript(script);
        dAppCall = dAppAccount.setData(intArg, base64String, boolArg, stringArg);

        payments.clear();
        payments.add(wavesAmount);

        setFee(invokeFee);
        setExtraFee(extraFee);
    }

    public void prepareDataForDeleteEntryTests() {
        invokeFee = SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functionArgs = "intVal:Int";
        final String functions = "[\nIntegerEntry(\"int\", intVal),\nDeleteEntry(\"int\")\n]\n";
        final String script = defaultFunctionBuilder(functionArgs, functions, libVersion);
        dAppAccount.setScript(script);

        dAppCall = dAppAccount.setData(intArg);

        payments.clear();
        payments.add(wavesAmount);

        setFee(invokeFee);
        setExtraFee(0);
    }

    public void prepareDataForBurnTests() {
        invokeFee = ONE_WAVES + SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functions = "Burn(assetId, " + assetAmount.value() + ")," +
                "\nBurn(issueAssetId, " + assetAmount.value() + ")";
        final String script = assetsFunctionBuilder(libVersion, "unit", functions, args, getAssetDAppPublicKey());
        assetDAppAccount.setScript(script);

        dAppCall = assetDAppAccount.setDataAssetId(Base58.decode(assetId.toString()));
        amountAfterInvokeIssuedAsset = Long.parseLong(getIssueAssetData().get(VOLUME)) - assetAmount.value();
        amountAfterInvokeDAppIssuedAsset = Integer.parseInt(assetData.get(VOLUME)) - assetAmount.value();

        payments.clear();
        otherAmounts.clear();
        otherAmounts.add(assetAmount);

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);
    }

    public void prepareDataForIssueTests() {
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

        assetDAppAccount.setScript(script);

        dAppCall = assetDAppAccount.setDataAssetId();

        payments.clear();

        setFee(SUM_FEE);
        setExtraFee(extraFee);
    }

    public void prepareDataForReissueTests() {
        invokeFee = ONE_WAVES + SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functions = "Reissue(assetId," + assetAmount.value() + ",true),\n" +
                "Reissue(issueAssetId," + assetAmount.value() + ",true)";
        final String script = assetsFunctionBuilder(libVersion, "unit", functions, args, getAssetDAppPublicKey());

        assetDAppAccount.setScript(script);

        dAppCall = assetDAppAccount.setDataAssetId(Base58.decode(assetId.toString()));
        amountAfterInvokeIssuedAsset = Long.parseLong(getIssueAssetData().get(VOLUME)) + assetAmount.value();
        amountAfterInvokeDAppIssuedAsset = Integer.parseInt(assetData.get(VOLUME)) + assetAmount.value();

        payments.clear();
        otherAmounts.clear();
        otherAmounts.add(assetAmount);

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);
    }

    public void prepareDataForLeaseTests(long fee, long extraFee) {
        final int libVersion = getRandomInt(5, MAX_LIB_VERSION);
        invokeFee = fee + extraFee;

        final String functionArgs = "address:ByteVector";
        final String functions = "[\nLease(Address(address), " + wavesAmount.value() + ")\n]\n";
        final String script = defaultFunctionBuilder(functionArgs, functions, libVersion);
        dAppAccount.setScript(script);

        dAppCall = dAppAccount.setData(callerAddressBytes);

        payments.clear();
        payments.add(wavesAmount);

        setFee(fee);
        setExtraFee(extraFee);
    }

    public void prepareDataForLeaseCancelTests(long fee, long extraFee) {
        final int libVersion = getRandomInt(5, MAX_LIB_VERSION);
        invokeFee = fee + extraFee;

        final String functionArgs = "leaseId:ByteVector";
        final String functions = "[\nLeaseCancel(leaseId)\n]\n";
        final String script = defaultFunctionBuilder(functionArgs, functions, libVersion);

        dAppAccount.setScript(script);

        payments.clear();
        payments.add(wavesAmount);

        leaseId = Base58.decode(dAppAccount.lease(callerAccount, wavesAmount.value()).tx().id().toString());
        dAppCall = dAppAccount.setData(leaseId);

        setFee(fee);
        setExtraFee(extraFee);
    }

    public void prepareDataForSponsorFeeTests() {
        invokeFee = ONE_WAVES + SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functions = "SponsorFee(assetId, " + assetAmount.value() + ")," +
                "\n\tSponsorFee(issueAssetId, " + assetAmount.value() + ")";
        final String script = assetsFunctionBuilder(libVersion, "unit", functions, args, getAssetDAppPublicKey());
        assetDAppAccount.setScript(script);

        dAppCall = assetDAppAccount.setDataAssetId(Base58.decode(assetId.toString()));

        payments.clear();
        otherAmounts.clear();
        otherAmounts.add(assetAmount);

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);
    }

    public void prepareDataForScriptTransferTests() {
        invokeFee = ONE_WAVES + SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String currentArgs = args + ", " + "address:ByteVector";
        final String functions = "ScriptTransfer(Address(address), " + assetAmount.value() + ", assetId),\n" +
                "\tScriptTransfer(Address(address), " + assetAmount.value() + ", issueAssetId),\n" +
                "\tScriptTransfer(Address(address), " + wavesAmount.value() + ", unit),\n" +
                "\tScriptTransfer(i.caller, " + wavesAmount.value() + ", unit)";
        final String script = assetsFunctionBuilder(libVersion, "unit", functions, currentArgs, getAssetDAppPublicKey());
        assetDAppAccount.setScript(script);
        dAppCall = assetDAppAccount.setDataAssetAndAddress(Base58.decode(assetId.toString()), dAppAddressBytes);

        payments.clear();
        otherAmounts.clear();
        otherAmounts.add(wavesAmount);
        otherAmounts.add(assetAmount);

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);
    }

    public void prepareDataForDAppToDAppTests(long fee) {
        invokeFee = fee;
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
                        "let lease = Lease(i.caller, " + wavesAmount.value() + ")\n" +
                        "   (\n" +
                        "      [\n" +
                        "           ScriptTransfer(i.caller, " + wavesAmount.value() + ", unit),\n" +
                        "           SponsorFee(assetId, " + assetAmount.value() + "),\n" +
                        "           Burn(assetId, " + assetAmount.value() + "),\n" +
                        "           Reissue(assetId, " + assetAmount.value() + ", true),\n" +
                        "           lease,\n" +
                        "           LeaseCancel(lease.calculateLeaseId()),\n" +
                        "           IntegerEntry(\"int\", a),\n" +
                        "           DeleteEntry(\"int\")\n" +
                        "      ],\n" +
                        "      a * 2\n" +
                        "   )\n" +
                        "}";
        dAppAccount.setScript(dApp1);
        assetDAppAccount.setScript(dApp2);

        dAppCall = dAppAccount.setData(assetDAppAddressBytes, intArg, keyForDAppEqualBar, key2ForDAppEqualBalance, assetId.bytes());

        payments.clear();
        otherAmounts.clear();
        otherAmounts.add(wavesAmount);
        otherAmounts.add(assetAmount);
        setExtraFee(0);
    }

    public void prepareDataForDoubleNestedTest(long fee, String firstRecipient, String secondRecipient) {
        invokeFee = fee;
        final int libVersion = getRandomInt(5, MAX_LIB_VERSION);

        final String functionArgsDApp1 = "acc1:ByteVector ,acc2:ByteVector, a:Int, key1:String, key2:String, assetId:ByteVector";
        final String dApp1Body =
                "strict res = invoke(Address(acc2),\"bar\",[a, assetId, acc1]," +
                        "[AttachedPayment(assetId," + assetAmount.value() + ")])\n" +
                        "   match res {\n" +
                        "   case r : Int => \n(\n[\n" +
                        "   IntegerEntry(key1, r),\n" +
                        "   IntegerEntry(key2, wavesBalance(Address(acc2)).regular)\n]\n    )\n" +
                        "\tcase _ => throw(\"Incorrect invoke result for res in dApp 1\")}\n";
        final String dApp1 = defaultFunctionBuilder(functionArgsDApp1, dApp1Body, libVersion);

        final String dApp2 =
                "{-# STDLIB_VERSION 5 #-}\n{-# CONTENT_TYPE DAPP #-}\n{-# SCRIPT_TYPE ACCOUNT #-}\n" +
                        "@Callable(i)\n" +
                        "func bar(a: Int, assetId: ByteVector, acc1: ByteVector) = {\n" +
                        "strict res2 = invoke(Address(acc1),\"" + keyForDAppEqualBaz + "\",[a],[])\n" +
                        "   match res2 {" +
                        "   \ncase r : Int =>\n" +
                        "   (\n" +
                        "      [\n" +
                        "           ScriptTransfer(" + firstRecipient + ", " + wavesAmount.value() + ", unit)\n" +
                        "      ], a*2\n" +
                        "   )\n" +
                        "\tcase _ => throw(\"Incorrect invoke result for res2\")\n" +
                        "\t}" +
                        "}";

        final String dApp3 =
                "\n{-# STDLIB_VERSION 5 #-}" +
                        "\n{-# CONTENT_TYPE DAPP #-}" +
                        "\n{-# SCRIPT_TYPE ACCOUNT #-}\n" +
                        "@Callable(i)\n" +
                        "func " + keyForDAppEqualBaz + "(a: Int) = {\n" +
                        "   (\n" +
                        "      [\n" +
                        "           ScriptTransfer(" + secondRecipient + ", " + secondWavesAmount.value() + ", unit)\n" +
                        "      ],\n" +
                        "      a+2\n" +
                        "   )\n" +
                        "}";
        dAppAccount.setScript(dApp1);
        assetDAppAccount.setScript(dApp2);
        otherDAppAccount.setScript(dApp3);

        dAppCall = dAppAccount.setData(otherDAppAddressBytes, assetDAppAddressBytes, intArg, keyForDAppEqualBar, key2ForDAppEqualBalance, assetId.bytes());

        payments.clear();
        otherAmounts.clear();
        otherAmounts.add(wavesAmount);
        otherAmounts.add(secondWavesAmount);
        otherAmounts.add(assetAmount);
        setExtraFee(0);
    }

    public DAppCall getDAppCall() {
        return dAppCall;
    }

    public List<Amount> getPayments() {
        return payments;
    }

    public List<Amount> getOtherAmounts() {
        return otherAmounts;
    }

    public AssetId getAssetId() {
        return assetId;
    }

    public long getInvokeFee() {
        return invokeFee;
    }

    public int getIntArg() {
        return intArg;
    }

    public Base64String getBase64String() {
        return base64String;
    }

    public boolean getBoolArg() {
        return boolArg;
    }

    public String getStringArg() {
        return stringArg;
    }

    public byte[] getLeaseId() {
        return leaseId;
    }

    public Account getCallerAccount() {
        return callerAccount;
    }

    public DataDApp getDAppAccount() {
        return dAppAccount;
    }

    public AssetDAppAccount getAssetDAppAccount() {
        return assetDAppAccount;
    }

    public Amount getWavesAmount() {
        return wavesAmount;
    }

    public Amount getSecondWavesAmount() {
        return secondWavesAmount;
    }

    public Amount getAssetAmount() {
        return assetAmount;
    }

    public long getAmountAfterInvokeIssuedAsset() {
        return amountAfterInvokeIssuedAsset;
    }

    public long getAmountAfterInvokeDAppIssuedAsset() {
        return amountAfterInvokeDAppIssuedAsset;
    }

    public String getCallerAddress() {
        return callerAddress;
    }

    public String getDAppAddress() {
        return dAppAddress;
    }

    public String getAssetDAppAddress() {
        return assetDAppAddress;
    }

    public String getCallerPublicKey() {
        return callerPublicKey;
    }

    public String getCallerPublicKeyHash() {
        return callerPublicKeyHash;
    }

    public String getDAppPublicKey() {
        return dAppPublicKey;
    }

    public String getDAppPublicKeyHash() {
        return dAppPublicKeyHash;
    }

    public String getAssetDAppPublicKey() {
        return assetDAppPublicKey;
    }

    public Map<String, String> getAssetData() {
        return assetData;
    }

    public Map<String, String> getAssetDataForIssue() {
        return assetDataForIssue;
    }

    public String getKeyForDAppEqualBar() {
        return keyForDAppEqualBar;
    }

    public String getKey2ForDAppEqualBalance() {
        return key2ForDAppEqualBalance;
    }

    public DataDApp getOtherDAppAccount() {
        return otherDAppAccount;
    }

    public String getOtherDAppAddress() {
        return otherDAppAddress;
    }

    public Account getSecondCallerAccount() {
        return secondCallerAccount;
    }

    public Account getThirdCallerAccount() {
        return thirdCallerAccount;
    }

    public String getKeyForDAppEqualBaz() {
        return keyForDAppEqualBaz;
    }
}
