package br.edu.fiap.cartoes.security;
import io.smallrye.jwt.build.Jwt;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import br.edu.fiap.cartoes.dto.TokenResponse;
import br.edu.fiap.cartoes.entity.Usuario;

import java.time.Instant;

/**
 * Adaptador de emissão a implementar com io.smallrye.jwt.build.Jwt e HS256.
 * Verificação Bearer já é feita pelo Quarkus. Assinar com o mesmo segredo configurado.
 * Claims obrigatórios: iss, sub (email), iat, exp e usuarioId. Nunca incluir senha/hash.
 * Na migração Clean, criar porta EmissorToken para que o use case não conheça SmallRye.
 */
@ApplicationScoped
public class JwtService {
    private String segredo;
    private String emissor;
    private long duracao;


    public JwtService(
            @ConfigProperty(name="security.jwt.secret") String segredo,
            @ConfigProperty(name="security.jwt.issuer") String emissor,
            @ConfigProperty(name="security.jwt.expiration-seconds") long duracao
    ){
        this.segredo = segredo;
        this.emissor = emissor;
        this.duracao = duracao;
    }

    /** TODO emitir TokenResponse com Bearer, duração e expiração coerentes com o JWT. */
    public TokenResponse emitir(Usuario usuario) {
        long agora = Instant.now().getEpochSecond();
        long expiracao = agora + duracao;

        String token = Jwt.subject(usuario.getEmail())
                .issuer(emissor)
                .issuedAt(agora)
                .expiresAt(expiracao)
                .claim("usuarioId", usuario.getId())
                .claim("nome", usuario.getNome())
                .signWithSecret(segredo);
        return new TokenResponse(token, "Bearer", duracao, expiracao);
    }
}
