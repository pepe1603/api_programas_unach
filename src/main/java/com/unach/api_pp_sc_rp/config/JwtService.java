package com.unach.api_pp_sc_rp.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final Logger logger = LoggerFactory.getLogger(JwtService.class);

    private static final String MY_SECRET_KEY =  "YARvmNMmWXo6fKvM4o6nv/aUi9ryX38ZH+L1bkrnD1ObOQ8JAUmHCBq7Iy7otZcyAagBLHVKvvYaIpmMuxmARQ97jUVG16Jkpkp1wXOPsrF9zwew6TpczyHkHgX5EuLg2MeBuiT/qJACs1J0apruOOJCg/gOtkjB4c=";

    // Tiempo de expiración del token en milisegundos (2 horas)
    private static final long JWT_EXPIRATION_MS = 1000 * 60 * 60 * 2;
    /*iempo de expiración del token en milisegundos (5 minutos)
    * JWT_EXPIRATION_MS = 1000 * 60 * 5;
    */

    public String generateToken(UserDetails userDetails) {
        logger.debug("Generating token for user: {}", userDetails.getUsername());

        return generateToken(new HashMap<>(), userDetails);
    }




    public String generateToken(Map<String , Object> extractClaims, UserDetails userDetails) {
        return Jwts.builder().setClaims(extractClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + JWT_EXPIRATION_MS))
                .signWith(getSignInKey() , SignatureAlgorithm.HS256)//importamos la firma haseada en base64
                .compact();
    }
    // Obtiene el nombre de usuario del token
    public String getUserName(String token) {
        logger.debug("Parsing token to get username.");
        return getClaim(token, Claims::getSubject);//metodo_generico getClaim

    }
    // Método genérico para obtener un reclamo específico del token
    public <T> T getClaim(String token, Function<Claims,T> claimsResolver) {
        final Claims claims = getAllClaims(token);
        return  claimsResolver.apply(claims);

    }

    private Claims getAllClaims(String token ) {//se utiza_jwts para_usar la firma o secretKey y
        return Jwts
                .parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    // Generar_una clave_segura para firmar_los JWT
    private Key getSignInKey() {//convetimos_la firma_en base64
        byte [] keyBytes = Decoders.BASE64.decode( MY_SECRET_KEY) ;
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public boolean validarToken(String token, UserDetails userDetails) {
        // TODO Auto-generated method stub
        logger.debug("Validating token for user: {}", userDetails.getUsername());
        final String username =  getUserName(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    // Verifica si el token está expirado
    private boolean isTokenExpired(String token) { return getExpiration(token).before(new Date());	}
    // Obtiene la fecha de expiración del token
    public Date getExpiration(String token) { return getClaim(token, Claims::getExpiration);	}


}
