package im.mak.paddle.helpers.transaction_senders;

import com.wavesplatform.transactions.ExchangeTransaction;
import com.wavesplatform.transactions.exchange.Order;
import im.mak.paddle.Account;

import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Calculations.calculateBalancesAfterExchange;
import static im.mak.paddle.util.Constants.DEFAULT_DECIMALS;
import static im.mak.paddle.util.Constants.MIN_FEE_FOR_EXCHANGE;

public class ExchangeTransactionSender extends BaseTransactionSender {
    private static ExchangeTransaction exchangeTx;
    private final Account from;
    private final Account to;
    private final Order buy;
    private final Order sell;

    public ExchangeTransactionSender(Account from, Account to, Order buy, Order sell) {
        this.from = from;
        this.to = to;
        this.buy = buy;
        this.sell = sell;
    }


    public void exchangeTransactionSender(long amount, long price, long extraFee, int version) {
        calculateBalancesAfterExchange(from, to, buy, amount, DEFAULT_DECIMALS);

        exchangeTx = ExchangeTransaction
                .builder(buy, sell, amount, price, MIN_FEE_FOR_EXCHANGE, MIN_FEE_FOR_EXCHANGE)
                .extraFee(extraFee)
                .version(version)
                .getSignedWith(from.privateKey());

        node().waitForTransaction(node().broadcast(exchangeTx).id());

        txInfo = node().getTransactionInfo(exchangeTx.id());
    }

    public static ExchangeTransaction getExchangeTx() {
        return exchangeTx;
    }

    public Account getFrom() {
        return from;
    }

    public Account getTo() {
        return to;
    }

    public Order getBuy() {
        return buy;
    }

    public Order getSell() {
        return sell;
    }
}
