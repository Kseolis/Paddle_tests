package im.mak.paddle.blockchain_updates.get_block_update_tests;

import com.wavesplatform.transactions.*;
import com.wavesplatform.transactions.account.Address;
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
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static im.mak.paddle.util.ScriptUtil.fromFile;

public class BaseGetBlockUpdateTest extends BaseGrpcTest {
    protected static Account sender;
    protected static Address senderAddress;
    protected static PrivateKey senderPrivateKey;
    protected static PublicKey senderPublicKey;

    protected static Account recipient;
    protected static PrivateKey recipientPrivateKey;
    protected static PublicKey recipientPublicKey;

    protected static Amount wavesAmount;

    protected static Order orderBuy;
    protected static Order orderSell;

    protected static String newAlias;

    protected static String assetName;
    protected static String assetDescription;
    protected static int assetDecimals;

    protected static IssueTransaction issueTx;
    protected static Id issueTxId;
    protected static long amountBeforeIssueTx;
    protected static long amountAfterIssueTx;
    protected static AssetId assetId;
    protected static Amount assetAmount;

    protected static TransferTransactionSender transferSender;
    protected static Id transferTxId;
    protected static long assetBalanceBeforeTransfer;
    protected static long wavesBalanceBeforeTransfer;

    protected static ReissueTransactionSender reissueTxSender;
    protected static Id reissueTxId;
    protected static long assetAmountBeforeReissueTx;
    protected static long assetAmountAfterReissueTx;

    protected static BurnTransactionSender burnTxSender;
    protected static Id burnTxId;
    protected static long assetAmountBeforeBurnTx;
    protected static long assetAmountAfterBurnTx;

    protected static CreateAliasTransactionSender aliasTx;
    protected static Id aliasTxId;
    protected static long amountBeforeAliasTx;
    protected static long amountAfterAliasTx;

    protected static SetAssetScriptTransaction setAssetScriptTx;
    protected static Id setAssetScriptTxId;

    protected static LeaseTransactionSender leaseTx;
    protected static Id leaseTxId;
    protected static LeaseCancelTransactionSender leaseCancelTx;

    protected static ExchangeTransactionSender exchangeTx;
    protected static Id exchangeTxId;

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
                    senderAddress = sender.address();
                    senderPrivateKey = sender.privateKey();
                    senderPublicKey = sender.publicKey();
                    assetIdForSponsorFee = sender.issue(i -> i.name("sponsorFeeAsset")).tx().assetId();
                },
                () -> {
                    recipient = new Account(DEFAULT_FAUCET);
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
        amountBeforeIssueTx = sender.getWavesBalance();
        issueTx = sender.issue(i -> i
                .name(assetName)
                .quantity(1_000_000_000_000L)
                .description(assetDescription)
                .decimals(assetDecimals)
                .reissuable(true)
                .script(SCRIPT_PERMITTING_OPERATIONS)
        ).tx();
        amountAfterIssueTx = sender.getWavesBalance();
        issueTxId = issueTx.id();
        assetId = issueTx.assetId();
        assetAmount = Amount.of(50_000, assetId);
        checkHeight();
    }

    private static void transferSetUp() {
        assetBalanceBeforeTransfer = sender.getBalance(assetId);
        wavesBalanceBeforeTransfer = sender.getWavesBalance();
        transferSender = new TransferTransactionSender(assetAmount, sender, recipient, SUM_FEE);
        transferSender.transferTransactionSender(ADDRESS, LATEST_VERSION);
        transferTxId = transferSender.getTransferTx().id();
        checkHeight();
    }

    private static void burnSetUp() {
        assetAmountBeforeBurnTx = sender.getBalance(assetId);
        assetAmountAfterBurnTx = assetAmountBeforeBurnTx - assetAmount.value();
        burnTxSender = new BurnTransactionSender(sender, assetAmount, SUM_FEE, BurnTransaction.LATEST_VERSION);
        burnTxSender.burnTransactionSender();
        burnTxId = burnTxSender.getBurnTx().id();
        checkHeight();
    }

    private static void reissueSetUp() {
        assetAmountBeforeReissueTx = sender.getBalance(assetId);
        assetAmountAfterReissueTx = assetAmountBeforeReissueTx + assetAmount.value();
        reissueTxSender = new ReissueTransactionSender(sender, assetAmount, assetId);
        reissueTxSender.reissueTransactionSender(SUM_FEE, ReissueTransaction.LATEST_VERSION);
        reissueTxId = reissueTxSender.getReissueTx().id();
        checkHeight();
    }

    private static void leaseSetUp() {
        leaseTx = new LeaseTransactionSender(sender, recipient, MIN_FEE);
        leaseTx.leaseTransactionSender(MIN_TRANSACTION_SUM, LeaseTransaction.LATEST_VERSION);
        leaseTxId = leaseTx.getTxInfo().tx().id();
        checkHeight();
    }

    private static void leaseCancelSetUp() {
        leaseCancelTx = new LeaseCancelTransactionSender(sender, recipient, MIN_FEE);
        leaseCancelTx.leaseCancelTransactionSender(
                leaseTxId,
                MIN_TRANSACTION_SUM,
                LeaseCancelTransaction.LATEST_VERSION
        );
        checkHeight();
    }

    private static void aliasSetUp() {
        amountBeforeAliasTx = sender.getWavesBalance();
        aliasTx = new CreateAliasTransactionSender(
                sender,
                newAlias,
                MIN_FEE,
                CreateAliasTransaction.LATEST_VERSION
        );
        aliasTx.createAliasTransactionSender();
        amountAfterAliasTx = sender.getWavesBalance();
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
        setAssetScriptTxId = setAssetScriptTx.id();
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
        orderBuy = Order.buy(wavesAmount, assetAmount, senderPublicKey).version(ORDER_V_3)
                .getSignedWith(senderPrivateKey);

        orderSell = Order.sell(wavesAmount, assetAmount, senderPublicKey).version(ORDER_V_4)
                .getSignedWith(recipientPrivateKey);

        exchangeTx = new ExchangeTransactionSender(sender, recipient, orderBuy, orderSell);

        exchangeTx.exchangeTransactionSender(
                wavesAmount.value(),
                assetAmount.value(),
                EXTRA_FEE,
                ExchangeTransaction.LATEST_VERSION
        );
        exchangeTxId = exchangeTx.getExchangeTx().id();
        checkHeight();
    }

    private static void checkHeight() {
        if (height < node().getHeight()) {
            height = node().getHeight();
            heightsList.add(height);
        }
    }
}
