package pt.meta_II.tppd.servers.http.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.meta_II.tppd.DbManager;

import java.util.ArrayList;


@RestController
public class DBController {
    DbManager manager = DbManager.getInstance();

    @PostMapping("/register")
    public ResponseEntity register(@RequestParam(value = "email") String email, @RequestParam(value = "nome") String nome,
                                   @RequestParam(value = "telefone") int telefone, @RequestParam(value = "password") String password) {

        manager =  DbManager.getInstance();

        if (manager.verificaEmail(email)) {
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


    @GetMapping("/grupos")
    public ResponseEntity grupos(Authentication authentication) {

        manager =  DbManager.getInstance();

        if(authentication.getName() != null) {

            String lista = manager.listaGrupos(authentication.getName());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/plain"))
                    .body("Lista de grupos: " + (lista.isEmpty() ? "\nNão pertence a nenhum grupo.." :lista));
        }
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Content not found");
    }

    @GetMapping("/despesas/{grupo}")
    public ResponseEntity despesas(@PathVariable("grupo") String grupo, Authentication authentication) {

        manager =  DbManager.getInstance();

        if(authentication.getName() != null) {
            if(!manager.verificaGrupo(grupo))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.parseMediaType("text/plain"))
                        .body("O grupo "+ grupo + "não existe...");

            if(!manager.verificaPertenceGrupo(authentication.getName(), grupo))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.parseMediaType("text/plain"))
                        .body("Não pertence ao grupo "+ grupo + "...");

            ArrayList<String> despesas = manager.listaDespesas(grupo);

            // Verificar se existem despesas
            if (despesas.isEmpty())
                return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/plain"))
                    .body("Histórico de despesas do grupo " + grupo + ":\nNão há despesas a listar...");

            // Ordenar despesas cronologicamente
            despesas.sort((d1, d2) -> {
                String[] detalhes1 = d1.split(";");
                String[] detalhes2 = d2.split(";");
                String data1 = detalhes1[0]; // Supõe que a data está na posição 0
                String data2 = detalhes2[0];

                return data1.compareTo(data2); // Ordenação ascendente
            });

            // Construir string com o histórico
            StringBuilder retu = new StringBuilder("\nHistórico de despesas do grupo " + grupo + ":\n");

            for (String despesa : despesas) {
                retu.append(despesa).append("\n");
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("text/plain"))
                    .body(retu);
        }
        else return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Content not found");
    }
}
