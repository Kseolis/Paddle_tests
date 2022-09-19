package im.mak.paddle.blockchain_updates.transactions_checkers.invoke_transactions_checkers;

import java.util.Map;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Assets.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.DataEntries.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Leasing.*;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.Constants.DESCRIPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class InvokeStateUpdateAssertions {
    public static void checkStateUpdateBalance
            (int stateUpdIndex, int balanceIndex, String address, String assetId, long before, long after) {
        if (assetId != null) {
            assertThat(getAssetIdAmountAfter(stateUpdIndex, balanceIndex)).isEqualTo(assetId);
        }
        assertAll(
                () -> assertThat(getAddress(stateUpdIndex, balanceIndex)).isEqualTo(address),
                () -> assertThat(getAmountBefore(stateUpdIndex, balanceIndex)).isEqualTo(before),
                () -> assertThat(getAmountAfter(stateUpdIndex, balanceIndex)).isEqualTo(after)
        );
    }

    public static void checkStateUpdateIndividualLeases
            (int index, int dataIndex, long sum, String senderPK, String recipientAddress, String status) {
        assertAll(
                () -> assertThat(getStatusAfterFromIndividualLeases(index, dataIndex)).isEqualTo(status),
                () -> assertThat(getAmountFromIndividualLeases(index, dataIndex)).isEqualTo(sum),
                () -> assertThat(getSenderFromIndividualLeases(index, dataIndex)).isEqualTo(senderPK),
                () -> assertThat(getRecipientFromIndividualLeases(index, dataIndex)).isEqualTo(recipientAddress)
        );
    }

    public static void checkStateUpdateDataEntries(int index, int dataIndex, String address, String key, String val) {
        assertAll(
                () -> assertThat(getSenderAddress(index, dataIndex)).isEqualTo(address),
                () -> assertThat(getAfterKeyForStateUpdates(index, dataIndex)).isEqualTo(key),
                () -> assertThat(getBeforeDataEntriesKey(index, dataIndex)).isEqualTo(key)
        );

        switch (key) {
            case DATA_ENTRY_INT:
                assertThat(getAfterIntValueForStateUpdates(index, dataIndex)).isEqualTo(Integer.parseInt(val));
                break;
            case DATA_ENTRY_BOOL:
                assertThat(getAfterBoolValueForStateUpdates(index, dataIndex)).isEqualTo(Boolean.parseBoolean(val));
                break;
            case DATA_ENTRY_STR:
                assertThat(getAfterStringValueForStateUpdates(index, dataIndex)).isEqualTo(val);
                break;
            case DATA_ENTRY_BYTE:
                assertThat(getAfterByteValueForStateUpdates(index, dataIndex)).isEqualTo(val);
                break;
            default:
                break;
        }
    }

    public static void checkStateUpdateAssets
            (int txIndex, int assetIndex, Map<String, String> assetData, long quantityAfter) {
        String assetId = assetData.get(ASSET_ID);
        if (!getAssetIdFromAssetBefore(txIndex, assetIndex).isBlank()) {
            if (assetId != null) {
                assertThat(getAssetIdFromAssetBefore(txIndex, assetIndex)).isEqualTo(assetId);
            }
            assertAll(
                    () -> assertThat(getIssuerBefore(txIndex, assetIndex)).isEqualTo(assetData.get(ISSUER)),
                    () -> assertThat(getDecimalsBefore(txIndex, assetIndex)).isEqualTo(assetData.get(DECIMALS)),
                    () -> assertThat(getNameBefore(txIndex, assetIndex)).isEqualTo(assetData.get(NAME)),
                    () -> assertThat(getDescriptionBefore(txIndex, assetIndex)).isEqualTo(assetData.get(DESCRIPTION)),
                    () -> assertThat(getQuantityBefore(txIndex, assetIndex)).isEqualTo(assetData.get(VOLUME))
            );
        }

        if (!getAssetIdFromAssetAfter(txIndex, assetIndex).isBlank()) {
            if (assetId != null) {
                assertThat(getAssetIdFromAssetAfter(txIndex, assetIndex)).isEqualTo(assetId);
            }
            assertAll(
                    () -> assertThat(getIssuerAfter(txIndex, assetIndex)).isEqualTo(assetData.get(ISSUER)),
                    () -> assertThat(getDecimalsAfter(txIndex, assetIndex)).isEqualTo(assetData.get(DECIMALS)),
                    () -> assertThat(getNameAfter(txIndex, assetIndex)).isEqualTo(assetData.get(NAME)),
                    () -> assertThat(getDescriptionAfter(txIndex, assetIndex)).isEqualTo(assetData.get(DESCRIPTION)),
                    () -> assertThat(getQuantityAfter(txIndex, assetIndex)).isEqualTo(String.valueOf(quantityAfter))
            );
        }
    }

    public static void checkStateUpdateAssetsSponsorship(int txIndex, int assetIndex, long sponsorship) {
        assertThat(getAssetSponsorshipAfter(txIndex, assetIndex)).isEqualTo(sponsorship);
    }

    public static void checkStateUpdateBeforeLeasing(int index, int dataIndex, String address, long sumIn, long sumOut) {
        assertAll(
                () -> assertThat(getAddressFromLeasingForAddress(index, dataIndex)).isEqualTo(address),
                () -> assertThat(getInBeforeFromLeasingForAddress(index, dataIndex)).isEqualTo(sumIn),
                () -> assertThat(getOutBeforeFromLeasingForAddress(index, dataIndex)).isEqualTo(sumOut)
        );
    }

    public static void checkStateUpdateAfterLeasing(int index, int dataIndex, String address, long sumIn, long sumOut) {
        assertAll(
                () -> assertThat(getAddressFromLeasingForAddress(index, dataIndex)).isEqualTo(address),
                () -> assertThat(getInAfterFromLeasingForAddress(index, dataIndex)).isEqualTo(sumIn),
                () -> assertThat(getOutAfterFromLeasingForAddress(index, dataIndex)).isEqualTo(sumOut)
        );
    }
}
