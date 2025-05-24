//package gov.iti.jet.ewd.ecom.security;
//
//import io.jsonwebtoken.JwtParser;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.JwtBuilder;
//import io.jsonwebtoken.Jws;
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.security.Keys;
//import jakarta.annotation.PostConstruct;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Component;
//
//import javax.crypto.SecretKey;
//import java.security.Key;
//import java.util.Base64;
//import java.util.Date;
//
//@Component
//public class JwtTokenProvider {
//
//   @Value("${jwt.secret}")
//   private String secret;
//
//   private Key secretKey;
//   private JwtParser jwtParser;
//
//   private final long validityInMs = 3600000; // 1 hour
//
//   @PostConstruct
//   public void init() {
//       byte[] decodedKey = Base64.getDecoder().decode(secret);
//       this.secretKey = Keys.hmacShaKeyFor(decodedKey);
//       this.jwtParser = Jwts.parser().verifyWith((SecretKey) secretKey).build();
//   }
//
//   public String createToken(String username) {
//       Date now = new Date();
//       Date expiry = new Date(now.getTime() + validityInMs);
//
//       JwtBuilder builder = Jwts.builder();
//       return builder
//               .subject(username)
//               .issuedAt(now)
//               .expiration(expiry)
//               .signWith(secretKey)
//               .compact();
//   }
//
//   public String extractUsername(String token) {
//       Jws<Claims> jws = jwtParser.parseSignedClaims(token);
//       return jws.getPayload().getSubject();
//   }
//
//   public boolean validateToken(String token) {
//       try {
//           jwtParser.parseSignedClaims(token);
//           return true;
//       } catch (Exception e) {
//           return false;
//       }
//   }
//}
