package pt.meta_II.tppd.server.http.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.meta_II.tppd.DbManager;
import pt.meta_II.tppd.server.http.Application;


@RestController
public class DBController {

    DbManager manager =  DbManager.getInstance();

    @PostMapping("/register")
    public ResponseEntity register(@RequestParam(value = "email") String email, @RequestParam(value = "nome") String nome,
                                   @RequestParam(value = "telefone") int telefone, @RequestParam(value = "password") String password) {
        if ( manager.verificaEmail(email)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.parseMediaType("text/plain"))
                    .body("Email já existente na Base de Dados.");
        }
        if ( manager.verificaTelefone(telefone)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .contentType(MediaType.parseMediaType("text/plain"))
                    .body("Telefone já existente na Base de Dados.");
        }

        if ( manager.adicionaRegisto(email, nome, telefone, password))
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/plain"))
                    .body("O seu registo foi criado com sucesso!");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .contentType(MediaType.parseMediaType("text/plain"))
                .body("Ocorreu um erro ao criar o seu registo. Por favor, tente novamente.");

    }


    @GetMapping("grupos/{email}")
    public ResponseEntity getImageLength(@PathVariable("email") String email) {

        String lista = manager.listaGrupos(email);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/plain"))
                .body("\nLista de grupos: " + (lista.equals("")? "\nNão pertence a nenhum grupo.." :lista));
    }


}
