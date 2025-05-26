package dto;

import dto.Clientes;
import dto.DetalleVenta;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.CollectionAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.7.12.v20230209-rNA", date="2025-05-25T22:39:40")
@StaticMetamodel(Ventas.class)
public class Ventas_ { 

    public static volatile SingularAttribute<Ventas, BigDecimal> total;
    public static volatile SingularAttribute<Ventas, Clientes> idCliente;
    public static volatile SingularAttribute<Ventas, Integer> idVenta;
    public static volatile SingularAttribute<Ventas, Date> fechaVenta;
    public static volatile CollectionAttribute<Ventas, DetalleVenta> detalleVentaCollection;

}