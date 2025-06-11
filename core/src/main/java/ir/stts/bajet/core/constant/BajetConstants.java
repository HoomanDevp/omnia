package ir.stts.bajet.core.constant;

import ir.stts.bajet.core.message.entity.Message;
import ir.stts.bajet.core.resilience.entity.Error;
import ir.stts.bajet.core.setting.entity.Setting;

import java.util.ArrayList;
import java.util.List;

public class BajetConstants {

    /**
     * Global keys
     */
    public static final String BAJET_BASE_PACKAGE = "ir.stts.bajet";

    /**
     * Environment keys
     */
    public static final String DEV_ENV = "dev";
    public static final String TEST_ENV = "test";
    public static final String STAGE_ENV = "stage";
    public static final String PROD_ENV = "prod";

    /**
     * Cache data
     */
    public final static List<Error> ERRORS = new ArrayList<>();
    public final static List<Setting> SETTINGS = new ArrayList<>();
    public final static List<Message> MESSAGES = new ArrayList<>();

    /**
     * Prometheus
     */
    public static final String GATEWAY_COUNTER = "gateway_req_count";
    public static final String GATEWAY_ERROR_COUNTER = "gateway_req_error_count";
    public static final String GATEWAY_SUCCESS_COUNTER = "gateway_req_success_count";
    public static final String HTTP_COUNTER = "http_req_count";
    public static final String HTTP_ERROR_COUNTER = "http_req_error_count";
    public static final String HTTP_SUCCESS_COUNTER = "http_req_success_count";
    public static final String ERROR_COUNTER = "error_count";
    public static final String UNKNOWN_ERROR_COUNTER = "unknown_error_count";
    public static final String ERROR_LIVE_GAUGE = "error_live_count";
    public static final String ERROR_LIVE_THRESHOLD_GAUGE = "error_live_threshold";

    /**
     * Request Signing
     */
    public static final String EMPTY_BODY = "EMPTY_BODY";

    /**
     * Encrypted Bodies Keys
     */
    public static final String EB_IDENTITY_CODE = "identity_code";
    public static final String EB_PAYLOAD = "payload";

    /**
     * Headers
     */
    public static final String ORIGINAL_IP_HEADER_KEY = "x-original-forwarded-for";

}