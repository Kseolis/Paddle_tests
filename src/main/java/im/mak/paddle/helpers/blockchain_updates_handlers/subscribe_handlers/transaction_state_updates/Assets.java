package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.events.protobuf.Events;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.TransactionStateUpdates.getTransactionStateUpdate;

public class Assets {
    public static String getAssetIdFromAssetBefore(int txStateUpdIndex, int assetIndex) {
        return Base58.encode(getAssetBefore(txStateUpdIndex, assetIndex).getAssetId().toByteArray());
    }

    public static String getAssetIdFromAssetAfter(int txStateUpdIndex, int assetIndex) {
        return Base58.encode(getAssetAfter(txStateUpdIndex, assetIndex).getAssetId().toByteArray());
    }

    public static String getIssuerBefore(int txStateUpdIndex, int assetIndex) {
        return Base58.encode(getAssetBefore(txStateUpdIndex, assetIndex).getIssuer().toByteArray());
    }

    public static String getIssuerAfter(int txStateUpdIndex, int assetIndex) {
        return Base58.encode(getAssetAfter(txStateUpdIndex, assetIndex).getIssuer().toByteArray());
    }

    public static String getDecimalsBefore(int txStateUpdIndex, int assetIndex) {
        return String.valueOf(getAssetBefore(txStateUpdIndex, assetIndex).getDecimals());
    }

    public static String getDecimalsAfter(int txStateUpdIndex, int assetIndex) {
        return String.valueOf(getAssetAfter(txStateUpdIndex, assetIndex).getDecimals());
    }

    public static String getNameBefore(int txStateUpdIndex, int assetIndex) {
        return getAssetBefore(txStateUpdIndex, assetIndex).getName();
    }

    public static String getNameAfter(int txStateUpdIndex, int assetIndex) {
        return getAssetAfter(txStateUpdIndex, assetIndex).getName();
    }

    public static String getDescriptionBefore(int txStateUpdIndex, int assetIndex) {
        return getAssetBefore(txStateUpdIndex, assetIndex).getDescription();
    }

    public static String getDescriptionAfter(int txStateUpdIndex, int assetIndex) {
        return getAssetAfter(txStateUpdIndex, assetIndex).getDescription();
    }

    public static String getReissueBefore(int txStateUpdIndex, int assetIndex) {
        return String.valueOf(getAssetBefore(txStateUpdIndex, assetIndex).getReissuable());
    }

    public static String getReissueAfter(int txStateUpdIndex, int assetIndex) {
        return String.valueOf(getAssetAfter(txStateUpdIndex, assetIndex).getReissuable());
    }

    public static String getQuantityBefore(int txStateUpdIndex, int assetIndex) {
        return String.valueOf(getAssetBefore(txStateUpdIndex, assetIndex).getVolume());
    }

    public static String getQuantityAfter(int txStateUpdIndex, int assetIndex) {
        return String.valueOf(getAssetAfter(txStateUpdIndex, assetIndex).getVolume());
    }

    public static byte[] getScriptBefore(int txStateUpdIndex, int assetIndex) {
        return getAssetBefore(txStateUpdIndex, assetIndex).getScriptInfo().getScript().toByteArray();
    }

    public static byte[] getScriptAfter(int txStateUpdIndex, int assetIndex) {
        return getAssetAfter(txStateUpdIndex, assetIndex).getScriptInfo().getScript().toByteArray();
    }

    public static long getScriptComplexityBefore(int txStateUpdIndex, int assetIndex) {
        return getAssetBefore(txStateUpdIndex, assetIndex).getScriptInfo().getComplexity();
    }

    public static long getScriptComplexityAfter(int txStateUpdIndex, int assetIndex) {
        return getAssetAfter(txStateUpdIndex, assetIndex).getScriptInfo().getComplexity();
    }

    public static String getAssetSafeVolumeBefore(int txStateUpdIndex, int assetIndex) {
        return Base58.encode(getAssetBefore(txStateUpdIndex, assetIndex).getSafeVolume().toByteArray());
    }

    public static String getAssetSafeVolumeAfter(int txStateUpdIndex, int assetIndex) {
        return Base58.encode(getAssetAfter(txStateUpdIndex, assetIndex).getSafeVolume().toByteArray());
    }

    public static long getAssetSponsorshipBefore(int txStateUpdIndex, int assetIndex) {
        return getAssetBefore(txStateUpdIndex, assetIndex).getSponsorship();
    }

    public static long getAssetSponsorshipAfter(int txStateUpdIndex, int assetIndex) {
        return getAssetAfter(txStateUpdIndex, assetIndex).getSponsorship();
    }

    private static Events.StateUpdate.AssetDetails getAssetBefore(int txStateUpdIndex, int assetIndex) {
        return getTransactionStateUpdate(txStateUpdIndex)
                .getAssets(assetIndex)
                .getBefore();
    }

    private static Events.StateUpdate.AssetDetails getAssetAfter(int txStateUpdIndex, int assetIndex) {
        return getTransactionStateUpdate(txStateUpdIndex)
                .getAssets(assetIndex)
                .getAfter();
    }
}
