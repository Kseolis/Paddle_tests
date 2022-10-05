package im.mak.paddle.blockchain_updates.transactions_checkers;

import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.exchange.Order;
import im.mak.paddle.Account;
import im.mak.paddle.helpers.transaction_senders.ExchangeTransactionSender;

import static com.wavesplatform.transactions.ExchangeTransaction.LATEST_VERSION;
import static im.mak.paddle.blockchain_updates.BaseGrpcTest.CHAIN_ID;
import static im.mak.paddle.helpers.Calculations.*;
import static im.mak.paddle.helpers.Calculations.getAmountAssetId;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.getAssetIdAmountAfter;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.ExchangeTransactionHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.getTransactionVersion;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class GrpcExchangeCheckers {
    private final int txIndex;

    private final String buyerPublicKey;
    private final String buyerAddress;
    private final long wavesBuyerAmountBefore;
    private final long wavesBuyerAmountAfter;

    private final String sellerPublicKey;
    private final String sellerAddress;
    private final long wavesSellerAmountBefore;
    private final long wavesSellerAmountAfter;

    private final String exchangeTxId;
    private final String assetId;
    private final Order buy;
    private final Order sell;

    private final Amount amount;
    private final Amount price;

    public GrpcExchangeCheckers(int txIndex, Account buyer, Account seller, ExchangeTransactionSender txSender) {
        this.txIndex = txIndex;

        exchangeTxId = txSender.getExchangeTx().id().toString();
        assetId = txSender.getExchangeTx().assetPair().right().toString();

        buy = txSender.getBuy();
        sell = txSender.getSell();
        amount = buy.amount();
        price = buy.price();

        buyerPublicKey = buyer.publicKey().toString();
        buyerAddress = buyer.address().toString();
        wavesBuyerAmountBefore = txSender.getWavesBuyerAmountBefore();
        wavesBuyerAmountAfter = wavesBuyerAmountBefore - txSender.getExchangeExtraFee();

        sellerPublicKey = seller.publicKey().toString();
        sellerAddress = seller.address().toString();
        wavesSellerAmountBefore = txSender.getWavesSellerAmountBefore();
        wavesSellerAmountAfter = wavesSellerAmountBefore - MIN_FEE_FOR_EXCHANGE;
    }


    public void checkExchangeSubscribe(long fee, String amountAssetId) {
        assertAll(
                () -> assertThat(getChainId(txIndex)).isEqualTo(CHAIN_ID),
                () -> assertThat(getSenderPublicKeyFromTransaction(txIndex)).isEqualTo(buyerPublicKey),
                () -> assertThat(getTransactionFeeAmount(txIndex)).isEqualTo(fee),
                () -> assertThat(getTransactionVersion(txIndex)).isEqualTo(LATEST_VERSION),
                () -> assertThat(getTxId(txIndex)).isEqualTo(exchangeTxId),
                () -> assertThat(getAmountFromExchange(txIndex)).isEqualTo(amount.value()),
                () -> assertThat(getPriceFromExchange(txIndex)).isEqualTo(price.value()),
                () -> assertThat(getBuyMatcherFeeFromExchange(txIndex)).isEqualTo(MIN_FEE_FOR_EXCHANGE),
                () -> assertThat(getSellMatcherFeeFromExchange(txIndex)).isEqualTo(MIN_FEE_FOR_EXCHANGE),
                // buy order
                () -> assertThat(getSenderPublicKeyFromExchange(txIndex, 0)).isEqualTo(buyerPublicKey),
                () -> assertThat(getMatcherPublicKeyFromExchange(txIndex, 0)).isEqualTo(buyerPublicKey),
                () -> assertThat(getAmountAssetIdFromExchange(txIndex, 0)).isEqualTo(amountAssetId),
                () -> assertThat(getOrderAmountFromExchange(txIndex, 0)).isEqualTo(amount.value()),
                () -> assertThat(getOrderPriceFromExchange(txIndex, 0)).isEqualTo(price.value()),
                () -> assertThat(getMatcherFeeFromExchange(txIndex, 0)).isEqualTo(MIN_FEE_FOR_EXCHANGE),
                () -> assertThat(getOrderSideFromExchange(txIndex, 0)).isEqualTo(BUY),
                () -> assertThat(getOrderVersionFromExchange(txIndex, 0)).isEqualTo(buy.version()),
                // sell order
                () -> assertThat(getSenderPublicKeyFromExchange(txIndex, 1)).isEqualTo(sellerPublicKey),
                () -> assertThat(getMatcherPublicKeyFromExchange(txIndex, 1)).isEqualTo(buyerPublicKey),
                () -> assertThat(getAmountAssetIdFromExchange(txIndex, 1)).isEqualTo(amountAssetId),
                () -> assertThat(getOrderAmountFromExchange(txIndex, 1)).isEqualTo(amount.value()),
                () -> assertThat(getOrderPriceFromExchange(txIndex, 1)).isEqualTo(price.value()),
                () -> assertThat(getMatcherFeeFromExchange(txIndex, 1)).isEqualTo(MIN_FEE_FOR_EXCHANGE),
                () -> assertThat(getOrderSideFromExchange(txIndex, 1)).isEqualTo(SELL),
                () -> assertThat(getOrderVersionFromExchange(txIndex, 1)).isEqualTo(sell.version())
        );
    }

    public void checkBalancesForExchangeWithWaves(long amountBefore, long assetQuantity) {
        // check waves balance from balances buyer
        assertAll(
                () -> assertThat(getAddress(txIndex, 0)).isEqualTo(buyerAddress),
                () -> assertThat(getAmountBefore(txIndex, 0)).isEqualTo(amountBefore),
                () -> assertThat(getAmountAfter(txIndex, 0)).isEqualTo(getBuyerBalanceAfterTransactionAmountAsset()),
                // check asset balance from balances buyer
                () -> assertThat(getAddress(txIndex, 1)).isEqualTo(buyerAddress),
                () -> assertThat(getAmountBefore(txIndex, 1)).isEqualTo(assetQuantity),
                () -> assertThat(getAmountAfter(txIndex, 1)).isEqualTo(getBuyerBalanceAfterTransactionPriceAsset()),
                () -> assertThat(getAssetIdAmountAfter(txIndex, 1)).isEqualTo(assetId),
                // check waves balance from balances seller
                () -> assertThat(getAddress(txIndex, 2)).isEqualTo(sellerAddress),
                () -> assertThat(getAmountBefore(txIndex, 2)).isEqualTo(DEFAULT_FAUCET),
                () -> assertThat(getAmountAfter(txIndex, 2)).isEqualTo(getSellerBalanceAfterTransactionAmountAsset()),
                // check asset balance from balances seller
                () -> assertThat(getAddress(txIndex, 3)).isEqualTo(sellerAddress),
                () -> assertThat(getAmountBefore(txIndex, 3)).isEqualTo(0),
                () -> assertThat(getAmountAfter(txIndex, 3)).isEqualTo(getSellerBalanceAfterTransactionPriceAsset()),
                () -> assertThat(getAssetIdAmountAfter(txIndex, 3)).isEqualTo(assetId)
        );
    }

    public void checkBalancesForExchangeWithAssets(long assetQuantity) {
        // check waves balance from balances buyer
        assertThat(getAddress(txIndex, 0)).isEqualTo(buyerAddress);
        assertThat(getAmountBefore(txIndex, 0)).isEqualTo(wavesBuyerAmountBefore);
        assertThat(getAmountAfter(txIndex, 0)).isEqualTo(wavesBuyerAmountAfter);
        // check asset balance from balances buyer
        assertThat(getAddress(txIndex, 1)).isEqualTo(buyerAddress);
        assertThat(getAmountBefore(txIndex, 1)).isEqualTo(assetQuantity);
        assertThat(getAmountAfter(txIndex, 1)).isEqualTo(getBuyerBalanceAfterTransactionPriceAsset());
        assertThat(getAssetIdAmountAfter(txIndex, 1)).isEqualTo(getPriceAssetId().toString());
        // check asset balance from balances
        assertThat(getAddress(txIndex, 2)).isEqualTo(buyerAddress);
        assertThat(getAmountBefore(txIndex, 2)).isEqualTo(0);
        assertThat(getAmountAfter(txIndex, 2)).isEqualTo(getBuyerBalanceAfterTransactionAmountAsset());
        assertThat(getAssetIdAmountAfter(txIndex, 2)).isEqualTo(getAmountAssetId().toString());
        // check asset balance from balances seller
        assertThat(getAddress(txIndex, 3)).isEqualTo(sellerAddress);
        assertThat(getAmountBefore(txIndex, 3)).isEqualTo(wavesSellerAmountBefore);
        assertThat(getAmountAfter(txIndex, 3)).isEqualTo(wavesSellerAmountAfter);
        // check asset balance from balances buyer
        assertThat(getAddress(txIndex, 4)).isEqualTo(sellerAddress);
        assertThat(getAmountBefore(txIndex, 4)).isEqualTo(0);
        assertThat(getAmountAfter(txIndex, 4)).isEqualTo(getSellerBalanceAfterTransactionPriceAsset());
        assertThat(getAssetIdAmountAfter(txIndex, 4)).isEqualTo(getPriceAssetId().toString());
        // check asset balance from balances
        assertThat(getAddress(txIndex, 5)).isEqualTo(sellerAddress);
        assertThat(getAmountBefore(txIndex, 5)).isEqualTo(assetQuantity);
        assertThat(getAmountAfter(txIndex, 5)).isEqualTo(getSellerBalanceAfterTransactionAmountAsset());
        assertThat(getAssetIdAmountAfter(txIndex, 5)).isEqualTo(getAmountAssetId().toString());
    }
}
