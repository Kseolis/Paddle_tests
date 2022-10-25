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
    private DataDApp dAppAccount;
    private String dAppAddress;
    private String dAppPublicKey;
    private String dAppPublicKeyHash;
    private byte[] dAppAddressBytes;

    private AssetDAppAccount assetDAppAccount;
    private String assetDAppAddress;
    private String assetDAppPublicKey;
    private String assetDAppPublicKeyHash;
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
    private final Amount assetAmount;
    private final String args = "assetId:ByteVector";
    private final String key1ForDAppEqualBar = "bar";
    private final String key2ForDAppEqualBalance = "balance";

    private final Map<String, String> assetData = new HashMap<>();
    private final Map<String, String> assetDataForIssue = new HashMap<>();
    private final List<Amount> amounts = new ArrayList<>();

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
                    assetDAppPublicKeyHash = Base58.encode(assetDAppAccount.address().publicKeyHash());
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
                    dAppAccount = new DataDApp(DEFAULT_FAUCET, "true");
                    dAppAddress = dAppAccount.address().toString();
                    dAppPublicKey = dAppAccount.publicKey().toString();
                    dAppPublicKeyHash = Base58.encode(dAppAccount.address().publicKeyHash());
                    dAppAddressBytes = Base58.decode(dAppAddress);
                }
        );
        assetDAppAccount.transfer(callerAccount, Amount.of(300_000_000L, assetId));
        assetDAppAccount.transfer(dAppAccount, Amount.of(300_000_000L, assetId));
        wavesAmount = Amount.of(getRandomInt(10, 10000));
        assetAmount = Amount.of(getRandomInt(10, 10000), assetId);
    }

    public void prepareDataForDataDAppTests() {
        invokeFee = SUM_FEE + ONE_WAVES;

        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functionArgs = "intVal:Int, binVal:ByteVector, boolVal:Boolean, strVal:String";
        final String functions = "[\nIntegerEntry(\"int\", intVal),\nBinaryEntry(\"byte\", binVal),\n" +
                "BooleanEntry(\"bool\", boolVal),\nStringEntry(\"str\", strVal)\n]\n";
        final String script = defaultFunctionBuilder(functionArgs, functions, libVersion);

        dAppAccount.setScript(script);

        dAppCall = dAppAccount.setData(intArg, base64String, boolArg, stringArg);

        amounts.clear();
        amounts.add(wavesAmount);

        setFee(invokeFee);
        setExtraFee(ONE_WAVES);
    }

    public void prepareDataForDeleteEntryTests() {
        invokeFee = SUM_FEE;
        final int libVersion = getRandomInt(4, MAX_LIB_VERSION);

        final String functionArgs = "intVal:Int";
        final String functions = "[\nIntegerEntry(\"int\", intVal),\nDeleteEntry(\"int\")\n]\n";
        final String script = defaultFunctionBuilder(functionArgs, functions, libVersion);
        dAppAccount.setScript(script);

        dAppCall = dAppAccount.setData(intArg);

        amounts.clear();
        amounts.add(wavesAmount);

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
        amountAfterInvokeIssuedAsset = getIssueAssetVolume() - assetAmount.value();
        amountAfterInvokeDAppIssuedAsset = Integer.parseInt(assetData.get(VOLUME)) - assetAmount.value();

        amounts.clear();
        amounts.add(assetAmount);

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

        amounts.clear();

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
        amountAfterInvokeIssuedAsset = getIssueAssetVolume() + assetAmount.value();
        amountAfterInvokeDAppIssuedAsset = Integer.parseInt(assetData.get(VOLUME)) + assetAmount.value();

        amounts.clear();
        amounts.add(assetAmount);

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);
    }

    public void prepareDataForLeaseTests() {
        final int libVersion = getRandomInt(5, MAX_LIB_VERSION);
        invokeFee = SUM_FEE + ONE_WAVES;

        final String functionArgs = "address:ByteVector";
        final String functions = "[\nLease(Address(address), " + wavesAmount.value() + ")\n]\n";
        final String script = defaultFunctionBuilder(functionArgs, functions, libVersion);
        dAppAccount.setScript(script);

        dAppCall = dAppAccount.setData(callerAddressBytes);

        amounts.clear();
        amounts.add(wavesAmount);

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);
    }

    public void prepareDataForLeaseCancelTests() {
        final int libVersion = getRandomInt(5, MAX_LIB_VERSION);
        invokeFee = SUM_FEE + ONE_WAVES;

        final String functionArgs = "leaseId:ByteVector";
        final String functions = "[\nLeaseCancel(leaseId)\n]\n";
        final String script = defaultFunctionBuilder(functionArgs, functions, libVersion);

        dAppAccount.setScript(script);

        amounts.clear();
        amounts.add(wavesAmount);

        leaseId = Base58.decode(dAppAccount.lease(callerAccount, wavesAmount.value()).tx().id().toString());
        dAppCall = dAppAccount.setData(leaseId);

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);
    }

    public void prepareDataForSponsorFeeTests() {
        invokeFee = ONE_WAVES + SUM_FEE;
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

        amounts.clear();
        amounts.add(wavesAmount);
        amounts.add(assetAmount);

        setFee(SUM_FEE);
        setExtraFee(ONE_WAVES);
    }

    public void prepareDataForPaymentsTests() {
        invokeFee = ONE_WAVES + SUM_FEE;
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

        dAppCall = dAppAccount.setData
                (assetDAppAddressBytes, intArg, key1ForDAppEqualBar, key2ForDAppEqualBalance, assetId.bytes());

        amounts.clear();
        amounts.add(wavesAmount);
        amounts.add(assetAmount);
    }

    public void prepareDataForDoubleNestingTest(long fee) {
        invokeFee = fee;
        DataDApp otherDAppAccount = new DataDApp(DEFAULT_FAUCET, "true");
        String otherDAppAddress = otherDAppAccount.address().toString();
        String otherDAppPublicKey = otherDAppAccount.publicKey().toString();
        String otherDAppPublicKeyHash = Base58.encode(otherDAppAccount.address().publicKeyHash());
        byte[] otherDAppAddressBytes = Base58.decode(otherDAppAddress);
        final int libVersion = getRandomInt(5, MAX_LIB_VERSION);

        final String functionArgsDApp1 = "acc1:ByteVector ,acc2:ByteVector, a:Int, key1:String, key2:String, assetId:ByteVector";
        final String dApp1Body =
                "strict res = invoke(Address(acc2),\"bar\",[a, assetId, acc1]," +
                        "[AttachedPayment(assetId," + assetAmount.value() + ")])\n" +
                        "   match res {\n" +
                        "   case r : Int => \n(\n[\n" +
                        "   IntegerEntry(key1, r),\n" +
                        "   IntegerEntry(key2, wavesBalance(Address(acc2)).regular)\n" +
                        "], unit\n" +
                        ")\n" +
                        "\tcase _ => throw(\"Incorrect invoke result for res in dApp 1\")}\n";
        final String dApp1 = defaultFunctionBuilder(functionArgsDApp1, dApp1Body, libVersion);

        final String dApp2 =
                "{-# STDLIB_VERSION 5 #-}\n{-# CONTENT_TYPE DAPP #-}\n{-# SCRIPT_TYPE ACCOUNT #-}\n" +
                        "@Callable(i)\n" +
                        "func bar(a: Int, assetId: ByteVector, acc1: ByteVector) = {\n" +
                        "strict res2 = invoke(Address(acc1),\"baz\",[a],[])\n" +
                        "   match res2 {" +
                        "   \ncase r : Int =>\n" +
                        "   (\n" +
                        "      [\n" +
                        "           ScriptTransfer(i.caller, " + wavesAmount.value() + ", unit)\n" +
                        "      ], a\n" +
                        "   )\n" +
                        "\tcase _ => throw(\"Incorrect invoke result for res2\")\n" +
                        "\t}" +
                        "}";

        final String dApp3 =
                "\n{-# STDLIB_VERSION 5 #-}" +
                        "\n{-# CONTENT_TYPE DAPP #-}" +
                        "\n{-# SCRIPT_TYPE ACCOUNT #-}\n" +
                        "@Callable(i)\n" +
                        "func baz(a: Int) = {\n" +
                        "   (\n" +
                        "      [\n" +
                        "           ScriptTransfer(i.caller, " + wavesAmount.value() + ", unit)\n" +
                        "      ],\n" +
                        "      a * 2\n" +
                        "   )\n" +
                        "}";

        System.out.println(dApp1);
        System.out.println(dApp2);
        System.out.println(dApp3);

        dAppAccount.setScript(dApp1);
        assetDAppAccount.setScript(dApp2);
        otherDAppAccount.setScript(dApp3);

        dAppCall = dAppAccount.setData
                (otherDAppAddressBytes, assetDAppAddressBytes, intArg, key1ForDAppEqualBar, key2ForDAppEqualBalance, assetId.bytes());

        amounts.clear();
        amounts.add(wavesAmount);
        amounts.add(assetAmount);
    }

    public DAppCall getDAppCall() {
        return dAppCall;
    }

    public List<Amount> getAmounts() {
        return amounts;
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

    public String getAssetDAppPublicKeyHash() {
        return assetDAppPublicKeyHash;
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

    public byte[] getDAppAddressBase58() {
        return dAppAddressBytes;
    }

    public String getKey1ForDAppEqualBar() {
        return key1ForDAppEqualBar;
    }

    public String getKey2ForDAppEqualBalance() {
        return key2ForDAppEqualBalance;
    }

    public byte[] getCallerAddressBytes() {
        return callerAddressBytes;
    }

    public byte[] getAssetDAppAddressBytes() {
        return assetDAppAddressBytes;
    }

}
