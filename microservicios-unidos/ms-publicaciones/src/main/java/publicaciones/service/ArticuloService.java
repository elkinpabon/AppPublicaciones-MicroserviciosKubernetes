package publicaciones.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import publicaciones.dto.ArticuloDto;
import publicaciones.model.Articulo;
import publicaciones.model.Autor;
import publicaciones.repository.ArticuloRepository;
import publicaciones.repository.AutorRepository;

@Service
public class ArticuloService {
    
    @Autowired
    private ArticuloRepository articuloRepository;
    
    @Autowired
    private AutorRepository autorRepository;
    
    public List<ArticuloDto> getAllArticulos() {
        return articuloRepository.findAll().stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public ArticuloDto getArticuloById(Long id) {
        return articuloRepository.findById(id)
                .map(this::convertToDto)
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));
    }
    
    public List<ArticuloDto> getArticulosByAutor(Long autorId) {
        return articuloRepository.findByAutorId(autorId).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<ArticuloDto> getArticulosByAreaInvestigacion(String areaInvestigacion) {
        return articuloRepository.findByAreaInvestigacion(areaInvestigacion).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public List<ArticuloDto> getArticulosByRevista(String revista) {
        return articuloRepository.findByRevista(revista).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    public ArticuloDto createArticulo(ArticuloDto articuloDto) {
        if (articuloDto.getDoi() != null && articuloRepository.existsByDoi(articuloDto.getDoi())) {
            throw new RuntimeException("Ya existe un artículo con este DOI");
        }
        
        Autor autor = autorRepository.findById(articuloDto.getAutorId())
                .orElseThrow(() -> new RuntimeException("Autor no encontrado"));
        
        Articulo articulo = convertToEntity(articuloDto);
        articulo.setAutor(autor);
        Articulo savedArticulo = articuloRepository.save(articulo);
        return convertToDto(savedArticulo);
    }
    
    public ArticuloDto updateArticulo(Long id, ArticuloDto articuloDto) {
        return articuloRepository.findById(id)
                .map(articulo -> {
                    Autor autor = autorRepository.findById(articuloDto.getAutorId())
                            .orElseThrow(() -> new RuntimeException("Autor no encontrado"));
                    
                    articulo.setTitulo(articuloDto.getTitulo());
                    articulo.setAnioPublicacion(articuloDto.getAnioPublicacion());
                    articulo.setEditorial(articuloDto.getEditorial());
                    articulo.setIsbn(articuloDto.getIsbn());
                    articulo.setResumen(articuloDto.getResumen());
                    articulo.setIdioma(articuloDto.getIdioma());
                    articulo.setRevista(articuloDto.getRevista());
                    articulo.setDoi(articuloDto.getDoi());
                    articulo.setAreaInvestigacion(articuloDto.getAreaInvestigacion());
                    articulo.setFechaPublicacion(articuloDto.getFechaPublicacion());
                    articulo.setAutor(autor);
                    return convertToDto(articuloRepository.save(articulo));
                })
                .orElseThrow(() -> new RuntimeException("Artículo no encontrado"));
    }
    
    public void deleteArticulo(Long id) {
        if (!articuloRepository.existsById(id)) {
            throw new RuntimeException("Artículo no encontrado");
        }
        articuloRepository.deleteById(id);
    }
    
    private ArticuloDto convertToDto(Articulo articulo) {
        return new ArticuloDto(
                articulo.getId(),
                articulo.getTitulo(),
                articulo.getAnioPublicacion(),
                articulo.getEditorial(),
                articulo.getIsbn(),
                articulo.getResumen(),
                articulo.getIdioma(),
                articulo.getRevista(),
                articulo.getDoi(),
                articulo.getAreaInvestigacion(),
                articulo.getFechaPublicacion(),
                articulo.getAutor().getId(),
                articulo.getAutor().getNombre() + " " + articulo.getAutor().getApellido()
        );
    }
    
    private Articulo convertToEntity(ArticuloDto articuloDto) {
        Articulo articulo = new Articulo();
        articulo.setTitulo(articuloDto.getTitulo());
        articulo.setAnioPublicacion(articuloDto.getAnioPublicacion());
        articulo.setEditorial(articuloDto.getEditorial());
        articulo.setIsbn(articuloDto.getIsbn());
        articulo.setResumen(articuloDto.getResumen());
        articulo.setIdioma(articuloDto.getIdioma());
        articulo.setRevista(articuloDto.getRevista());
        articulo.setDoi(articuloDto.getDoi());
        articulo.setAreaInvestigacion(articuloDto.getAreaInvestigacion());
        articulo.setFechaPublicacion(articuloDto.getFechaPublicacion());
        return articulo;
    }
}