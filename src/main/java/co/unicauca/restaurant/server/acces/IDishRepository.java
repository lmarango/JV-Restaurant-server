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
     * @return cadena confirmando si la actualización fue exitosa o no.
     */
    public String save(Dish newDish);
    /**
     * Actualiza un Plato(Dish) 
     * @param newDish objeto de tipo Dish 
     * @return  booleano confirmando si la actualización fue exitosa o no.
     */
    public boolean update(Dish newDish);
    /**
     * Busca un Plato(Dish) por su codigo
     * @param id identificador del plato
     * @return  objeto de tipo Dish 
     */
    public Dish find(int id);
    /**
     * Lista los Platos(Dish)
     * @return lista de Dish(platos) 
     */
    public List<Dish> list();
}
