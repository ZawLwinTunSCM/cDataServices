package gradle.web.controller;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import gradle.web.common.FailedResponseEntity;
import gradle.web.common.SuccessResponseEntity;
import gradle.web.service.CdataConnectionCheckService;

@RestController
public class CdataController {
    @Autowired
    CdataConnectionCheckService checkService;

    @GetMapping("/connection_check")
    public ResponseEntity<?> checkConnection(@RequestHeader HttpHeaders headers)
            throws ClassNotFoundException, SQLException {
        Map<String, String> headersMap = checkService.getHeaders(headers);
        String service = headersMap.get("service");
        if (service == null || service.isEmpty()) {
            return badRequestResponse("Service is required.");
        }
        if (!checkService.checkServiceType(service)) {
            return badRequestResponse("Service " + service + " is unavailable.");
        }
        String parameter = checkService.checkParameters(service, headersMap);
        if (parameter != null) {
            return badRequestResponse(parameter + " is required.");
        }
        if (checkService.checkConnection(service, headersMap)) {
            return okResponse("Connection Success!", "");
        } else {
            return badRequestResponse("Bad Credential.");
        }
    }

    @GetMapping("/report/tables")
    public ResponseEntity<?> getTables(@RequestHeader HttpHeaders headers) throws ClassNotFoundException, SQLException {
        Map<String, String> headersMap = getHeadersMap(headers);
        String service = headersMap.get("service");
        return okResponse("Retrieve Table List Successully.",
                checkService.getTables(checkService.getConnection(service, headersMap)));

    }

    @GetMapping("/report/columns")
    public ResponseEntity<?> getColumns(@RequestHeader HttpHeaders headers)
            throws ClassNotFoundException, SQLException {
        Map<String, String> headersMap = getHeadersMap(headers);
        String service = headersMap.get("service");
        return okResponse("Retrieve Table List Successully.",
                checkService.getColumns(checkService.getConnection(service, headersMap)));
    }

    private Map<String, String> getHeadersMap(HttpHeaders headers) {
        return checkService.getHeaders(headers);
    }

    private ResponseEntity<?> badRequestResponse(String message) {
        return ResponseEntity.badRequest().body(
                new FailedResponseEntity(new Date(), HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, message));
    }

    private ResponseEntity<?> okResponse(String message, Object data) {
        return ResponseEntity.ok()
                .body(new SuccessResponseEntity(new Date(), HttpStatus.OK.value(), HttpStatus.OK, message, data));
    }
}