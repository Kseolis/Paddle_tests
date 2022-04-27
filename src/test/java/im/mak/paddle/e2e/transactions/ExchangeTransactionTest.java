package im.mak.paddle.e2e.transactions;

import com.wavesplatform.transactions.ExchangeTransaction;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.exchange.Order;

import com.wavesplatform.wavesj.info.TransactionInfo;
import im.mak.paddle.Account;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.wavesplatform.wavesj.ApplicationStatus.SUCCEEDED;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class ExchangeTransactionTest {
    private static Account alice;
    private static Account bob;
    private static Account cat;

    private static AssetId testAssetId;
    private static AssetId firstSmartAssetId;
    private static AssetId secondSmartAssetId;

    private AssetId priceAsset;
    private AssetId amountAsset;

    private long buyerBalanceAfterTransactionPriceAsset;
    private long sellerBalanceAfterTransactionPriceAsset;
    private long buyerBalanceAfterTransactionAmountAsset;
    private long sellerBalanceAfterTransactionAmountAsset;

    @BeforeAll
    static void before() {
        async(
            () -> {
                alice = new Account(DEFAULT_FAUCET);
                testAssetId = alice.issue(i -> i.name("Test_Asset").quantity(1000L).decimals(8)).tx().assetId();
            },
            () -> {
                bob = new Account(DEFAULT_FAUCET);
                secondSmartAssetId = bob.issue(i -> i.name("S_Smart_Asset").script("{-# SCRIPT_TYPE ASSET #-} true")
                        .quantity(4000L).decimals(8)).tx().assetId();
            },
            () -> {
                cat = new Account(DEFAULT_FAUCET);
                firstSmartAssetId = cat.issue(i -> i.name("F_Smart_Asset").script("{-# SCRIPT_TYPE ASSET #-} true")
                        .quantity(4000L).decimals(8)).tx().assetId();
            }
        );
    }

    @Test
    @DisplayName("Buying maximum tokens for maximum price")
    void exchangeMaxAssets() {
        long sumSellerTokens = bob.getWavesBalance() - MIN_FEE_FOR_EXCHANGE;
        long offerForToken = getRandomInt(1, 50);

        Amount amountsTokensForExchange = Amount.of(sumSellerTokens, AssetId.WAVES);
        Amount pricePerToken = Amount.of(offerForToken, testAssetId);

        Order buyerOrder = Order.buy(amountsTokensForExchange, pricePerToken, alice.publicKey())
                .getSignedWith(alice.privateKey());
        Order sellOrder = Order.sell(amountsTokensForExchange, pricePerToken, alice.publicKey())
                .getSignedWith(bob.privateKey());

        exchangeTransaction(
            alice,
            bob,
            buyerOrder,
            sellOrder,
            amountsTokensForExchange.value(),
            pricePerToken.value(),
            MIN_FEE_FOR_EXCHANGE
        );
    }

    @Test
    @DisplayName("Buying minimum tokens, issued asset is smart")
    void exchangeOneSmartAsset() {
        long sumSellerTokens = cat.getBalance(firstSmartAssetId) * 100_000 / 4;

        Amount amountsTokensForExchange = Amount.of(50, testAssetId);
        Amount pricePerToken = Amount.of(sumSellerTokens, firstSmartAssetId);

        Order buyerOrder = Order.buy(amountsTokensForExchange, pricePerToken, cat.publicKey())
                .getSignedWith(cat.privateKey());
        Order sellOrder = Order.sell(amountsTokensForExchange, pricePerToken, cat.publicKey())
                .getSignedWith(alice.privateKey());

        exchangeTransaction(
                cat,
                alice,
                buyerOrder,
                sellOrder,
                amountsTokensForExchange.value(),
                pricePerToken.value(),
                FEE_FOR_EXCHANGE
        );
    }

    @Test
    @DisplayName("Buying minimum tokens, two smart assets")
    void exchangeTwoSmartAssets() {
        long sumBuyerTokens = cat.getBalance(firstSmartAssetId) * 100_000 / 4;
        Amount amountsTokensForExchange = Amount.of(MIN_TRANSFER_SUM, firstSmartAssetId);
        Amount pricePerToken = Amount.of(sumBuyerTokens, secondSmartAssetId);

        Order buyerOrder = Order.buy(amountsTokensForExchange, pricePerToken, bob.publicKey())
                .getSignedWith(bob.privateKey());
        Order sellOrder = Order.sell(amountsTokensForExchange, pricePerToken, bob.publicKey())
                .getSignedWith(cat.privateKey());

        exchangeTransaction(
                bob,
                cat,
                buyerOrder,
                sellOrder,
                amountsTokensForExchange.value(),
                pricePerToken.value(),
                MAX_FEE_FOR_EXCHANGE
        );
    }

    private void exchangeTransaction
            (Account from, Account to, Order buy, Order sell, long amount, long price, long fee) {
        calculateBalancesAfterTransaction(from, to, buy, amount);

        ExchangeTransaction tx = from.exchange(buy, sell, amount, price).tx();
        TransactionInfo txInfo = node().getTransactionInfo(tx.id());

        assertAll(
            () -> assertThat(tx.assetPair()).isEqualTo(buy.assetPair()),
            () -> assertThat(tx.sender()).isEqualTo(from.publicKey()),
            () -> assertThat(tx.orders()).isEqualTo(List.of(buy, sell)),
            () -> assertThat(tx.amount()).isEqualTo(amount),
            () -> assertThat(tx.price()).isEqualTo(buy.price().value()),
            () -> assertThat(tx.type()).isEqualTo(7),
            () -> assertThat(from.getBalance(priceAsset)).isEqualTo(buyerBalanceAfterTransactionPriceAsset),
            () -> assertThat(to.getBalance(priceAsset)).isEqualTo(sellerBalanceAfterTransactionPriceAsset),
        /*
            () -> assertThat(from.getBalance(amountAsset)).isEqualTo(buyerBalanceAfterTransactionAmountAsset),
            () -> assertThat(to.getBalance(amountAsset)).isEqualTo(sellerBalanceAfterTransactionAmountAsset),
        */

            () -> assertThat(txInfo.applicationStatus()).isEqualTo(SUCCEEDED),
            () -> assertThat((Object) txInfo.tx().fee().value()).isEqualTo(fee)
        );
    }

    private void calculateBalancesAfterTransaction(Account from, Account to, Order buy, long amount) {
        priceAsset = buy.assetPair().left();
        amountAsset = buy.assetPair().right();

        buyerBalanceAfterTransactionPriceAsset = from.getBalance(priceAsset) + amount;
        sellerBalanceAfterTransactionPriceAsset = to.getBalance(priceAsset) - amount;
        buyerBalanceAfterTransactionAmountAsset = from.getBalance(amountAsset) - buy.amount().value();
        sellerBalanceAfterTransactionAmountAsset = to.getBalance(amountAsset) + buy.amount().value();

        if(priceAsset.isWaves()) {

            double expression = to.getBalance(amountAsset) +
                    (double) (to.getBalance(priceAsset) - MIN_FEE_FOR_EXCHANGE)
                            / 100_000_000 * buy.price().value();

            long result = Math.round(expression % 2 == 0 ? expression : expression - 1);
            sellerBalanceAfterTransactionPriceAsset = to.getBalance(priceAsset) - amount - MIN_FEE_FOR_EXCHANGE;
            buyerBalanceAfterTransactionAmountAsset = from.getBalance(amountAsset) - result;
            sellerBalanceAfterTransactionAmountAsset = result;
        }
    }
}
