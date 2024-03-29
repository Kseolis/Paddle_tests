package im.mak.paddle.util;

public class Constants {
    public static final String WAVES_STRING_ID = "";
    public static final int MAX_LIB_VERSION = 6;

    public static final long ETHEREUM_TX_LATEST_VERSION = 1;

    public static final byte ASSET_QUANTITY_MIN = 1;
    public static final long ASSET_QUANTITY_MAX = 9_223_372_036_854_775_807L;

    public static final long MIN_FEE = 100_000L;
    public static final long EXTRA_FEE = 400_000L;
    public static final long SUM_FEE = MIN_FEE + EXTRA_FEE;
    public static final long FEE_FOR_DAPP_ACC = SUM_FEE + EXTRA_FEE;

    public static final long MIN_FEE_FOR_SET_SCRIPT = 1_000_000L;
    public static final long EXTRA_FEE_FOR_SET_SCRIPT = 4_000_000L;
    public static final long ONE_WAVES = 100_000_000L;
    public static final long FIVE_WAVES = 500_000_000L;

    public static final long MIN_FEE_FOR_EXCHANGE = 300_000L;
    public static final long EXCHANGE_FEE_FOR_SMART_ASSETS = EXTRA_FEE * 2;
    public static final long FEE_FOR_MASS_TRANSFER = 50000L ;

    public static final int DEFAULT_DECIMALS = 8;
    public static final byte ASSET_DECIMALS_MIN = 0;
    public static final byte ASSET_DECIMALS_MAX = 8;
    public static final int DEFAULT_FAUCET = 10_00000000;

    public static final byte MIN_TRANSACTION_SUM = 1;

    public static final int MIN_NUM_ACCOUNT_FOR_MASS_TRANSFER = 1;
    public static final int MAX_NUM_ACCOUNT_FOR_MASS_TRANSFER = 100;

    public static final String ADDRESS = "Address";
    public static final String ALIAS = "Alias";

    public static final String SCRIPT_PERMITTING_OPERATIONS = "{-# STDLIB_VERSION 5 #-} {-# SCRIPT_TYPE ASSET #-} true";

    public static final byte ORDER_V_3 = 3;
    public static final byte ORDER_V_4 = 4;

    public static final String ACTIVE_STATUS_LEASE = "ACTIVE";
    public static final String INACTIVE_STATUS_LEASE = "INACTIVE";

    public static final String BUY = "BUY";
    public static final String SELL = "SELL";

    // asset data keys
    public static final String ASSET_ID = "asset_id";
    public static final String ISSUER = "issuer";
    public static final String DECIMALS = "decimals";
    public static final String DESCRIPTION = "description";
    public static final String NONCE = "nonce";
    public static final String NAME = "name";
    public static final String REISSUE = "reissue";
    public static final String VOLUME = "volume";

    // arg types
    public static final String INTEGER = "integer";
    public static final String STRING = "string";
    public static final String BOOLEAN = "boolean";
    public static final String BINARY_VALUE = "binary_value";
    public static final String BINARY_BASE58 = "binary_value_58";
    public static final String BINARY_BASE64 = "binary_value_64";
    public static final String DATA_ENTRY_INT = "int";
    public static final String DATA_ENTRY_BYTE = "byte";
    public static final String DATA_ENTRY_BOOL = "bool";
    public static final String DATA_ENTRY_STR = "str";
}
