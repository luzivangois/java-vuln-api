package com.advocacia.api.controllers;

import com.advocacia.api.services.IArchiveService;
import com.advocacia.api.domain.archive.Archive;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ArchiveController {

    final IArchiveService archiveService;

    public ArchiveController(IArchiveService archiveService) {
        this.archiveService = archiveService;
    }

    @PostMapping("/sendfile")
    public ResponseEntity<Object> uploadFile(@RequestParam(name = "file") MultipartFile archive, @RequestParam(name = "name") String name){
        if (archiveService.archiveExists(name)) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Arquivo já existe no servidor!");
        }

        Archive file = archiveService.sendDocument(archive, name);
        if(file != null){
            Map<String, String> response = new HashMap<>();
            response.put("Id do Arquivo", file.getId());
            response.put("Status", "Sucesso!");
            response.put("Nome do arquivo:", file.getName());
            response.put("Diretório:", file.getLinkArchive());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Extensão Inválida!");
    }

    @GetMapping()
    public ResponseEntity<Object> findByPath(@RequestBody @Valid String path){
        return ResponseEntity.status(HttpStatus.OK).body(archiveService.findByPath(path));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findById(@PathVariable(value = "id") String id){
        Optional<Archive> file = archiveService.findById(id);
        return file.<ResponseEntity<Object>>map(value -> ResponseEntity.status(HttpStatus.OK).body(value)).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Arquivo não encontrado!"));
    }

    @GetMapping("/name")
    public ResponseEntity<Object> readDoc(@RequestParam(name = "name") String name){
        String response = archiveService.readArchive(name);
        if (response.startsWith("java.io.IOException")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteById(@PathVariable(value = "id") String id){
        Optional<Archive> file = archiveService.findById(id);
        if(file.isEmpty()){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Arquivo não encontrado!");
        }
        archiveService.deleteById(id);
        return ResponseEntity.status(HttpStatus.OK).body("Arquivo deletado com sucesso!");
    }
}
