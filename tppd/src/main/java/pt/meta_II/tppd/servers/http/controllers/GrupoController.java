package pt.meta_II.tppd.servers.http.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.meta_II.tppd.DbManager;

@RestController
public class GrupoController {

    DbManager manager;

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
}
