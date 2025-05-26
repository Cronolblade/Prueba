package dto;

import dto.Libros;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.12.v20230209-rNA", date="2025-05-26T00:56:20")
@StaticMetamodel(Autores.class)
public class Autores_ { 

    public static volatile SingularAttribute<Autores, Integer> idAutor;
    public static volatile SingularAttribute<Autores, Date> fechaNacimiento;
    public static volatile SingularAttribute<Autores, String> apellido;
    public static volatile SingularAttribute<Autores, String> paisOrigen;
    public static volatile SingularAttribute<Autores, String> nombre;
    public static volatile CollectionAttribute<Autores, Libros> librosCollection;

}