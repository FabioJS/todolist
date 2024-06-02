package br.com.fabiodossantos.todolist.user;

import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.fabiodossantos.todolist.utils.Utils;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;

@RestController
@RequestMapping("/users")
public class UserController {
    
    @Autowired
    private IUserRepository userRepository;

    @PostMapping("/")
    public ResponseEntity create(@RequestBody UserModel userModel) {
        // Verifica existência do usuário (busca pelo username)
        var user = this.userRepository.findByUsername(userModel.getUsername());
        if (user != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário já existe!");
        }
        // Adiciona criptografia na senha do usuário
        var passwordHashred = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
        userModel.setPassword(passwordHashred);
        // Cria o usuário na base de dados e retorna como resposta da requisição
        var userCreated = this.userRepository.save(userModel);
        return ResponseEntity.status(HttpStatus.OK).body(userCreated);
    }

    @GetMapping("/{id}")
    public ResponseEntity listById(@RequestBody UserModel userModel, @PathVariable UUID id) {
        // Verifica existência do usuário (busca pelo id)
        var user = this.userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário não encontrado!");
        }
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody UserModel userModel, @PathVariable UUID id) {
        // Verifica existência do usuário (busca pelo id)
        var user = this.userRepository.findById(id).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Usuário não encontrado!");
        }
       // Atualiza os campos que foram passados na requisição
        Utils.copyNonNullProperties(userModel, user);
        // Atualiza os dados da tarefa
        var userUpdated = this.userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK).body(userUpdated);
    }
}
