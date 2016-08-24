package com.mybatis.handler;

import java.nio.file.AccessDeniedException;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.apache.tomcat.util.http.fileupload.FileUploadBase.SizeLimitExceededException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartException;


@ControllerAdvice
@RestController
public class GlobalExceptionHandler {
    protected final Logger logger = Logger.getLogger(this.getClass());

    /**
     * DataAccess exception을 처리한다.
     * @param e
     * @return
     */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<?> dataAccessException(DataAccessException e) {
        String errorMessage = "";

        SQLException sqle = (SQLException)e.getRootCause();

        logger.error("##### DataAccessException ");
        logger.error("#### error code [" + sqle.getErrorCode() +"]");
        logger.error("#### message [" + sqle.getMessage() +"]");
        errorMessage = "처리도중에 문제가 발생하였습니다.";

        return new ResponseEntity<>(errorMessage,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * BaseException, DataAccessExceptio을 제외한 Exception을 처리한다.
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<?> exception(Exception e) {
        String errorMessage = e.getMessage();
        e.printStackTrace();
        logger.error("##### Exception #####");
        logger.error("#### message [" + errorMessage + "]");

        errorMessage = "처리도중에 문제가 발생하였습니다.";

        return new ResponseEntity<>(errorMessage,HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * MultipartException을 처리한다.
     * @param e
     * @return
     */
    @ExceptionHandler(value = MultipartException.class)
    public ResponseEntity<?> multipartException(MultipartException e) {
        String errorMessage = e.getMessage();
        e.printStackTrace();
        logger.error("##### MultipartException #####");

        if (e.getRootCause() instanceof SizeLimitExceededException) {
            // file size가 초과한경우
            SizeLimitExceededException me = (SizeLimitExceededException) e.getRootCause();
            logger.error("#### message [" + me.getMessage() + "]");
            logger.error("#### MaxUploadSize [" + me.getPermittedSize() + "]");
            errorMessage = "업로드 가능한 파일 용량을 초과하였습니다. \n";
            errorMessage += "최대 파일 크기  [ " + (me.getPermittedSize() / 1024 ) +"KB ] ";

            return new ResponseEntity<>(errorMessage,HttpStatus.PAYLOAD_TOO_LARGE);
        } else {
            logger.error("#### message [" + errorMessage + "]");
            errorMessage = "파일업로드중 문제가 생겼습니다.";

            return new ResponseEntity<>(errorMessage,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * AccessDeniedException을 처리한다.
     * @param e
     * @return
     */
    @ExceptionHandler(value = AccessDeniedException.class)
    public ResponseEntity<?> accessDeniedException(Exception e) {
        String errorMessage = e.getMessage();
        e.printStackTrace();
        logger.error("##### Exception #####");
        logger.error("#### message [" + errorMessage + "]");

        return new ResponseEntity<>(errorMessage,HttpStatus.FORBIDDEN);
    }
}
