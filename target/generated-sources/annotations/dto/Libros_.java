package dto;

import dto.Autores;
import dto.Categorias;
import dto.DetalleVenta;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.12.v20230209-rNA", date="2025-05-25T23:36:01")
@StaticMetamodel(Libros.class)
public class Libros_ { 

    public static volatile SingularAttribute<Libros, BigDecimal> precio;
    public static volatile SingularAttribute<Libros, Integer> idLibro;
    public static volatile SingularAttribute<Libros, Autores> idAutor;
    public static volatile SingularAttribute<Libros, String> isbn;
    public static volatile SingularAttribute<Libros, String> titulo;
    public static volatile SingularAttribute<Libros, Date> anioPublicacion;
    public static volatile SingularAttribute<Libros, Categorias> idCategoria;
    public static volatile CollectionAttribute<Libros, DetalleVenta> detalleVentaCollection;

}