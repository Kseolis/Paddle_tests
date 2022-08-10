package im.mak.paddle.helpers;

import java.util.HashMap;
import java.util.Map;

import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.util.Constants.*;

public class ConstructorRideFunctions {
    private static final Map<String, String> issuedAssetData =  new HashMap<>();

    private static final int issueAssetDecimals = getRandomInt(0, 8);
    private static final String issuedAssetName = "issuedAsset" + getRandomInt(1, 99);
    private static final String issuedAssetDescription = "asset ride script " + getRandomInt(1, 9999999);
    private static final int issueAssetVolume = getRandomInt(700_000, 900_000_000);
    private static final boolean issueAssetReissuable = true;
    private static final int issueAssetNonce = getRandomInt(0, 10);

    public static String assetsFunctionBuilder(int libVersion, String script, String functions, String args, String pk) {
        issuedAssetData.put(ASSET_ID, null);
        issuedAssetData.put(ISSUER, pk);
        issuedAssetData.put(DECIMALS, String.valueOf(issueAssetDecimals));
        issuedAssetData.put(DESCRIPTION, issuedAssetDescription);
        issuedAssetData.put(NAME, issuedAssetName);
        issuedAssetData.put(REISSUE, String.valueOf(issueAssetReissuable));
        issuedAssetData.put(VOLUME, String.valueOf(issueAssetVolume));

        final StringBuilder sb = new StringBuilder();
        sb.append("{-# STDLIB_VERSION ").append(libVersion).append(" #-}\n")
                .append("{-# CONTENT_TYPE DAPP #-}\n")
                .append("{-# SCRIPT_TYPE ACCOUNT #-}\n")
                .append("@Callable(i)\n")
                .append("func setData(").append(args).append(")={\n");

        sb.append("let issueAsset = Issue(\"").append(issuedAssetName).append("\", \"")
                .append(issuedAssetDescription).append("\",")
                .append(issueAssetVolume).append(",")
                .append(issueAssetDecimals).append(",")
                .append(issueAssetReissuable).append(",")
                .append(script).append(",")
                .append(issueAssetNonce).append(")");

        sb.append("\nlet issueAssetId = issueAsset.calculateAssetId()\n");

        sb.append("[\n")
                .append("\tissueAsset,\n")
                .append("\t").append(functions).append("\n]\n")
                .append("}");
        return sb.toString();
    }

    public static String defaultFunctionBuilder(String funcArgs, String functions, long libVersion) {
        final StringBuilder sb = new StringBuilder();
        sb.append("{-# STDLIB_VERSION ").append(libVersion).append(" #-}\n")
                .append("{-# CONTENT_TYPE DAPP #-}\n")
                .append("{-# SCRIPT_TYPE ACCOUNT #-}\n")
                .append("@Callable(i)\n")
                .append("func setData(").append(funcArgs).append(")={\n");

        sb.append(functions)
                .append("}\n\n\n");
        return sb.toString();
    }

    public static String getIssuedAssetName() {
        return issuedAssetName;
    }

    public static String getIssuedAssetDescription() {
        return issuedAssetDescription;
    }

    public static int getIssueAssetVolume() {
        return issueAssetVolume;
    }

    public static int getIssueAssetDecimals() {
        return issueAssetDecimals;
    }

    public static boolean getIssueAssetReissuable() {
        return issueAssetReissuable;
    }

    public static int getIssueAssetNonce() {
        return issueAssetNonce;
    }

    public static Map<String, String> getIssueAssetData() {
        return issuedAssetData;
    }

}
