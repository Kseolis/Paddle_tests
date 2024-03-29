package im.mak.paddle.internal;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.io.File;

public class Settings {

    public final String name;
    public final String apiUrl;
    public final String grpcUrl;
    public final int grpcPort;
    public final long blockInterval;
    public final int minAssetInfoUpdateInterval;
    public final String faucetSeed;
    public final String dockerImage;
    public final boolean autoShutdown;
    public final String logLevel;

    public Settings(String apiUrl, String grpcUrl, int grpcPort, long blockInterval, int minAssetInfoUpdateInterval, String faucetSeed, String dockerImage, boolean autoShutdown, String logLevel) {
        this.grpcUrl = grpcUrl;
        this.grpcPort = grpcPort;
        this.logLevel = logLevel;
        this.name = "manual";
        this.apiUrl = apiUrl;
        this.blockInterval = blockInterval;
        this.minAssetInfoUpdateInterval = minAssetInfoUpdateInterval;
        this.faucetSeed = faucetSeed;
        this.dockerImage = dockerImage;
        this.autoShutdown = autoShutdown;
    }

    public Settings(String apiUrl, String grpcUrl, int grpcPort, String logLevel) {
        this(apiUrl, grpcUrl, grpcPort, 60_000, 100_000, null, null, false, logLevel);
    }

    public Settings() {
        String base = "paddle";

        String rootPath = System.getProperty("user.dir") + "/" + base;

        Config overridden = ConfigFactory.parseFileAnySyntax(new File(rootPath))
                .withFallback(ConfigFactory.load(base))
                .withFallback(ConfigFactory.defaultReference());

        name = overridden.getString("paddle.profile");
        Config _conf = overridden.getObject("paddle." + name).toConfig();

        apiUrl = _conf.getString("api-url");
        grpcUrl = _conf.getString("grpc-url");
        grpcPort = Integer.parseInt(_conf.getString("grpc-port"));
        blockInterval = _conf.hasPath("block-interval") ? _conf.getDuration("block-interval").toMillis() : 60_000;
        minAssetInfoUpdateInterval = _conf.hasPath("min-asset-info-update-interval")
                ? _conf.getInt("min-asset-info-update-interval") : 0;
        faucetSeed = _conf.hasPath("faucet-seed") ? _conf.getString("faucet-seed") : null;
        dockerImage = _conf.hasPath("docker-image") ? _conf.getString("docker-image") : null;
        autoShutdown = !_conf.hasPath("auto-shutdown") || _conf.getBoolean("auto-shutdown");
        logLevel = _conf.getString("log-level");
    }

}
