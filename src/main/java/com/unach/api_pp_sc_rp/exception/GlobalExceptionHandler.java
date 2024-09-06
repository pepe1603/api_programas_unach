package com.unach.api_pp_sc_rp.exception;

import com.unach.api_pp_sc_rp.dto.CustomErrorResponse;
import com.unach.api_pp_sc_rp.dto.ResponseInformative;
import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {

    ///Mnejo de Excepciones para  JWT
    @ExceptionHandler(TokenInvalidException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ErrorResponse> handleTokenInvalidException(TokenInvalidException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Token Invalid",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ErrorResponse> handleTokenExpiredException(TokenExpiredException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Token Invalid",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(MalformedJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ResponseEntity<ResponseInformative> handleMalformedJwtException(MalformedJwtException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseInformative( ex.getMessage()));
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedJwtException(UnsupportedJwtException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Token Invalid",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }
    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ErrorResponse> handleExpiredJwtException(ExpiredJwtException ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.UNAUTHORIZED.value(),
                "Token Invalid or Expired: ",
                ex.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EmptyClaimsException.class)
    public ResponseEntity<ResponseInformative> handleEmptyClaimsException(EmptyClaimsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ResponseInformative( ex.getMessage()));
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ResponseInformative> handleIllegalException(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseInformative( ex.getMessage()));
    }

    //para menajo de exceptiones para Base de datos
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ResponseInformative> handleNotFoundEntity (EntityNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseInformative( ex.getMessage()));
    }

    //para Atuhentcation y regiuster
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseInformative> handleBadCredentialsException (BadCredentialsException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body( new ResponseInformative("credenciales No correctas"+ ex.getMessage()) );
    }
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ResponseInformative> handlerMaxSizeException(AuthException ex){
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(new ResponseInformative( ex.getMessage()) );
    }


    //Exceptiones io para Amanejo de Archivops

    @ExceptionHandler(FileNotFoundException.class)
    public ResponseEntity<ResponseInformative> handleFileNotFoundException (FileNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body( new ResponseInformative( ex.getMessage()) );
    }
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ResponseInformative> handleFileStorageException (FileStorageException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseInformative( ex.getMessage()));
    }


    @ExceptionHandler(FileException.class)
    public ResponseEntity<ResponseInformative> handleFileExceptionException(FileException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body( new ResponseInformative( e.getMessage()) );
    }

    @ExceptionHandler(Exception.class)
    public
    ResponseEntity<ResponseInformative> handleGeneralException(Exception e){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body( new ResponseInformative( e.getMessage()) );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ResponseInformative> handlerMaxSizeException(MaxUploadSizeExceededException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ResponseInformative(  "Verifica el tramaño de los archivos" +ex.getMessage()) );
    }

    @ExceptionHandler(ExportException.class)
    public ResponseEntity<CustomErrorResponse> handleExportException(ExportException ex){
        CustomErrorResponse errorResponse = new CustomErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Error To Export Resource",  ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }


    // Manejo de otras excepciones

}
