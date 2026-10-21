package br.edu.fiap.cartoes.security;
import io.quarkus.elytron.security.common.BcryptUtil;
import jakarta.enterprise.context.ApplicationScoped;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import br.edu.fiap.cartoes.exception.EtapaNaoImplementadaException;
/**
 * Adaptador de senha a implementar com BcryptUtil do Quarkus.
 * BCrypt é hash com salt, não criptografia reversível. O custo é logarítmico (2^custo),
 * não o número literal de repetições. Validar limite de 72 bytes UTF-8 e não logar senhas.
 */
@ApplicationScoped
public class SenhaBCrypt {
    private int custo;


    public SenhaBCrypt(
            @ConfigProperty(name="security.password.bcrypt-cost") int custo){
        this.custo = custo;
    }

    /** TODO usar BcryptUtil.bcryptHash com salt aleatório gerado pela biblioteca. */
    public String criarHash(String senha) {
        return BcryptUtil.bcryptHash(senha, custo);
    }
    /** TODO usar BcryptUtil.matches sem descriptografar, rehashar ou comparar Strings de hash. */
    public boolean confere(String senha, String hash) {
        return BcryptUtil.matches(senha, hash);
    }
}
