package med.voll.api.infra.security;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

import med.voll.api.usuario.Usuario;

@Service
public class TokenService {
	
	@Value("${api.security.token.secret}")
	private String secret;

	public String gerarToken(Usuario usuario) {
		System.out.println(secret);
		try {
		    var algoritmo = Algorithm.HMAC256(secret);
		    return JWT.create()
		        .withIssuer("API voll.med")
		        .withSubject(usuario.getLogin())
		        .withExpiresAt(dataExpiracao())
		   //     .withClaim("id", usuario.getId()) caso queira passar mais informaçoes
		        .sign(algoritmo);
		} catch (JWTCreationException exception){
		   throw new RuntimeException("erro ao gerar token jwt", exception);
		}
	}

	private Instant dataExpiracao() {
		return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
	}
	
	public String getSubjet(String tokenJWT) {
		try {
			var algoritmo = Algorithm.HMAC256(secret);
		  return JWT.require(algoritmo)
		        .withIssuer("API voll.med")
		        .build()
		        .verify(tokenJWT).getSubject();
		        
		} catch (JWTVerificationException exception){
			throw new RuntimeException("Token jwt invalido ou expirado");
		}
	}
}
