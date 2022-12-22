package im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers;

import com.wavesplatform.crypto.base.Base58;

import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.waves_transactions_handlers.WavesTransactionsHandler.getWavesTransactionAtIndex;

public class SponsorFeeTransactionHandler {
    public static String getAssetIdFromSponsorFee(int txIndex) {
        return Base58.encode(getWavesTransactionAtIndex(txIndex)
                .getSponsorFee()
                .getMinFee()
                .getAssetId()
                .toByteArray());
    }

    public static long getAmountFromSponsorFee(int txIndex) {
        return getWavesTransactionAtIndex(txIndex)
                .getSponsorFee()
                .getMinFee()
                .getAmount();
    }
}
