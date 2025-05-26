package dto;

import dto.Libros;
import dto.Ventas;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.12.v20230209-rNA", date="2025-05-26T00:56:20")
@StaticMetamodel(DetalleVenta.class)
public class DetalleVenta_ { 

    public static volatile SingularAttribute<DetalleVenta, BigDecimal> precioUnitario;
    public static volatile SingularAttribute<DetalleVenta, Integer> idDetalle;
    public static volatile SingularAttribute<DetalleVenta, Libros> idLibro;
    public static volatile SingularAttribute<DetalleVenta, Integer> cantidad;
    public static volatile SingularAttribute<DetalleVenta, Ventas> idVenta;

}