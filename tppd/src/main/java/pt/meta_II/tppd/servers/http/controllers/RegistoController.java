package pt.meta_II.tppd.servers.http.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.meta_II.tppd.DbManager;
import pt.meta_II.tppd.servers.http.tools.Verificacoes;


@RestController
public class RegistoController {

    DbManager manager;
    Verificacoes verifica =  new Verificacoes();

    @PostMapping("/register")
    public ResponseEntity register(@RequestParam(value = "email") String email, @RequestParam(value = "nome") String nome,
                                   @RequestParam(value = "telefone") int telefone, @RequestParam(value = "password") String password) {

        manager =  DbManager.getInstance();
        //-------formatos
        if (!verifica.verificaEmail(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.parseMediaType("text/plain"))
                    .body("Email inválido.... Formato <a..z1...9>@<a...z>.<a...z>");
        }
        if (!verifica.verificaTelefone(""+telefone)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.parseMediaType("text/plain"))
                    .body("Telefone inválido....");
        }
        //----existentes
        if (manager.verificaEmail(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.parseMediaType("text/plain"))
                    .body("Email já existente na Base de Dados.");
        }
        if ( manager.verificaTelefone(telefone)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.parseMediaType("text/plain"))
                    .body("Telefone já existente na Base de Dados.");
        }

        if ( manager.adicionaRegisto(email, nome, telefone, password))
            return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/plain"))
                    .body("O seu registo foi criado com sucesso!");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.parseMediaType("text/plain"))
                .body("Ocorreu um erro ao criar o seu registo. Por favor, tente novamente.");

    }
}
