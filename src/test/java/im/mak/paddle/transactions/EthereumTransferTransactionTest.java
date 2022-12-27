package im.mak.paddle.transactions;

import com.wavesplatform.transactions.EthereumTransaction;
import com.wavesplatform.transactions.EthereumTransaction.Transfer;
import com.wavesplatform.transactions.account.Address;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.wavesj.exceptions.NodeException;
import im.mak.paddle.Account;
import im.mak.paddle.helpers.EthereumTestUser;
import im.mak.paddle.helpers.transaction_senders.EthereumTransferTransactionSender;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static com.wavesplatform.transactions.common.AssetId.WAVES;
import static com.wavesplatform.wavesj.ApplicationStatus.SUCCEEDED;
import static im.mak.paddle.Node.node;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class EthereumTransferTransactionTest {
    private static EthereumTestUser ethereumTestUser;
    private static Address senderAddress;
    private static Account recipient;
    private static Address recipientAddress;
    private static EthereumTransferTransactionSender txSender;
    private static Transfer payload;

    @BeforeAll
    static void setUp() {
        async(
                () -> {
                    try {
                        ethereumTestUser = new EthereumTestUser();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    senderAddress = ethereumTestUser.getSenderAddress();
                    node().faucet().transfer(senderAddress, 1_0000_0000L, AssetId.WAVES, i -> i.additionalFee(0));
                },
                () -> {
                    recipient = new Account(3_0000_0000L);
                    recipientAddress = recipient.address();
                }
        );
    }

    @Test
    @DisplayName("Test of transferring a minimum amount using an Ethereum transaction")
    void ethereumTransferTransactionMinimumAmountForWaves() throws NodeException, IOException {
        final Amount minAmountTransfer = Amount.of(1);
        txSender = new EthereumTransferTransactionSender(ethereumTestUser, recipientAddress, minAmountTransfer, MIN_FEE);
        txSender.sendingAnEthereumTransferTransaction();
        payload = (Transfer) txSender.getEthTx().payload();
        checkEthereumTransfer(minAmountTransfer);
        checkBalancesAfterTx(minAmountTransfer.assetId());
    }

    @Test
    @DisplayName("Test of transferring a random amount using an Ethereum transaction")
    void ethereumTransferTransactionForWaves() throws NodeException, IOException {
        final Amount amountTransfer = Amount.of(getRandomInt(100, 1_000_000));
        txSender = new EthereumTransferTransactionSender(ethereumTestUser, recipientAddress, amountTransfer, MIN_FEE);
        txSender.sendingAnEthereumTransferTransaction();
        payload = (Transfer) txSender.getEthTx().payload();
        checkEthereumTransfer(amountTransfer);
        checkBalancesAfterTx(amountTransfer.assetId());
    }

    @Test
    @DisplayName("Test of transferring issued asset a random amount using an Ethereum transaction")
    void ethereumTransferTransactionAmountForIssuedAsset() throws NodeException, IOException {
        final AssetId issuedAssetId = recipient.issue(i -> i.name("Test_Asset").quantity(1000).decimals(8)).tx().assetId();
        recipient.transfer(senderAddress, Amount.of(recipient.getBalance(issuedAssetId), issuedAssetId), i -> i.additionalFee(0));
        final Amount transferAmountSimpleIssuedAsset = Amount.of(getRandomInt(1, 100), issuedAssetId);
        txSender = new EthereumTransferTransactionSender(ethereumTestUser, recipientAddress, transferAmountSimpleIssuedAsset, MIN_FEE);

        txSender.sendingAnEthereumTransferTransaction();
        payload = (Transfer) txSender.getEthTx().payload();
        checkEthereumTransfer(transferAmountSimpleIssuedAsset);
        checkBalancesAfterTx(transferAmountSimpleIssuedAsset.assetId());
    }

    @Test
    @DisplayName("Test of transferring issued smart asset a random amount using an Ethereum transaction")
    void ethereumTransferTransactionAmountForIssuedSmartAsset() throws NodeException, IOException {
        final AssetId issuedSmartAssetId = recipient.issue(i -> i.name("T_smart").quantity(1000).decimals(8).script(SCRIPT_PERMITTING_OPERATIONS)).tx().assetId();
        recipient.transfer(senderAddress, Amount.of(recipient.getBalance(issuedSmartAssetId), issuedSmartAssetId), i -> i.additionalFee(0));
        final Amount transferAmountSmartIssuedAsset = Amount.of(getRandomInt(1, 100), issuedSmartAssetId);

        txSender = new EthereumTransferTransactionSender(ethereumTestUser, recipientAddress, transferAmountSmartIssuedAsset, SUM_FEE);
        txSender.sendingAnEthereumTransferTransaction();
        payload = (Transfer) txSender.getEthTx().payload();
        checkEthereumTransfer(transferAmountSmartIssuedAsset);
        checkBalancesAfterTx(transferAmountSmartIssuedAsset.assetId());
    }

    private void checkEthereumTransfer(Amount amount) {
        assertAll(
                () -> assertThat(txSender.getTxInfo().applicationStatus()).isEqualTo(SUCCEEDED),
                () -> assertThat(txSender.getEthTx().chainId()).isEqualTo(node().chainId()),
                () -> assertThat(txSender.getEthTx().type()).isEqualTo(EthereumTransaction.TYPE_TAG),
                () -> assertThat(txSender.getEthTx().version()).isEqualTo(ETHEREUM_TX_LATEST_VERSION),
                () -> assertThat(txSender.getEthTx().gasPrice()).isEqualTo(EthereumTransaction.DEFAULT_GAS_PRICE),
                () -> assertThat(txSender.getEthTx().timestamp()).isEqualTo(txSender.getEthTimestamp()),
                () -> assertThat(txSender.getEthTx().fee().assetId()).isEqualTo(WAVES),
                () -> assertThat(txSender.getEthTx().fee().value()).isEqualTo(txSender.getEthFee()),
                () -> assertThat(txSender.getEthTx().id()).isEqualTo(txSender.getEthTxId()),
                () -> assertThat(txSender.getEthTx().sender().address()).isEqualTo(senderAddress),
                () -> assertThat(payload.amount()).isEqualTo(amount),
                () -> assertThat(payload.recipient()).isEqualTo(recipientAddress)

        );
    }

    private void checkBalancesAfterTx(AssetId assetId) {
        assertAll(
              //  () -> assertThat(node().getBalance(senderAddress)).isEqualTo(txSender.getSenderBalanceAfterEthTransaction()),
                () -> assertThat(node().getBalance(recipientAddress)).isEqualTo(txSender.getRecipientBalanceAfterEthTransaction())
        );
        if (!assetId.isWaves()) {
            assertAll(
                    () -> assertThat(node().getAssetBalance(senderAddress, assetId)).isEqualTo(txSender.getSenderAssetBalanceAfterTransaction()),
                    () -> assertThat(node().getAssetBalance(recipientAddress, assetId)).isEqualTo(txSender.getRecipientAssetBalanceAfterTransaction())
            );
        }
    }
}

