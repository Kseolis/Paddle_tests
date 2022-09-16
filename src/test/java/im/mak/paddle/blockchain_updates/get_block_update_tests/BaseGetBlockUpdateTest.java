package im.mak.paddle.blockchain_updates.get_block_update_tests;

import com.wavesplatform.transactions.*;
import com.wavesplatform.transactions.account.PrivateKey;
import com.wavesplatform.transactions.account.PublicKey;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.common.Base64String;
import com.wavesplatform.transactions.common.Id;
import com.wavesplatform.transactions.data.*;
import com.wavesplatform.transactions.exchange.Order;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.dapp.DAppCall;
import im.mak.paddle.helpers.PrepareInvokeTestsData;
import im.mak.paddle.helpers.transaction_senders.*;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeCalculationsBalancesAfterTx;
import im.mak.paddle.helpers.transaction_senders.invoke.InvokeScriptTransactionSender;
import org.junit.jupiter.api.BeforeAll;

import java.util.ArrayList;
import java.util.List;

import static com.wavesplatform.transactions.TransferTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Randomizer.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.transaction_senders.BaseTransactionSender.setVersion;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.ScriptUtil.fromFile;

public class BaseGetBlockUpdateTest extends BaseGrpcTest {
    protected static Account sender;
    protected static PrivateKey senderPrivateKey;
    protected static PublicKey senderPublicKey;

    protected static Account recipient;
    protected static PrivateKey recipientPrivateKey;
    protected static PublicKey recipientPublicKey;

    protected static Amount wavesAmount;

    protected static Amount orderAmount;
    protected static Amount orderPrice;
    protected static Order orderBuy;
    protected static Order orderSell;

    protected static String newAlias;

    protected static String assetName;
    protected static String assetDescription;
    protected static int assetDecimals;

    protected static IssueTransaction issueTx;
    protected static AssetId assetId;
    protected static Amount assetAmount;

    protected static TransferTransactionSender transferTx;
    protected static ReissueTransactionSender reissueTx;
    protected static BurnTransactionSender burnTx;

    protected static CreateAliasTransactionSender aliasTx;
    protected static Id aliasTxId;
    protected static SetAssetScriptTransaction setAssetScriptTx;

    protected static LeaseTransactionSender leaseTx;
    protected static Id leaseTxId;
    protected static LeaseCancelTransactionSender leaseCancelTx;
    protected static ExchangeTransactionSender exchangeTx;

    protected static MassTransferTransactionSender massTransferTx;
    protected static Id massTransferTxId;
    protected static List<Account> accountList;

    protected static DataTransactionsSender dataTx;
    protected static Id dataTransferTxId;

    protected static SetScriptTransactionSender setScriptTx;
    protected static Id setScriptTxId;

    protected static PrepareInvokeTestsData testData;
    protected static InvokeCalculationsBalancesAfterTx calcBalances;
    protected static InvokeScriptTransactionSender invokeTx;
    protected static Id invokeTxId;

    protected static AssetId assetIdForSponsorFee;
    protected static SponsorFeeTransactionSender sponsorFeeTx;
    protected static Id sponsorFeeTxId;

    protected static int height;
    protected static List<Integer> heightsList = new ArrayList<>();

    protected static int indexCountTransactions = -1;
    protected static List<Integer> transactionsIndexList = new ArrayList<>();

    private static final Base64String base64String = new Base64String(randomNumAndLetterString(6));
    private static final BinaryEntry binaryEntry = BinaryEntry.as("BinEntry", base64String);
    private static final BooleanEntry booleanEntry = BooleanEntry.as("Boolean", true);
    private static final IntegerEntry integerEntry = IntegerEntry.as("Integer", getRandomInt(100, 100000));
    private static final StringEntry stringEntry = StringEntry.as("String", "string");
    private static final String scriptFromFile = fromFile("ride_scripts/defaultAssetExpression.ride");
    protected static final Base64String script = node().compileScript(scriptFromFile).script();

    @BeforeAll
    static void setUp() {
        mainSetUp();
        // Issue transaction
        issueSetUp();
        // Transfer transaction
        transferSetUp();
        // Burn transaction
        burnSetUp();
        // Reissue transaction
        reissueSetUp();
        // Exchange transaction
        exchangeSetUp();
        // Lease transaction
        leaseSetUp();
        // LeaseCancel transaction
        leaseCancelSetUp();
        // Create Alias transaction
        aliasSetUp();
        // MassTransfer transaction
        massTransferSetUp();
        // Data transaction
        dataSetUp();
        // SetScript transaction
        setScriptSetUp();
        // SponsorFee transaction
        sponsorFeeSetUp();
        // SetAssetScript transaction
        setAssetScriptSetUp();
        // Invoke transaction
    //     invokeSetUp();
    }

    private static void mainSetUp() {
        async(
                () -> {
                    sender = new Account(DEFAULT_FAUCET * 2);
                    indexCountTransactions += 1;

                    senderPrivateKey = sender.privateKey();
                    senderPublicKey = sender.publicKey();

                    assetIdForSponsorFee = sender.issue(i -> i.name("sponsorFeeAsset")).tx().assetId();
                    indexCountTransactions += 1;
                },
                () -> {
                    recipient = new Account(DEFAULT_FAUCET);
                    indexCountTransactions += 1;

                    recipientPrivateKey = recipient.privateKey();
                    recipientPublicKey = recipient.publicKey();
                },
                () -> newAlias = randomNumAndLetterString(15),
                () -> {
                    assetName = getRandomInt(1, 900000) + "asset";
                    assetDescription = assetName + "test";
                    assetDecimals = getRandomInt(0, 8);
                },
                () -> wavesAmount = Amount.of(10)
        );
        height = node().getHeight();
        heightsList.add(height);
    }

