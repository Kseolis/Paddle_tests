package im.mak.paddle.transactions;

import com.wavesplatform.transactions.UpdateAssetInfoTransaction;
import com.wavesplatform.transactions.account.PrivateKey;
import com.wavesplatform.transactions.account.PublicKey;
import com.wavesplatform.transactions.common.Amount;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.wavesj.info.IssueTransactionInfo;
import im.mak.paddle.Account;
import im.mak.paddle.helpers.transaction_senders.UpdateAssetInfoSender;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.UpdateAssetInfoTransaction.LATEST_VERSION;
import static com.wavesplatform.wavesj.ApplicationStatus.SUCCEEDED;
import static im.mak.paddle.blockchain_updates.BaseGrpcTest.CHAIN_ID;
import static im.mak.paddle.helpers.Randomizer.getRandomInt;
import static im.mak.paddle.helpers.Randomizer.randomNumAndLetterString;
import static im.mak.paddle.util.Async.async;
import static im.mak.paddle.util.Constants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.testcontainers.utility.Base58.randomString;

public class UpdateAssetInfoTransactionTest {
    private Account sender;
    private PublicKey senderPublicKey;
    private PrivateKey senderPrivateKey;
    private AssetId assetId;
    private AssetId smartAssetId;
    private Amount feeAmount;
    private String assetName;
    private String assetDescription;
    private String newAssetName;
    private String newAssetDescription;
    private UpdateAssetInfoSender txSender;

    @BeforeEach
    void before() {
        async(
                () -> {
                    sender = new Account(DEFAULT_FAUCET);
                    senderPublicKey = sender.publicKey();
                    senderPrivateKey = sender.privateKey();
                    assetName = randomNumAndLetterString(10);
                    assetDescription = randomNumAndLetterString(getRandomInt(2, 50));
                    IssueTransactionInfo issueTransactionInfo = sender.issue(i -> i.name(assetName).description(assetDescription));
                    IssueTransactionInfo issueTransactionSmartAssetInfo = sender.issue(i -> i.name(assetName).description(assetDescription).script(SCRIPT_PERMITTING_OPERATIONS));
                    assetId = issueTransactionInfo.tx().assetId();
                    smartAssetId = issueTransactionSmartAssetInfo.tx().assetId();
                }
        );
    }

    @Test
    @DisplayName("minimally short name and description")
    void updateAssetInfoMinimallyShortNameAndDescriptionTest() {
        newAssetName = randomString(4);
        newAssetDescription = "";
        feeAmount = Amount.of(MIN_FEE);
        for (int v = 1; v <= LATEST_VERSION; v++) {
            txSender = new UpdateAssetInfoSender(assetId, newAssetName, newAssetDescription, feeAmount, senderPrivateKey);
            txSender.updateAssetInfoSending(v, 0);
            checkMassTransferTransaction(assetId);
        }
    }

    @Test
    @DisplayName("the longest possible name and description")
    void updateAssetInfoLongestPossibleNameAndDescriptionTest() {
        newAssetName = randomString(16);
        newAssetDescription = randomString(1000);
        feeAmount = Amount.of(MIN_FEE);
        for (int v = 1; v <= LATEST_VERSION; v++) {
            txSender = new UpdateAssetInfoSender(assetId, newAssetName, newAssetDescription, feeAmount, senderPrivateKey);
            txSender.updateAssetInfoSending(v, 0);
            checkMassTransferTransaction(assetId);
        }
    }

    @Test
    @DisplayName("minimally short name and description for smart asset")
    void updateAssetInfoForSmartAssetMinimallyShortNameAndDescriptionTest() {
        newAssetName = randomString(4);
        newAssetDescription = "";
        feeAmount = Amount.of(MIN_FEE);
        for (int v = 1; v <= LATEST_VERSION; v++) {
            txSender = new UpdateAssetInfoSender(smartAssetId, newAssetName, newAssetDescription, feeAmount, senderPrivateKey);
            txSender.updateAssetInfoSending(v, EXTRA_FEE);
            feeAmount = Amount.of(SUM_FEE);
            checkMassTransferTransaction(smartAssetId);
        }
    }

    @Test
    @DisplayName("the longest possible name and description for smart asset")
    void updateAssetInfoForSmartAssetLongestPossibleNameAndDescriptionTest() {
        newAssetName = randomString(16);
        newAssetDescription = randomString(1000);
        feeAmount = Amount.of(MIN_FEE);
        for (int v = 1; v <= LATEST_VERSION; v++) {
            txSender = new UpdateAssetInfoSender(smartAssetId, newAssetName, newAssetDescription, feeAmount, senderPrivateKey);
            txSender.updateAssetInfoSending(v, EXTRA_FEE);
            feeAmount = Amount.of(SUM_FEE);
            checkMassTransferTransaction(smartAssetId);
        }
    }

    private void checkMassTransferTransaction(AssetId assetId) {
        assertAll(
                () -> assertThat(txSender.getTxInfo().applicationStatus()).isEqualTo(SUCCEEDED),
                () -> assertThat(txSender.getUpdAssetInfoTx().chainId()).isEqualTo((byte) CHAIN_ID),
                () -> assertThat(txSender.getUpdAssetInfoTx().type()).isEqualTo(UpdateAssetInfoTransaction.TYPE),
                () -> assertThat(txSender.getUpdAssetInfoTx().id()).isEqualTo(txSender.getUpdAssetInfoTxId()),
                () -> assertThat(txSender.getUpdAssetInfoTx().sender()).isEqualTo(senderPublicKey),
                () -> assertThat(txSender.getUpdAssetInfoTx().proofs().size()).isEqualTo(1),
                () -> assertThat(txSender.getUpdAssetInfoTx().assetId()).isEqualTo(assetId),
                () -> assertThat(txSender.getUpdAssetInfoTx().description()).isEqualTo(newAssetDescription),
                () -> assertThat(txSender.getUpdAssetInfoTx().name()).isEqualTo(newAssetName),
                () -> assertThat(txSender.getUpdAssetInfoTx().fee()).isEqualTo(feeAmount)
        );
    }

}
