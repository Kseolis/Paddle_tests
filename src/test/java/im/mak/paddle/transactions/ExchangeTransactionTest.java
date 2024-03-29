package im.mak.paddle.transactions;

import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.exchange.Order;

import im.mak.paddle.Account;
import im.mak.paddle.helpers.transaction_senders.ExchangeTransactionSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.wavesplatform.transactions.ExchangeTransaction.LATEST_VERSION;
import static com.wavesplatform.wavesj.ApplicationStatus.SUCCEEDED;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Calculations.*;
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
    private long fee;

    @BeforeAll
    static void before() {
        async(
                () -> {
                    alice = new Account(DEFAULT_FAUCET);
                    testAssetId = alice.issue(i -> i.name("Test_Asset")
                            .quantity(4000L).decimals(DEFAULT_DECIMALS)).tx().assetId();
                },
                () -> {
                    cat = new Account(DEFAULT_FAUCET);
                    firstSmartAssetId = cat.issue(i -> i.name("F_Smart_Asset").script(SCRIPT_PERMITTING_OPERATIONS)
                            .quantity(4000L).decimals(DEFAULT_DECIMALS)).tx().assetId();
                },
                () -> {
                    bob = new Account(DEFAULT_FAUCET);
                    secondSmartAssetId = bob.issue(i -> i.name("S_Smart_Asset").script(SCRIPT_PERMITTING_OPERATIONS)
                            .quantity(4000L).decimals(DEFAULT_DECIMALS)).tx().assetId();
                }
        );
    }

    @Test
    @DisplayName("Exchange maximum tokens for maximum price, orders and exchange equals versions")
    void exchangeMaxAssets() {
        fee = MIN_FEE_FOR_EXCHANGE;
        long sumSellerTokens = bob.getWavesBalance() - MIN_FEE_FOR_EXCHANGE;
        long offerForToken = getRandomInt(1, 50);

        for (int v = 1; v <= LATEST_VERSION; v++) {
            Amount amountsTokensForExchange = Amount.of(sumSellerTokens, AssetId.WAVES);
            long amountValue = amountsTokensForExchange.value();
            Amount pricePerToken = Amount.of(offerForToken, testAssetId);
            long priceValue = pricePerToken.value();

            Order buyerOrder = Order.buy(amountsTokensForExchange, pricePerToken, alice.publicKey()).version(v)
                    .getSignedWith(alice.privateKey());
            Order sellOrder = Order.sell(amountsTokensForExchange, pricePerToken, alice.publicKey()).version(v)
                    .getSignedWith(bob.privateKey());

            ExchangeTransactionSender txSender = new ExchangeTransactionSender(alice, bob, buyerOrder, sellOrder);

            txSender.exchangeTransactionSender(amountValue, priceValue, 0, v);

            checkAssertsForExchangeTransaction(amountValue, txSender);

            node().faucet().transfer(bob, DEFAULT_FAUCET, AssetId.WAVES);
        }
    }

    @Test
    @DisplayName("Exchange minimum tokens for maximum price Orders v3, exchange v2")
    void exchangeMinAssets() {
        fee = MIN_FEE_FOR_EXCHANGE;
        int exchangeVersion = 2;
        long sumSellerTokens = 1;
        long offerForToken = getRandomInt(1, 50);

        Amount amountsTokensForExchange = Amount.of(sumSellerTokens, AssetId.WAVES);
        long amountValue = amountsTokensForExchange.value();
        Amount pricePerToken = Amount.of(offerForToken, testAssetId);
        long priceValue = pricePerToken.value();

        Order buyerOrder = Order.buy(amountsTokensForExchange, pricePerToken, alice.publicKey()).version(ORDER_V_3)
                .getSignedWith(alice.privateKey());
        Order sellOrder = Order.sell(amountsTokensForExchange, pricePerToken, alice.publicKey()).version(ORDER_V_3)
                .getSignedWith(bob.privateKey());

        ExchangeTransactionSender txSender = new ExchangeTransactionSender(alice, bob, buyerOrder, sellOrder);

        txSender.exchangeTransactionSender(amountValue, priceValue, 0, exchangeVersion);

        checkAssertsForExchangeTransaction(amountValue, txSender);
    }

    @Test
    @DisplayName("Exchange minimum tokens, issued asset is smart, latest exchange version, order v4/v3")
    void exchangeOneSmartAsset() {
        fee = MIN_FEE_FOR_EXCHANGE + EXTRA_FEE;
        long sumSellerTokens = getRandomInt(1, 50) * (long) Math.pow(10, 8);

        Amount amountsTokensForExchange = Amount.of(50, testAssetId);
        long amountValue = amountsTokensForExchange.value();
        Amount pricePerToken = Amount.of(sumSellerTokens, firstSmartAssetId);
        long priceValue = pricePerToken.value();

        Order buyerOrder = Order.buy(amountsTokensForExchange, pricePerToken, cat.publicKey()).version(ORDER_V_4)
                .getSignedWith(cat.privateKey());
        Order sellOrder = Order.sell(amountsTokensForExchange, pricePerToken, cat.publicKey()).version(ORDER_V_3)
                .getSignedWith(alice.privateKey());


        ExchangeTransactionSender txSender = new ExchangeTransactionSender(cat, alice, buyerOrder, sellOrder);

        txSender.exchangeTransactionSender(amountValue, priceValue, EXTRA_FEE, LATEST_VERSION);

        checkAssertsForExchangeTransaction(amountValue, txSender);
    }

    @Test
    @DisplayName("Exchange transaction two smart assets, latest exchange version, order v3/v4")
    void exchangeTwoSmartAssets() {
        fee = MIN_FEE_FOR_EXCHANGE + EXCHANGE_FEE_FOR_SMART_ASSETS;

        long sumBuyerTokens = getRandomInt(1, 50) * (long) Math.pow(10, 8);

        Amount amountsTokensForExchange = Amount.of(MIN_TRANSACTION_SUM, firstSmartAssetId);
        long amountValue = amountsTokensForExchange.value();

        Amount pricePerToken = Amount.of(sumBuyerTokens, secondSmartAssetId);
        long priceValue = pricePerToken.value();

        Order buyerOrder = Order.buy(amountsTokensForExchange, pricePerToken, bob.publicKey()).version(ORDER_V_3)
                .getSignedWith(bob.privateKey());
        Order sellOrder = Order.sell(amountsTokensForExchange, pricePerToken, bob.publicKey()).version(ORDER_V_4)
                .getSignedWith(cat.privateKey());

        ExchangeTransactionSender txSender = new ExchangeTransactionSender(bob, cat, buyerOrder, sellOrder);

        txSender.exchangeTransactionSender(amountValue, priceValue, EXCHANGE_FEE_FOR_SMART_ASSETS, LATEST_VERSION);

        checkAssertsForExchangeTransaction(amountValue, txSender);
    }

    private void checkAssertsForExchangeTransaction(long amount, ExchangeTransactionSender txSender) {
        assertAll(
                () -> assertThat(txSender.getTxInfo().applicationStatus()).isEqualTo(SUCCEEDED),
                () -> assertThat(txSender.getExchangeTx().fee().value()).isEqualTo(fee),
                () -> assertThat(txSender.getExchangeTx().fee().assetId()).isEqualTo(AssetId.WAVES),
                () -> assertThat(txSender.getExchangeTx().assetPair()).isEqualTo(txSender.getBuy().assetPair()),
                () -> assertThat(txSender.getExchangeTx().sender()).isEqualTo(txSender.getFrom().publicKey()),
                () -> assertThat(txSender.getExchangeTx().orders()).isEqualTo(List.of(txSender.getBuy(), txSender.getSell())),
                () -> assertThat(txSender.getExchangeTx().amount()).isEqualTo(amount),
                () -> assertThat(txSender.getExchangeTx().price()).isEqualTo(txSender.getBuy().price().value()),
                () -> assertThat(txSender.getExchangeTx().type()).isEqualTo(7),
                () -> assertThat(txSender.getFrom().getBalance(getAmountAssetId()))
                        .isEqualTo(getBuyerBalanceAfterTransactionAmountAsset()),
                () -> assertThat(txSender.getTo().getBalance(getAmountAssetId()))
                        .isEqualTo(getSellerBalanceAfterTransactionAmountAsset()),
                () -> assertThat(txSender.getFrom().getBalance(getPriceAssetId()))
                        .isEqualTo(getBuyerBalanceAfterTransactionPriceAsset()),
                () -> assertThat(txSender.getTo().getBalance(getPriceAssetId()))
                        .isEqualTo(getSellerBalanceAfterTransactionPriceAsset())
        );
    }
}
