package im.mak.paddle.transactions;

import com.wavesplatform.transactions.EthereumTransaction;
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
import static im.mak.paddle.helpers.EthereumTestUser.getEthInstance;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

public class EthereumTransferTransactionTest {
    private static EthereumTestUser ethInstance;
    private static Address senderAddress;
    private static Account recipient;
    private static Address recipientAddress;
    private static AssetId issuedAssetId;
    private static AssetId issuedSmartAssetId;
    private static Amount minAmountTransfer;
    private static Amount amountTransfer;
    private static Amount transferAmountSimpleIssuedAsset;
    private static Amount transferAmountSmartIssuedAsset;

    @BeforeAll
    static void setUp() {
        async(
                () -> {
                    try {
                        ethInstance = getEthInstance();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    senderAddress = ethInstance.getSenderAddress();
                    node().faucet().transfer(senderAddress, 1_0000_0000L, AssetId.WAVES, i -> i.additionalFee(0));
                },
                () -> {
                    recipient = new Account(3_0000_0000L);
                    recipientAddress = recipient.address();
                    issuedAssetId = recipient.issue(i -> i.name("Test_Asset").quantity(1000).decimals(8)).tx().assetId();
                    issuedSmartAssetId = recipient.issue(i -> i.name("T_smart").quantity(1000).decimals(8).script(SCRIPT_PERMITTING_OPERATIONS)).tx().assetId();

                    recipient.transfer(senderAddress, Amount.of(recipient.getBalance(issuedAssetId), issuedAssetId), i -> i.additionalFee(0));
                    recipient.transfer(senderAddress, Amount.of(recipient.getBalance(issuedSmartAssetId), issuedSmartAssetId), i -> i.additionalFee(0));
                    transferAmountSimpleIssuedAsset = Amount.of(getRandomInt(1, 100), issuedAssetId);
                    transferAmountSmartIssuedAsset = Amount.of(getRandomInt(1, 100), issuedSmartAssetId);
                },
                () -> minAmountTransfer = Amount.of(1),
                () -> amountTransfer = Amount.of(getRandomInt(100, 1_000_000))
        );
    }

    @Test
    @DisplayName("Test of transferring a minimum amount using an Ethereum transaction")
    void subscribeTestForTransferMinimumAmountTransaction() throws NodeException, IOException {
        EthereumTransferTransactionSender txSender = new EthereumTransferTransactionSender(senderAddress, recipientAddress, minAmountTransfer, MIN_FEE);
        txSender.sendingAnEthereumTransferTransaction();
        EthereumTransaction.Transfer payload = (EthereumTransaction.Transfer) txSender.getEthTx().payload();
        checkEthereumTransfer(txSender, payload, minAmountTransfer);
        checkBalancesAfterTx(txSender, minAmountTransfer.assetId());
    }

    @Test
    @DisplayName("Test of transferring a random amount using an Ethereum transaction")
    void subscribeTestForTransferTransaction() throws NodeException, IOException {
        EthereumTransferTransactionSender txSender = new EthereumTransferTransactionSender(senderAddress, recipientAddress, amountTransfer, MIN_FEE);
        txSender.sendingAnEthereumTransferTransaction();
        EthereumTransaction.Transfer payload = (EthereumTransaction.Transfer) txSender.getEthTx().payload();
        checkEthereumTransfer(txSender, payload, amountTransfer);
        checkBalancesAfterTx(txSender, amountTransfer.assetId());
    }

    @Test
    @DisplayName("Test of transferring issued asset a random amount using an Ethereum transaction")
    void subscribeTestForTransferIssuedAssetTransaction() throws NodeException, IOException {
        EthereumTransferTransactionSender txSender = new EthereumTransferTransactionSender(senderAddress, recipientAddress, transferAmountSimpleIssuedAsset, MIN_FEE);
        txSender.sendingAnEthereumTransferTransaction();
        EthereumTransaction.Transfer payload = (EthereumTransaction.Transfer) txSender.getEthTx().payload();
        checkEthereumTransfer(txSender, payload, transferAmountSimpleIssuedAsset);
        checkBalancesAfterTx(txSender, transferAmountSimpleIssuedAsset.assetId());
    }

    @Test
    @DisplayName("Test of transferring issued smart asset a random amount using an Ethereum transaction")
    void subscribeTestForTransferIssuedSmartAssetTransaction() throws NodeException, IOException {
        EthereumTransferTransactionSender txSender = new EthereumTransferTransactionSender(senderAddress, recipientAddress, transferAmountSmartIssuedAsset, SUM_FEE);
        txSender.sendingAnEthereumTransferTransaction();
        EthereumTransaction.Transfer payload = (EthereumTransaction.Transfer) txSender.getEthTx().payload();
        checkEthereumTransfer(txSender, payload, transferAmountSmartIssuedAsset);
        checkBalancesAfterTx(txSender, transferAmountSmartIssuedAsset.assetId());
    }

    private void checkEthereumTransfer(EthereumTransferTransactionSender txSender, EthereumTransaction.Transfer payload, Amount amount) {
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

    private void checkBalancesAfterTx(EthereumTransferTransactionSender txSender, AssetId assetId) {
        assertAll(
                () -> assertThat(node().getBalance(senderAddress)).isEqualTo(txSender.getBalances().getSenderBalanceAfterEthTransaction()),
                () -> assertThat(node().getBalance(recipientAddress)).isEqualTo(txSender.getBalances().getRecipientBalanceAfterEthTransaction())
        );
        if (!assetId.isWaves()) {
            assertAll(
                    () -> assertThat(node().getAssetBalance(senderAddress, assetId)).isEqualTo(txSender.getBalances().getSenderAssetBalanceAfterTransaction()),
                    () -> assertThat(node().getAssetBalance(recipientAddress, assetId)).isEqualTo(txSender.getBalances().getRecipientAssetBalanceAfterTransaction())
            );
        }
    }
}

