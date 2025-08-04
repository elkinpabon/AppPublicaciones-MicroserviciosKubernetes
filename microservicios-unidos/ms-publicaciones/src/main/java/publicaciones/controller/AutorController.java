package publicaciones.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import publicaciones.dto.AutorDto;
import publicaciones.dto.ResponseDto;
import publicaciones.service.AutorService;

@RestController
@RequestMapping("/api/autores")
@CrossOrigin(origins = "*")
public class AutorController {
    
    @Autowired
    private AutorService autorService;
    
    @GetMapping
    public ResponseEntity<List<AutorDto>> getAllAutores() {
        List<AutorDto> autores = autorService.getAllAutores();
        return ResponseEntity.ok(autores);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<AutorDto> getAutorById(@PathVariable Long id) {
        return autorService.getAutorById(id)
                .map(autor -> ResponseEntity.ok(autor))
                .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<ResponseDto> createAutor(@RequestBody AutorDto autorDto) {
        try {
            AutorDto createdAutor = autorService.createAutor(autorDto);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ResponseDto("Autor creado exitosamente", "ID: " + createdAutor.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto("Error al crear autor: " + e.getMessage(), null));
        }
    }
    
    @PutMapping("/{id}")
    public ResponseEntity<ResponseDto> updateAutor(@PathVariable Long id, @RequestBody AutorDto autorDto) {
        try {
            AutorDto updatedAutor = autorService.updateAutor(id, autorDto);
            return ResponseEntity.ok(new ResponseDto("Autor actualizado exitosamente", "ID: " + updatedAutor.getId()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto("Error al actualizar autor: " + e.getMessage(), null));
        }
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<ResponseDto> deleteAutor(@PathVariable Long id) {
        try {
            autorService.deleteAutor(id);
            return ResponseEntity.ok(new ResponseDto("Autor eliminado exitosamente", "ID: " + id));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ResponseDto("Error al eliminar autor: " + e.getMessage(), null));
        }
    }
}