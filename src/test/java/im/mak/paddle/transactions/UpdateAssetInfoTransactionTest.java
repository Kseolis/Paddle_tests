package im.mak.paddle.transactions;

import com.wavesplatform.transactions.UpdateAssetInfoTransaction;
import com.wavesplatform.transactions.account.PublicKey;
import com.wavesplatform.transactions.common.AssetId;
import com.wavesplatform.wavesj.info.IssueTransactionInfo;
import im.mak.paddle.Account;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.wavesplatform.transactions.LeaseTransaction.LATEST_VERSION;
import static im.mak.paddle.util.Constants.*;

public class UpdateAssetInfoTransactionTest {
    private static Account account;
    private static PublicKey accountPublicKey;
    private static IssueTransactionInfo issueTransactionInfo;
    private static AssetId assetId;


    @BeforeAll
    static void before() {
        account = new Account(DEFAULT_FAUCET);
        accountPublicKey = account.publicKey();
        issueTransactionInfo = account.issue(i -> i
                .name("Asset")
                .description("Asset for test UpdateAssetInfo")
                .reissuable(true)
                .quantity(1000_00000000L)
                .script(null)
        );
        assetId = issueTransactionInfo.tx().assetId();
    }

    @Test
    @DisplayName("Update asset info")
    void leaseMinimumWavesAssets() {
        for (int v = 1; v <= LATEST_VERSION; v++) {
            UpdateAssetInfoTransaction txSender = new UpdateAssetInfoTransaction(accountPublicKey, assetId, "Edited asset", "Edited description");
            System.out.println(txSender);

        }
    }

}
