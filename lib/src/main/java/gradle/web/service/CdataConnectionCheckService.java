package gradle.web.service;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import gradle.web.common.Constants;

@Service
public class CdataConnectionCheckService {
    public Map<String, String> getHeaders(HttpHeaders headers) {
        return headers.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get(0)));
    }

    public boolean checkServiceType(String service) {
        return Constants.drivers.containsKey(service.toLowerCase());
    }

    public String checkParameters(String service, Map<String, String> headersMap) {
        String[] parameters = Constants.parameters.get(service.toLowerCase());
        return Arrays.stream(parameters).map(String::toLowerCase).filter(parameter -> headersMap.get(parameter) == null)
                .findFirst().orElse(null);
    }

    public boolean checkConnection(String service, Map<String, String> headersMap)
            throws SQLException, ClassNotFoundException {
        try (Connection conn = getConnection(service, headersMap)) {
            System.out.println("Getting the table " + conn.isValid(0));
            getTables(conn);
            return conn.isValid(0);
        }
    }

    public Connection getConnection(String service, Map<String, String> headersMap)
            throws SQLException, ClassNotFoundException {
        String lowerCaseService = service.toLowerCase();
        Class.forName(Constants.drivers.get(lowerCaseService));
        Properties properties = new Properties();

        switch (lowerCaseService) {
        case "kintone":
            properties.setProperty("User", headersMap.get("user"));
            properties.setProperty("Password", headersMap.get("password"));
            properties.setProperty("Url", headersMap.get("url"));
            return DriverManager.getConnection("jdbc:kintone:", properties);

        case "salesforce":
            properties.setProperty("User", headersMap.get("user"));
            properties.setProperty("Password", headersMap.get("password"));
            properties.setProperty("Security Token", headersMap.get("securitytoken"));
            return DriverManager.getConnection("jdbc:salesforce:", properties);

        case "facebookads":
            properties.setProperty("InitiateOAuth", "REFRESH");
            properties.setProperty("OAuthClientId", headersMap.get("oauthclientid"));
            properties.setProperty("OAuthClientSecret", headersMap.get("oauthclientsecret"));
            properties.setProperty("OAuthAccessToken", headersMap.get("oauthaccesstoken"));
            return DriverManager.getConnection("jdbc:facebookads:", properties);

        case "googlesheets":
            properties.setProperty("InitiateOAuth", "REFRESH");
            properties.setProperty("OAuthClientId", headersMap.get("oauthclientid"));
            properties.setProperty("OAuthClientSecret", headersMap.get("oauthclientsecret"));
            properties.setProperty("OAuthAccessToken", headersMap.get("oauthaccesstoken"));
            properties.setProperty("Spreadsheet", headersMap.get("spreadsheet"));
            return DriverManager.getConnection("jdbc:googlesheets:", properties);

        case "bigquery":
            properties.setProperty("InitiateOAuth", "REFRESH");
            properties.setProperty("ProjectId", headersMap.get("projectid"));
            properties.setProperty("DatasetId", headersMap.get("datasetid"));
            properties.setProperty("OAuthClientId", headersMap.get("oauthclientid"));
            properties.setProperty("OAuthClientSecret", headersMap.get("oauthclientsecret"));
            properties.setProperty("OAuthAccessToken", headersMap.get("oauthaccesstoken"));
            return DriverManager.getConnection("jdbc:googlebigquery:", properties);

        case "googleads":
            properties.setProperty("InitiateOAuth", "REFRESH");
            properties.setProperty("DeveloperToken", headersMap.get("developertoken"));
            properties.setProperty("ClientCustomerId", headersMap.get("clientcustomerid"));
            properties.setProperty("OAuthClientId", headersMap.get("oauthclientid"));
            properties.setProperty("OAuthClientSecret", headersMap.get("oauthclientsecret"));
            properties.setProperty("OAuthAccessToken", headersMap.get("oauthaccesstoken"));
            properties.setProperty("managerid", headersMap.get("managerid"));
            return DriverManager.getConnection("jdbc:googleads:", properties);

        case "shopify":
            properties.setProperty("AuthScheme", "AccessToken");
            properties.setProperty("ShopUrl", headersMap.get("shopurl"));
            properties.setProperty("AccessToken", headersMap.get("accesstoken"));
            return DriverManager.getConnection("jdbc:shopify:", properties);

        case "makeshop":
            properties.setProperty("ProductsAccessCode", headersMap.get("productsaccesscode"));
            properties.setProperty("OrdersAccessCode", headersMap.get("ordersaccesscode"));
            properties.setProperty("MembersAccessCode", headersMap.get("membersaccesscode"));
            properties.setProperty("ShopId", headersMap.get("shopid"));
            return DriverManager.getConnection("jdbc:gmomakeshop:", properties);

        default:
            throw new IllegalArgumentException("Unsupported service: " + service);
        }
    }

    public List<String> getTables(Connection conn) {
        List<String> tables = new ArrayList<>();
        try {
            DatabaseMetaData tableMeta = conn.getMetaData();
            try (ResultSet rs = tableMeta.getTables(null, null, "%", null)) {
                while (rs.next()) {
                    tables.add(rs.getString("TABLE_NAME"));
                    System.out.println(rs.getString("TABLE_NAME"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return tables;
    }

    public Map<String, List<String>> getColumns(Connection conn) {
        Map<String, List<String>> data = new HashMap<>();
        List<String> columns = new ArrayList<>();
        try {
            DatabaseMetaData tableMeta = conn.getMetaData();
            try (ResultSet tableRs = tableMeta.getTables(null, null, "%", null)) {
                while (tableRs.next()) {
                    String table = tableRs.getString("TABLE_NAME");
                    try (ResultSet columnRs = tableMeta.getColumns(null, null, table, null)) {
                        while (columnRs.next()) {
                            columns.add(columnRs.getString("COLUMN_NAME"));
                        }
                    }
                    data.put(table, columns);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }
}