    private static void issueSetUp() {
        issueTx = sender.issue(i -> i
                .name(assetName)
                .quantity(ASSET_QUANTITY_MAX)
                .description(assetDescription)
                .decimals(assetDecimals)
                .reissuable(true)
                .script(SCRIPT_PERMITTING_OPERATIONS)
        ).tx();
        assetId = issueTx.assetId();
        assetAmount = Amount.of(50_000, assetId);
        checkHeight();
    }

    private static void transferSetUp() {
        transferTx = new TransferTransactionSender(assetAmount, sender, recipient, SUM_FEE);
        transferTx.transferTransactionSender(ADDRESS, LATEST_VERSION);
        checkHeight();
    }

    private static void reissueSetUp() {
        reissueTx = new ReissueTransactionSender(sender, assetAmount, assetId);
        reissueTx.reissueTransactionSender(SUM_FEE, ReissueTransaction.LATEST_VERSION);
        checkHeight();
    }

    private static void burnSetUp() {
        burnTx = new BurnTransactionSender(
                sender,
                assetAmount,
                assetId,
                SUM_FEE,
                BurnTransaction.LATEST_VERSION
        );
        burnTx.burnTransactionSender();
        checkHeight();
    }

    private static void leaseSetUp() {
        leaseTx = new LeaseTransactionSender(sender, recipient);
        leaseTx.leaseTransactionSender(MIN_TRANSACTION_SUM, MIN_FEE, LeaseTransaction.LATEST_VERSION);
        leaseTxId = leaseTx.getTxInfo().tx().id();
        checkHeight();
    }

    private static void leaseCancelSetUp() {
        leaseCancelTx = new LeaseCancelTransactionSender(sender, recipient);
        leaseCancelTx.leaseCancelTransactionSender(
                leaseTxId,
                MIN_TRANSACTION_SUM,
                MIN_FEE,
                LeaseCancelTransaction.LATEST_VERSION
        );
        checkHeight();
    }

    private static void aliasSetUp() {
        aliasTx = new CreateAliasTransactionSender(
                sender,
                newAlias,
                123456789,
                CreateAliasTransaction.LATEST_VERSION
        );
        aliasTx.createAliasTransactionSender();
        aliasTxId = aliasTx.getCreateAliasTx().id();
        checkHeight();
    }

    private static void massTransferSetUp() {
        accountList = accountListGenerator(MIN_NUM_ACCOUNT_FOR_MASS_TRANSFER);
        massTransferTx = new MassTransferTransactionSender(sender, assetId, assetAmount.value(), accountList);
        massTransferTx.massTransferTransactionSender(MassTransferTransaction.LATEST_VERSION);
        massTransferTxId = massTransferTx.getMassTransferTx().id();
        checkHeight();
    }

    private static void setAssetScriptSetUp() {
        setAssetScriptTx = sender.setAssetScript(assetId, script).tx();
        checkHeight();
    }

    private static void dataSetUp() {
        DataEntry[] dataEntries = new DataEntry[]{integerEntry, binaryEntry, booleanEntry, stringEntry};
        dataTx = new DataTransactionsSender(sender, dataEntries);
        dataTx.dataEntryTransactionSender(sender, DataTransaction.LATEST_VERSION);
        dataTransferTxId = dataTx.getTxInfo().tx().id();
        checkHeight();
    }

    private static void setScriptSetUp() {
        setScriptTx = new SetScriptTransactionSender(sender, script);
        setScriptTx.setScriptTransactionSender(MIN_FEE, SetScriptTransaction.LATEST_VERSION);
        setScriptTxId = setScriptTx.getSetScriptTx().id();
        checkHeight();
    }

    private static void sponsorFeeSetUp() {
        sponsorFeeTx = new SponsorFeeTransactionSender(sender, wavesAmount.value(), assetIdForSponsorFee);
        sponsorFeeTx.sponsorFeeTransactionSender(SUM_FEE, SponsorFeeTransaction.LATEST_VERSION);
        sponsorFeeTxId = sponsorFeeTx.getSponsorTx().id();
        checkHeight();
    }

    private static void exchangeSetUp() {
        long sumSellerTokens = wavesAmount.value();
        long offerForToken = 1000;
        long amountBefore = sender.getWavesBalance() - ONE_WAVES;

        orderAmount = Amount.of(sumSellerTokens, AssetId.WAVES);
        orderPrice = Amount.of(offerForToken, assetId);

        orderBuy = Order.buy(orderAmount, orderPrice, recipientPublicKey)
                .version(ORDER_V_3)
                .getSignedWith(recipientPrivateKey);

        orderSell = Order.sell(orderAmount, orderPrice, recipientPublicKey)
                .version(ORDER_V_4)
                .getSignedWith(senderPrivateKey);

        exchangeTx = new ExchangeTransactionSender(recipient, sender, orderBuy, orderSell);

        exchangeTx.exchangeTransactionSender(
                orderAmount.value(),
                orderPrice.value(),
                EXTRA_FEE,
                ExchangeTransaction.LATEST_VERSION
        );
        checkHeight();
    }

    private static void checkHeight() {
        indexCountTransactions += 1;
        if (height < node().getHeight()) {
            indexCountTransactions = 0;
            height = node().getHeight();
            heightsList.add(height);
        }
        transactionsIndexList.add(indexCountTransactions);
    }

    public static List<Integer> getTransactionsIndexList() {
        return transactionsIndexList;
    }
}
