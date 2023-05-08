package jp.co.axa.apidemo.util;

import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;

/**
 * Utility class for HTTP headers creation.
 */
@Log4j2
public final class HeaderUtil {

    private static String HEADER_MESSAGE = "X-AXA-Message";
    private static String HEADER_PARAM = "X-AXA-Param";
    private static String HEADER_ERROR = "X-AXA-Error";
    private static String HEADER_PAGINATION_TOTAL_COUNT = "X-Total-Count";
    private static String HEADER_PAGINATION_PAGE_NO = "X-Page-No";


    private static String APPLICATION_NAME = "apidemo";

    private HeaderUtil() {
    }

    public static HttpHeaders createHeaders(String message, String param) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_MESSAGE, message);
        headers.add(HEADER_PARAM, param);
        return headers;
    }

    public static HttpHeaders createEntityCreationHeaders(String entityName, String param) {
        return createHeaders(APPLICATION_NAME + "." + entityName + ".created", param);
    }

    public static HttpHeaders createEntityUpdateHeaders(String entityName, String param) {
        return createHeaders(APPLICATION_NAME + "." + entityName + ".updated", param);
    }

    public static HttpHeaders createEntityDeleteHeaders(String entityName, String param) {
        return createHeaders(APPLICATION_NAME + "." + entityName + ".deleted", param);
    }
    public static HttpHeaders createEntityNotFoundHeader(String entityName) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_PARAM, entityName);
        return headers;
    }

    public static HttpHeaders createFailureHeaders(String entityName, String errorKey, String defaultMessage) {
        log.info("Entity processing failed, {}", defaultMessage);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_ERROR, "error." + errorKey);
        headers.add(HEADER_PARAM, entityName);
        return headers;
    }

    public static HttpHeaders generatePaginationHeaders(Page page) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HEADER_PAGINATION_TOTAL_COUNT, Long.toString(page.getTotalElements()));
        headers.add(HEADER_PAGINATION_PAGE_NO, Long.toString(page.getNumber()));
        return headers;
    }
}