package ms_catalogo.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "articulos")
public class ArticuloCientifico {

    @Id
    private String codigo;
    private String titulo;
    private int anioPublicacion;
    private String editorial;
    private String resumen;
    private String isbn;
    private String areaInvestigacion;

    // Getters y setters
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public int getAnioPublicacion() { return anioPublicacion; }
    public void setAnioPublicacion(int anioPublicacion) { this.anioPublicacion = anioPublicacion; }
    public String getEditorial() { return editorial; }
    public void setEditorial(String editorial) { this.editorial = editorial; }
    public String getResumen() { return resumen; }
    public void setResumen(String resumen) { this.resumen = resumen; }
    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public String getAreaInvestigacion() { return areaInvestigacion; }
    public void setAreaInvestigacion(String areaInvestigacion) { this.areaInvestigacion = areaInvestigacion; }
}

