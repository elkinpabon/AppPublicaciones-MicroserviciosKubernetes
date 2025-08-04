package ms_catalogo.listener;

import java.util.Map;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import ms_catalogo.dto.ArticuloCientificoDto;
import ms_catalogo.dto.LibroDto;
import ms_catalogo.service.CatalogoService;

@Component
public class CatalogoListener {

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private CatalogoService catalogoService;

    @RabbitListener(queues = "catalogo.queue")
    public void recibirDatosCatalogo(String mensajeJson) {
        try {
            System.out.println("Mensaje recibido en catalogo.queue: " + mensajeJson);
            
            Map<String, Object> mensaje = objectMapper.readValue(mensajeJson, Map.class);
            
            String tipo = (String) mensaje.get("tipo");
            Object datos = mensaje.get("datos");
            Long timestamp = null;
            
            if (mensaje.get("timestamp") != null) {
                timestamp = ((Number) mensaje.get("timestamp")).longValue();
            }
            
            if ("LIBRO".equals(tipo)) {
                procesarYGuardarLibro(datos, timestamp);
            } else if ("ARTICULO".equals(tipo)) {
                procesarYGuardarArticulo(datos, timestamp);
            } else {
                System.out.println("Tipo de datos no reconocido: " + tipo);
            }
            
        } catch (Exception e) {
            System.err.println("Error procesando datos de catalogo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void procesarYGuardarLibro(Object datosLibro, Long timestamp) {
        try {
            Map<String, Object> libroData = (Map<String, Object>) datosLibro;
            
            LibroDto libroDto = new LibroDto();
            libroDto.setCodigo(generateCodigoLibro(libroData));
            libroDto.setTitulo((String) libroData.get("titulo"));
            libroDto.setAnioPublicacion(getIntValue(libroData.get("anioPublicacion")));
            libroDto.setEditorial((String) libroData.get("editorial"));
            libroDto.setResumen((String) libroData.get("resumen"));
            libroDto.setIsbn((String) libroData.get("isbn"));
            libroDto.setNumeroPaginas(getIntValue(libroData.get("numeroPaginas")));
            
            catalogoService.registrarLibro(libroDto);
            
            System.out.println("Libro guardado en catalogo BD - Codigo: " + libroDto.getCodigo() + 
                             " Titulo: " + libroDto.getTitulo() + 
                             " Autor: " + libroData.get("autorNombre"));
            
        } catch (Exception e) {
            System.err.println("Error procesando libro: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void procesarYGuardarArticulo(Object datosArticulo, Long timestamp) {
        try {
            Map<String, Object> articuloData = (Map<String, Object>) datosArticulo;
            
            ArticuloCientificoDto articuloDto = new ArticuloCientificoDto();
            articuloDto.setCodigo(generateCodigoArticulo(articuloData));
            articuloDto.setTitulo((String) articuloData.get("titulo"));
            articuloDto.setAnioPublicacion(getIntValue(articuloData.get("anioPublicacion")));
            articuloDto.setEditorial((String) articuloData.get("editorial"));
            articuloDto.setResumen((String) articuloData.get("resumen"));
            articuloDto.setIsbn((String) articuloData.get("isbn"));
            articuloDto.setAreaInvestigacion((String) articuloData.get("areaInvestigacion"));
            
            catalogoService.registrarArticulo(articuloDto);
            
            System.out.println("Articulo guardado en catalogo BD - Codigo: " + articuloDto.getCodigo() + 
                             " Titulo: " + articuloDto.getTitulo() + 
                             " Autor: " + articuloData.get("autorNombre"));
            
        } catch (Exception e) {
            System.err.println("Error procesando articulo: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private String generateCodigoLibro(Map<String, Object> libroData) {
        String titulo = (String) libroData.get("titulo");
        String codigo = "LIB-" + System.currentTimeMillis();
        if (titulo != null && titulo.length() > 3) {
            codigo = "LIB-" + titulo.substring(0, 3).toUpperCase() + "-" + System.currentTimeMillis();
        }
        return codigo;
    }
    
    private String generateCodigoArticulo(Map<String, Object> articuloData) {
        String titulo = (String) articuloData.get("titulo");
        String codigo = "ART-" + System.currentTimeMillis();
        if (titulo != null && titulo.length() > 3) {
            codigo = "ART-" + titulo.substring(0, 3).toUpperCase() + "-" + System.currentTimeMillis();
        }
        return codigo;
    }
    
    private int getIntValue(Object value) {
        if (value == null) return 0;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Number) return ((Number) value).intValue();
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
}