package pt.meta_II.tppd.servers.http.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import pt.meta_II.tppd.DbManager;
import pt.meta_II.tppd.servers.http.models.Despesa;

import java.util.ArrayList;

@RestController
public class DespesasController {

    DbManager manager;

    @GetMapping("/{grupo}/despesas")
    public ResponseEntity despesas(@PathVariable("grupo") String grupo, Authentication authentication) {

        manager = DbManager.getInstance();

        if(authentication.getName() != null) {
            if(!manager.verificaGrupo(grupo))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.parseMediaType("text/plain"))
                        .body("O grupo "+ grupo + " não existe...");

            if(!manager.verificaPertenceGrupo(authentication.getName(), grupo))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.parseMediaType("text/plain"))
                        .body("Utilizador não pertence ao grupo "+ grupo + "...");

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

            return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/plain"))
                    .body(retu);
        }
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Precisa de estar logado para aceder a este conteúdo");
    }

    @PostMapping("/{grupo}/eliminar")
    public ResponseEntity eliminar(@PathVariable("grupo") String grupo, @RequestParam(value = "id") int id, Authentication authentication){

        manager = DbManager.getInstance();

        if(authentication.getName() != null) {
            if(!manager.verificaGrupo(grupo))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.parseMediaType("text/plain"))
                        .body("O grupo "+ grupo + "não existe...");

            if(!manager.verificaPertenceGrupo(authentication.getName(), grupo))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.parseMediaType("text/plain"))
                        .body("Utilizador não pertence ao grupo "+ grupo + "...");

            if(!manager.verificaId(id, "despesa"))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.parseMediaType("text/plain"))
                        .body("Id da despesa inválido...");

            if(manager.eliminarDespesa(id))
                return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/plain"))
                        .body("Despesa eliminada com sucesso");
            else
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.parseMediaType("text/plain"))
                        .body("Ocorreu um erro ao eliminar a despesa. Por favor, tente novamente.");
        }
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Precisa de estar logado para aceder a este conteúdo");
    }

    @PostMapping("/{grupo}/adicionar")
    public ResponseEntity adicionar(@PathVariable("grupo") String grupo, @RequestParam(value = "despesa") Despesa d, Authentication authentication){

        manager = DbManager.getInstance();

        if(authentication.getName() != null) {
            if(!manager.verificaGrupo(grupo))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.parseMediaType("text/plain"))
                        .body("O grupo "+ grupo + "não existe...");

            if(!manager.verificaPertenceGrupo(authentication.getName(), grupo))
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.parseMediaType("text/plain"))
                        .body("Utilizador não pertence ao grupo "+ grupo + "...");

            if(!d.verificaData()){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.parseMediaType("text/plain"))
                        .body("Data inválida...");
            }

            int id = manager.adicionaDespesa(authentication.getName(),grupo,d.getValor(),d.getData(),d.getQuemPagou(),d.getDescricao());

            if(id<0)
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.parseMediaType("text/plain"))
                        .body("Ocorreu um erro ao adicionar a despesa. Por favor, tente novamente.");

            StringBuilder quemNaodeu = new StringBuilder("\nNo entanto :D, Email(s) inválido(s):\n");

            boolean falhou = false;
            for (String email_partilha: d.getPartilhas()) {
                if(!manager.verificaPertenceGrupo(email_partilha,grupo)||!manager.adicionaDespesaPartilhada(""+id, email_partilha)){
                    quemNaodeu.append("\n - ").append(email_partilha);
                    falhou = true;
                }
            }
            return ResponseEntity.ok().contentType(MediaType.parseMediaType("text/plain"))
                        .body("\nDespesa adicionada com sucesso!" + (falhou? quemNaodeu:"\nPartilhado com todos!"));

        }
        else return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Precisa de estar logado para aceder a este conteúdo");
    }

}
