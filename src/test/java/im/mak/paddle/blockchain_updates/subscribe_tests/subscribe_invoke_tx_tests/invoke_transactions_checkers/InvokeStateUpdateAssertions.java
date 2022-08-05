package im.mak.paddle.blockchain_updates.subscribe_tests.subscribe_invoke_tx_tests.invoke_transactions_checkers;

import java.util.Map;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Assets.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.getAmountAfter;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Leasing.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Leasing.getRecipientFromIndividualLeases;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.Constants.DESCRIPTION;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class InvokeStateUpdateAssertions {
    public static void checkStateUpdateBalance
            (int balanceIndex, String address, String assetId, long amountBefore, long amountAfter) {
        if (assetId != null) {
            assertThat(getAssetIdAmountAfter(0, balanceIndex)).isEqualTo(assetId);
        }
        assertAll(
                () -> assertThat(getAddress(0, balanceIndex)).isEqualTo(address),
                () -> assertThat(getAmountBefore(0, balanceIndex)).isEqualTo(amountBefore),
                () -> assertThat(getAmountAfter(0, balanceIndex)).isEqualTo(amountAfter)
        );
    }

    public static void checkStateUpdateBeforeLeasingForAddress
            (int metadataIndex, int dataIndex, String address, long sumIn, long sumOut) {
        assertAll(
                () -> assertThat(getAddressFromLeasingForAddress(metadataIndex, dataIndex)).isEqualTo(address),
                () -> assertThat(getInBeforeFromLeasingForAddress(metadataIndex, dataIndex)).isEqualTo(sumIn),
                () -> assertThat(getOutBeforeFromLeasingForAddress(metadataIndex, dataIndex)).isEqualTo(sumOut)
        );
    }

    public static void checkStateUpdateAfterLeasingForAddress
            (int metadataIndex, int dataIndex, String address, long sumIn, long sumOut) {
        assertAll(
                () -> assertThat(getAddressFromLeasingForAddress(metadataIndex, dataIndex)).isEqualTo(address),
                () -> assertThat(getInAfterFromLeasingForAddress(metadataIndex, dataIndex)).isEqualTo(sumIn),
                () -> assertThat(getOutAfterFromLeasingForAddress(metadataIndex, dataIndex)).isEqualTo(sumOut)
        );
    }

    public static void checkStateUpdateIndividualLeases
            (int metadataIndex, int dataIndex, long sum, String senderPK, String recipientAddress, String status) {
        assertAll(
                () -> assertThat(getStatusAfterFromIndividualLeases(metadataIndex, dataIndex)).isEqualTo(status),
                () -> assertThat(getAmountFromIndividualLeases(metadataIndex, dataIndex)).isEqualTo(sum),
                () -> assertThat(getSenderFromIndividualLeases(metadataIndex, dataIndex)).isEqualTo(senderPK),
                () -> assertThat(getRecipientFromIndividualLeases(metadataIndex, dataIndex)).isEqualTo(recipientAddress)
        );
    }

    public static void checkStateUpdateAssets
            (int txStateUpdIndex, int assetIndex, Map<String, String> assetData, long quantityAfter) {
        String assetId = assetData.get(ASSET_ID);
        if (!getAssetIdFromAssetBefore(txStateUpdIndex, assetIndex).isBlank()) {
            if (assetId != null) {
                assertThat(getAssetIdFromAssetBefore(txStateUpdIndex, assetIndex)).isEqualTo(assetId);
            }
            assertAll(
                    () -> assertThat(getIssuerBefore(txStateUpdIndex, assetIndex)).isEqualTo(assetData.get(ISSUER)),
                    () -> assertThat(getDecimalsBefore(txStateUpdIndex, assetIndex)).isEqualTo(assetData.get(DECIMALS)),
                    () -> assertThat(getNameBefore(txStateUpdIndex, assetIndex)).isEqualTo(assetData.get(NAME)),
                    () -> assertThat(getDescriptionBefore(txStateUpdIndex, assetIndex)).isEqualTo(assetData.get(DESCRIPTION)),
                    () -> assertThat(getQuantityBefore(txStateUpdIndex, assetIndex)).isEqualTo(assetData.get(VOLUME))
            );
        }

        if (!getAssetIdFromAssetAfter(txStateUpdIndex, assetIndex).isBlank()) {
            if (assetId != null) {
                assertThat(getAssetIdFromAssetAfter(txStateUpdIndex, assetIndex)).isEqualTo(assetId);
            }
            assertAll(
                    () -> assertThat(getIssuerAfter(txStateUpdIndex, assetIndex)).isEqualTo(assetData.get(ISSUER)),
                    () -> assertThat(getDecimalsAfter(txStateUpdIndex, assetIndex)).isEqualTo(assetData.get(DECIMALS)),
                    () -> assertThat(getNameAfter(txStateUpdIndex, assetIndex)).isEqualTo(assetData.get(NAME)),
                    () -> assertThat(getDescriptionAfter(txStateUpdIndex, assetIndex)).isEqualTo(assetData.get(DESCRIPTION)),
                    () -> assertThat(getQuantityAfter(txStateUpdIndex, assetIndex)).isEqualTo(String.valueOf(quantityAfter))
            );
        }
    }
}
