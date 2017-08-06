package veg.mediaplayer.sdk;

public class ThumbnailerConfig {
    private static final String TAG = "MediaPlayerConfig";
    private float bogoMIPS = 0.0f;
    private int connectionNetworkProtocol = -1;
    private String connectionUrl = "";
    private int dataReceiveTimeout = 30000;
    private int numberOfCPUCores = 1;
    private int out_height = 240;
    private int out_width = 240;

    public ThumbnailerConfig() {
        resetToDefault();
    }

    public ThumbnailerConfig(String connectionUrl, int connectionNetworkProtocol, int dataReceiveTimeout, int numberOfCPUCores, float bogoMIPS) {
        this.connectionUrl = connectionUrl;
        this.connectionNetworkProtocol = connectionNetworkProtocol;
        this.dataReceiveTimeout = dataReceiveTimeout;
        this.numberOfCPUCores = numberOfCPUCores;
        this.bogoMIPS = bogoMIPS;
    }

    public ThumbnailerConfig(ThumbnailerConfig src) {
        this.connectionUrl = src.connectionUrl;
        this.connectionNetworkProtocol = src.connectionNetworkProtocol;
        this.dataReceiveTimeout = src.dataReceiveTimeout;
        this.numberOfCPUCores = src.numberOfCPUCores;
        this.bogoMIPS = src.bogoMIPS;
        this.out_width = src.out_width;
        this.out_height = src.out_height;
    }

    public String getConnectionUrl() {
        return this.connectionUrl;
    }

    public void setConnectionUrl(String connectionUrl) {
        this.connectionUrl = connectionUrl;
    }

    public int getNumberOfCPUCores() {
        return this.numberOfCPUCores;
    }

    public void setNumberOfCPUCores(int numberOfCPUCores) {
        this.numberOfCPUCores = numberOfCPUCores;
    }

    public int getConnectionNetworkProtocol() {
        return this.connectionNetworkProtocol;
    }

    public void setConnectionNetworkProtocol(int connectionNetworkProtocol) {
        this.connectionNetworkProtocol = connectionNetworkProtocol;
    }

    public int getDataReceiveTimeout() {
        return this.dataReceiveTimeout;
    }

    public void setDataReceiveTimeout(int dataReceiveTimeout) {
        this.dataReceiveTimeout = dataReceiveTimeout;
    }

    public float getBogoMIPS() {
        return this.bogoMIPS;
    }

    public void setBogoMIPS(float bogoMIPS) {
        this.bogoMIPS = bogoMIPS;
    }

    public void setOutWidth(int width) {
        this.out_width = width;
    }

    public int getOutWidth() {
        return this.out_width;
    }

    public void setOutHeight(int height) {
        this.out_height = height;
    }

    public int getOutHeight() {
        return this.out_height;
    }

    public void resetToDefault() {
        this.connectionUrl = "";
        this.connectionNetworkProtocol = -1;
        this.dataReceiveTimeout = 30000;
        this.numberOfCPUCores = 1;
        this.bogoMIPS = 0.0f;
        this.out_width = 240;
        this.out_height = 240;
    }
}
