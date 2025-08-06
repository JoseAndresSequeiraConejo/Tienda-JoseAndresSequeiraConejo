package com.tienda.service;

import com.tienda.domain.Item;
import java.util.List;

public interface ItemService {
    //Se usa para tener en una session de memoria la información del carrito de compras

    public List<Item> gets();

    //Se recupera el registro que tiene el idItem pasado por parámetro
    //si no existe en la tabla se retorna null
    public Item get(Item item);

    //Se elimina el registro que tiene el idItem pasado por parámetro
    public void delete(Item item);

    //Si el objeto item tiene un idItem que existe en la tabla item
    //El registro de actualiza con la nueva información
    //Si el idItem NO existe en la tabla, se crea el registro con esa información
    public void save(Item item);

    public void update(Item item);

    public void facturar();

    public double getTotal();
}
