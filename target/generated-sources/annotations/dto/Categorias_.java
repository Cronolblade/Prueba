package dto;

import dto.Libros;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.12.v20230209-rNA", date="2025-05-26T00:56:20")
@StaticMetamodel(Categorias.class)
public class Categorias_ { 

    public static volatile SingularAttribute<Categorias, String> descripcion;
    public static volatile SingularAttribute<Categorias, Integer> idCategoria;
    public static volatile CollectionAttribute<Categorias, Libros> librosCollection;
    public static volatile SingularAttribute<Categorias, String> nombreCategoria;

}