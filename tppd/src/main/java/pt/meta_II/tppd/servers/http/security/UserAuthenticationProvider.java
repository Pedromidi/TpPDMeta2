package pt.meta_II.tppd.servers.http.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import pt.meta_II.tppd.DbManager;

@Component
public class UserAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        DbManager manager = DbManager.getInstance();

        if(manager.verificaEmail(username) && manager.verificaPassword(username,password)){
             return new UsernamePasswordAuthenticationToken(username, password, null);
        }else {
            throw new BadCredentialsException("Não foi encontrado este utilizador");
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }
}
