package co.unicauca.restaurant.server.domain.services;

import co.unicauca.restaurant.commons.domain.Dish;
import co.unicauca.restaurant.server.acces.IDishRepository;
import co.unicauca.restaurant.commons.infra.JsonError;
import co.unicauca.restaurant.commons.infra.SubjectDish;
import co.unicauca.restaurant.commons.infra.Utilities;
import co.unicauca.restaurant.server.acces.DishFactory;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;

/**
 *
 * @author HP
 */
public class DishService extends SubjectDish{
        /**
     * Repositorio de platos especiales
     */
    IDishRepository repo;
    
    /**
     * Constructor parametrizado. Hace inyeccion de dependencias
     *
     * @param repo repositorio de tipo ICustomerRepository
     */
    public DishService(IDishRepository repo) {
        this.repo = repo;
    }

    public DishService() {
        repo = DishFactory.getInstance().getRepository();
    }

    /**
     * Buscar un plato especial
     *
     * @param id identificador del plato
     * @return objeto tipo Dish
     */
    public Dish find(int id) {
        return repo.find(id);
    }

    /**
     * Crea un nuevo plato. Aplica validaciones de negocio
     *
     * @param newDish nuevo plato especial
     * @return devuelve el id del plato creado
     */
    public String save(Dish newDish){
        List<JsonError> errors = new ArrayList<>();
  
        // Validaciones y reglas de negocio
        if (String.valueOf(newDish.getDishID()).isEmpty() || newDish.getDishName().isEmpty()
            || newDish.getDishDescription().isEmpty() || String.valueOf(newDish.getDishPrice()).isEmpty()) {
           errors.add(new JsonError("400", "BAD_REQUEST","id, nombre, descripcion, precio e imagen son obligatorios. "));
        }        
        
        if(!Utilities.isNumeric(String.valueOf(newDish.getDishPrice()))){
            errors.add(new JsonError("400", "BAD_REQUEST","El precio debe contener sólo dígitos "));
           
        }
        // Que no esté repetido
        
        Dish DishSearched = this.find(newDish.getDishID());
        if (DishSearched != null){
            errors.add(new JsonError("400", "BAD_REQUEST","El plato ya existe. "));
        }
        
       if (!errors.isEmpty()) {
            Gson gson = new Gson();
            String errorsJson = gson.toJson(errors);
            return errorsJson;
        }             
        return repo.save(newDish);
    }
}
