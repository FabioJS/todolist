package br.com.fabiodossantos.todolist.filter;

import java.io.IOException;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.fabiodossantos.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class FilterTaskAuth extends OncePerRequestFilter{

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Apenas valida os dados de autorização se for o end-point de inclusão de tarefas
        var serveletPath = request.getServletPath();
        if (!serveletPath.startsWith("/tasks/")) {
            filterChain.doFilter(request, response);
        } else {
            // Obtém os dados de autorização (Basic auth) passados na requisição
            var authorization = request.getHeader("Authorization");
            var authEncode = authorization.substring("Basic".length()).trim();
            // Decodifica os dados para um array de byte
            byte[] authDecode = Base64.getDecoder().decode(authEncode);
            // Pega o usuário e senha da requisição
            var authString = new String(authDecode);
            String username = authString.split(":")[0];
            String password = authString.split(":")[1];
    
            var user = this.userRepository.findByUsername(username);
            if (user == null) {
                // Indica que o usuário não está autorizado
                response.sendError(401);
            } else {
                // Verifica se a senha recebida para o usuário está correta
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (passwordVerify.verified) {
                    request.setAttribute("idUser", user.getId());
                    filterChain.doFilter(request, response);
                } else {
                    // Indica que o usuário não está autorizado
                    response.sendError(401);
                }
            }
        }
    }
}
