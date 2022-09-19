package im.mak.paddle.blockchain_updates.subscribe_tests;

import com.wavesplatform.crypto.base.Base58;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.transactions.common.Base58String;
import im.mak.paddle.Account;
import im.mak.paddle.blockchain_updates.BaseGrpcTest;
import im.mak.paddle.helpers.dapps.DefaultDApp420Complexity;
import im.mak.paddle.helpers.transaction_senders.MassTransferTransactionSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.wavesplatform.transactions.MassTransferTransaction.LATEST_VERSION;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Calculations.getTransactionCommission;
import static im.mak.paddle.helpers.Randomizer.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.SubscribeHandler.subscribeResponseHandler;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.TransactionMetadataHandler.getMassTransferFromTransactionMetadata;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transaction_state_updates.Balances.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.MassTransferTransactionHandler.*;
import static im.mak.paddle.helpers.blockchain_updates_handlers.subscribe_handlers.transactions_handlers.TransactionsHandler.*;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class MassTransferTransactionSubscriptionGrpcTest extends BaseGrpcTest {
    private static Account senderAccount;
    private static String senderAddress;
    private static String senderPublicKey;

    private static DefaultDApp420Complexity dAppAccount;
    private static String dAppAccountAddress;
    private static String dAppAccountPublicKey;
    private static AssetId smartAssetId;

    private long amountValue;
    private long balanceBefore;

    private static Base58String base58StringAttachment;
    private static List<Account> accountList;

    @BeforeAll
    static void setUp() {
        async(
                () -> base58StringAttachment = new Base58String("attachment"),
                () -> {
                    senderAccount = new Account(DEFAULT_FAUCET);
                    senderAddress = senderAccount.address().toString();
                    senderPublicKey = senderAccount.publicKey().toString();
                },
                () -> {
                    dAppAccount = new DefaultDApp420Complexity(DEFAULT_FAUCET);
                    dAppAccount.createAlias(randomNumAndLetterString(15));
                    dAppAccountAddress = dAppAccount.address().toString();
                    dAppAccountPublicKey = dAppAccount.publicKey().toString();

                    smartAssetId = dAppAccount.issue(i -> i.name("Smart")
                            .quantity(900_000_000_000L)
                            .script(SCRIPT_PERMITTING_OPERATIONS)).tx().assetId();
                }
        );
    }

    @Test
    @DisplayName("Transaction Waves")
    void subscribeTestForMassTransferTransaction() {
        balanceBefore = senderAccount.getWavesBalance();
        amountValue = getRandomInt(10_000, 1_000_000);
        accountList = accountListGenerator(MAX_NUM_ACCOUNT_FOR_MASS_TRANSFER);

        MassTransferTransactionSender txSender =
                new MassTransferTransactionSender(senderAccount, AssetId.WAVES, amountValue, accountList);
        txSender.massTransferTransactionSender(LATEST_VERSION);
        String txId = txSender.getMassTransferTx().id().toString();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);

        checkMassTransferSubscribe(senderPublicKey, senderAddress, txSender);
    }

    @Test
    @DisplayName("DApp account & smart asset")
    void subscribeTestForMassTransferIssueSmartAssetTransaction() {
        balanceBefore = dAppAccount.getWavesBalance();
        amountValue = getRandomInt(10_000, 1_000_000);
        accountList = accountListGenerator(MAX_NUM_ACCOUNT_FOR_MASS_TRANSFER);

        MassTransferTransactionSender txSender =
                new MassTransferTransactionSender(dAppAccount, smartAssetId, amountValue, accountList);
        txSender.massTransferTransactionSender(LATEST_VERSION);
        String txId = txSender.getMassTransferTx().id().toString();

        height = node().getHeight();
        subscribeResponseHandler(CHANNEL, height, height, txId);

        checkMassTransferSubscribe(dAppAccountPublicKey, dAppAccountAddress, txSender);
    }

    private void checkMassTransferSubscribe(String publicKey, String address, MassTransferTransactionSender txSender) {
        assertThat(getTransactionFeeAmount(0)).isEqualTo(getTransactionCommission());
        assertAll(
                () -> assertThat(getChainId(0)).isEqualTo(CHAIN_ID),
                () -> assertThat(getSenderPublicKeyFromTransaction(0)).isEqualTo(publicKey),
                () -> assertThat(getTransactionFeeAmount(0)).isEqualTo(getTransactionCommission()),
                () -> assertThat(getTransactionVersion(0)).isEqualTo(LATEST_VERSION),
                () -> assertThat(getAttachmentFromMassTransfer(0)).isEqualTo(base58StringAttachment.toString()),
                () -> assertThat(getAssetIdFromMassTransfer(0)).isEqualTo(txSender.getAssetId()),
                () -> checkBalances(address, txSender)
        );

    }

    private void checkBalances(String address, MassTransferTransactionSender txSender) {
        for (int i = 0; i < accountList.size(); i++) {
            assertThat(getRecipientAmountFromMassTransfer(0, i)).isEqualTo(amountValue);
            assertThat(getRecipientPublicKeyHashFromMassTransfer(0, i)).isEqualTo(publicKeyHashFromList(i));
            assertThat(getMassTransferFromTransactionMetadata(0, i)).isEqualTo(accountAddressFromList(i));

            for (int j = 0; j < accountList.size(); j++) {

                final String addressFromBalance = getAddress(0, i);
                final String assetFromBalance = getAssetIdAmountAfter(0, i);

                if (accountAddressFromList(j).equals(addressFromBalance)) {
                    assertThat(getAmountBefore(0, i)).isEqualTo(0);
                    assertThat(getAmountAfter(0, i)).isEqualTo(amountValue);
                }

                if (address.equals(addressFromBalance) && txSender.getAssetId().equals(assetFromBalance)) {
                    assertThat(getAmountBefore(0, i)).isEqualTo(txSender.getSenderBalanceBeforeMassTransfer());
                    assertThat(getAmountAfter(0, i)).isEqualTo(txSender.getSenderBalanceAfterMassTransfer());
                } else if (!txSender.getAssetId().equals(assetFromBalance)) {
                    assertThat(getAmountBefore(0, i)).isEqualTo(balanceBefore);
                    assertThat(getAmountAfter(0, i)).isEqualTo(txSender.getBalanceAfterTransaction());
                }
            }
        }
    }


    private String publicKeyHashFromList(int index) {
        return Base58.encode(accountList.get(index).address().publicKeyHash());
    }

    private String accountAddressFromList(int index) {
        return accountList.get(index).address().toString();
    }
}