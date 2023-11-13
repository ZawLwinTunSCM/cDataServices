package gradle.web.common;

import java.util.Date;

import org.springframework.http.HttpStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FailedResponseEntity {
    private Date timeStamp;
    private int statusCode;
    private HttpStatus status;
    private String error;
}
