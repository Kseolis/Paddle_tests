package im.mak.paddle.blockchain_updates.subscribe_tests.tests_others_subscription_transactions;

import com.wavesplatform.transactions.IssueTransaction;
import com.wavesplatform.transactions.account.PrivateKey;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.exchange.Order;

import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.blockchain_updates.transactions_checkers.GrpcExchangeCheckers;
import im.mak.paddle.helpers.transaction_senders.ExchangeTransactionSender;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.ExchangeTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.Constants.ORDER_V_4;

public class ExchangeTransactionSubscriptionGrpcTest extends BaseGrpcTest {
    private Account buyer;
    private PrivateKey buyerPrivateKey;

    private Account seller;
    private PrivateKey sellerPrivateKey;

    private AssetId assetId;

    private int assetQuantity;
    private long fee;
    private String assetName;
    private String assetDescription;
    private IssueTransaction issueTx;
    private Amount amount;
    private Amount price;
    private Order buy;
    private Order sell;
    private final int assetDecimals = 8;

    @BeforeEach
    void setUp() {
        async(
                () -> {
                    buyer = new Account(DEFAULT_FAUCET);
                    buyerPrivateKey = buyer.privateKey();

                    assetName = getRandomInt(1, 900000) + "asset";
                    assetQuantity = getRandomInt(100000, 999_999_999);
                    assetDescription = assetName + "test";
                    issueTx = buyer.issue(i -> i.name(assetName)
                            .quantity(assetQuantity)
                            .description(assetDescription)
                            .decimals(assetDecimals)
                            .reissuable(true)).tx();
                    assetId = issueTx.assetId();
                },
                () -> {
                    seller = new Account(DEFAULT_FAUCET);
                    sellerPrivateKey = seller.privateKey();
                }
        );
    }

    @Test
    @DisplayName("Check subscription on Exchange transaction")
    void subscribeTestForExchangeTransaction() {
        long sumSellerTokens = seller.getWavesBalance() - MIN_FEE_FOR_EXCHANGE;
        long offerForToken = getRandomInt(1000, 50000);
        long amountBefore = seller.getWavesBalance() - ONE_WAVES;

        amount = Amount.of(sumSellerTokens, AssetId.WAVES);
        price = Amount.of(offerForToken, assetId);
        buy = Order.buy(amount, price, buyer.publicKey()).version(ORDER_V_3).getSignedWith(buyerPrivateKey);
        sell = Order.sell(amount, price, buyer.publicKey()).version(ORDER_V_4).getSignedWith(sellerPrivateKey);

        ExchangeTransactionSender txSender = new ExchangeTransactionSender(buyer, seller, buy, sell);
        txSender.exchangeTransactionSender(amount.value(), price.value(), 0, LATEST_VERSION);
        String txId = txSender.getTxInfo().tx().id().toString();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);

        GrpcExchangeCheckers grpcExchangeCheckers = new GrpcExchangeCheckers(0, buyer, seller, txSender);

        grpcExchangeCheckers.checkExchangeSubscribe(MIN_FEE_FOR_EXCHANGE, "");
        grpcExchangeCheckers.checkBalancesForExchangeWithWaves(amountBefore, assetQuantity);
    }

    @Test
    @DisplayName("Check subscription on Exchange transaction for two smart assets")
    void subscribeTestForExchangeTwoSmartAssetsTransaction() {
        fee = MIN_FEE_FOR_EXCHANGE + EXCHANGE_FEE_FOR_SMART_ASSETS;
        long sumBuyerTokens = getRandomInt(1, 500) * (long) Math.pow(10, 8);

        AssetId firstSmartAssetId = seller.issue(i -> i.name(assetName).script(SCRIPT_PERMITTING_OPERATIONS)
                .quantity(assetQuantity).decimals(DEFAULT_DECIMALS)).tx().assetId();
        AssetId secondSmartAssetId = buyer.issue(i -> i.name("S_Smart_Asset").script(SCRIPT_PERMITTING_OPERATIONS)
                .quantity(assetQuantity).decimals(DEFAULT_DECIMALS)).tx().assetId();

        amount = Amount.of(MIN_TRANSACTION_SUM, firstSmartAssetId);
        price = Amount.of(sumBuyerTokens, secondSmartAssetId);
        buy = Order.buy(amount, price, buyer.publicKey()).version(ORDER_V_4).getSignedWith(buyerPrivateKey);
        sell = Order.sell(amount, price, buyer.publicKey()).version(ORDER_V_4).getSignedWith(sellerPrivateKey);

        ExchangeTransactionSender txSender = new ExchangeTransactionSender(buyer, seller, buy, sell);
        txSender.exchangeTransactionSender(amount.value(), price.value(), EXCHANGE_FEE_FOR_SMART_ASSETS, LATEST_VERSION);
        String txId = txSender.getTxInfo().tx().id().toString();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);

        GrpcExchangeCheckers grpcExchangeCheckers = new GrpcExchangeCheckers(0, buyer, seller, txSender);

        grpcExchangeCheckers.checkExchangeSubscribe(fee, amount.assetId().toString());
        grpcExchangeCheckers.checkBalancesForExchangeWithAssets(assetQuantity);
    }

    @Test
    @DisplayName("Check subscription on Exchange transaction for one smart asset")
    void subscribeTestForExchangeOneSmartAssetTransaction() {
        fee = MIN_FEE_FOR_EXCHANGE + EXTRA_FEE;
        long sumBuyerTokens = getRandomInt(1, 500) * (long) Math.pow(10, 8);

        AssetId smartAssetId = seller.issue(i -> i.name("v_Asset").script(SCRIPT_PERMITTING_OPERATIONS)
                .quantity(assetQuantity).decimals(DEFAULT_DECIMALS)).tx().assetId();

        amount = Amount.of(MIN_TRANSACTION_SUM, smartAssetId);
        price = Amount.of(sumBuyerTokens, assetId);
        buy = Order.buy(amount, price, buyer.publicKey()).version(ORDER_V_4).getSignedWith(buyerPrivateKey);
        sell = Order.sell(amount, price, buyer.publicKey()).version(ORDER_V_4).getSignedWith(sellerPrivateKey);

        ExchangeTransactionSender txSender = new ExchangeTransactionSender(buyer, seller, buy, sell);
        txSender.exchangeTransactionSender(amount.value(), price.value(), EXTRA_FEE, LATEST_VERSION);
        String txId = txSender.getTxInfo().tx().id().toString();
        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);

        GrpcExchangeCheckers grpcExchangeCheckers = new GrpcExchangeCheckers(0, buyer, seller, txSender);

        grpcExchangeCheckers.checkExchangeSubscribe(fee, amount.assetId().toString());
        grpcExchangeCheckers.checkBalancesForExchangeWithAssets(assetQuantity);
    }
}
