/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package co.unicauca.restaurant.server.acces;

import co.unicauca.restaurant.commons.domain.Dish;
import java.util.List;

/**
 *
 * @author HP
 */
public interface IDishRepository {
    /**
     * Guarda un nuevo Plato(Dish) 
     * @param newDish objeto de tipo Dish 
     * @return  booleano confirmando si la actualización fue exitosa o no.
     */
    boolean save(Dish newDish);
    /**
     * Actualiza un Plato(Dish) 
     * @param newDish objeto de tipo Dish 
     * @return  booleano confirmando si la actualización fue exitosa o no.
     */
    boolean update(Dish newDish);
    /**
     * Busca un Plato(Dish) por su codigo
     * @param id identificador del plato
     * @return  objeto de tipo Dish 
     */
    Dish find(int id);
    /**
     * Lista los Platos(Dish)
     * @return lista de Dish(platos) 
     */
    List<Dish> list();
}
