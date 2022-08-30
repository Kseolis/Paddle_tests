package im.mak.paddle.blockchain_updates.subscribe_invoke_tx_tests;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.common.Base64String;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseTest;
import im.mak.paddle.helpers.dapps.AssetDAppAccount;
import im.mak.paddle.helpers.dapps.DataDApp;
import org.junit.jupiter.api.BeforeAll;

import java.util.HashMap;
import java.util.Map;

import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.helpers.Randomizer.randomNumAndLetterString;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;

public class SubscribeInvokeBaseTest extends BaseTest {
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

    private static Amount wavesAmount;
    private static Amount assetAmount;
    private static final Map<String, String> assetData = new HashMap<>();

    @BeforeAll
    static void before() {
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
        wavesAmount = Amount.of(getRandomInt(10, 10000));
        assetAmount = Amount.of(getRandomInt(10, 10000), assetId);
    }


    public static AssetId getAssetId() {
        return assetId;
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

    public static byte[] getDAppAddressBase58() {
        return dAppAddressBase58;
    }

    public static Map<String, String> getAssetData() {
        return assetData;
    }

    public static byte[] getCallerAddressBase58() {
        return callerAddressBase58;
    }

    public static byte[] getAssetDAppAddressBase58() {
        return assetDAppAddressBase58;
    }

    public static boolean isBoolArg() {
        return boolArg;
    }
}
