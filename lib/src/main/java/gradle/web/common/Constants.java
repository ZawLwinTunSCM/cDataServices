package gradle.web.common;

import java.util.HashMap;
import java.util.Map;

public class Constants {
    public static Map<String, String> drivers = new HashMap<String, String>() {
        private static final long serialVersionUID = -3831706982616724741L;
        {
            put("kintone", "cdata.jdbc.kintone.KintoneDriver");
            put("salesforce", "cdata.jdbc.salesforce.SalesforceDriver");
            put("makeshop", "cdata.jdbc.gmomakeshop.GMOMakeShopDriver");
            put("facebookads", "cdata.jdbc.facebookads.FacebookAdsDriver");
            put("bigquery", "cdata.jdbc.googlebigquery.GoogleBigQueryDriver");
            put("googleads", "cdata.jdbc.googleads.GoogleAdsDriver");
            put("googlesheets", "cdata.jdbc.googlesheets.GoogleSheetsDriver");
            put("shopify", "cdata.jdbc.shopify.ShopifyDriver");
        }
    };
    public static Map<String, String[]> parameters = new HashMap<String, String[]>() {
        private static final long serialVersionUID = -3831706982616724741L;
        {
            put("kintone", new String[] { "User", "Password", "Url" });
            put("salesforce", new String[] { "User", "Password", "SecurityToken" });
            put("makeshop", new String[] { "ShopId", "MembersAccessCode", "OrdersAccessCode", "ProductsAccessCode" });
            put("facebookads", new String[] { "OAuthClientId", "OAuthClientSecret", "OAuthAccessToken" });
            put("bigquery", new String[] { "DatasetId", "ProjectId", "OAuthClientId", "OAuthClientSecret",
                    "OAuthAccessToken" });
            put("googleads", new String[] { "DeveloperToken", "ClientCustomerId", "OauthClientId", "OAuthClientSecret",
                    "OAuthAccessToken", "managerid" });
            put("googlesheets",
                    new String[] { "OAuthClientId", "OAuthClientSecret", "OAuthAccessToken", "Spreadsheet" });
            put("shopify", new String[] { "ShopUrl", "AccessToken" });
        }
    };
}
