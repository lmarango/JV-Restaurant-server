
package co.unicauca.restaurant.server.acces;

import co.unicauca.restaurant.commons.infra.Utilities;

/**
 *
 * @author HP
 */
public class DishFactory {
    private static DishFactory instance;

    private DishFactory() {
    }

    /**
     * Clase singleton
     *
     * @return
     */
    public static DishFactory getInstance() {

        if (instance == null) {
            instance = new DishFactory();
        }
        return instance;

    }

    /**
     * Método que crea una instancia concreta de la jerarquia
     * IDishRepository
     *
     * @return una clase hija de la abstracción IRepositorioDish
     */
    public IDishRepository getRepository() {
        String type = Utilities.loadProperty("dish.repository");
        if (type.isEmpty()) {
            type = "default";
        }
        IDishRepository result = null;

        switch (type) {
            case "default":
                //result = new CustomerRepositoryImplArrays();
                break;
            case "mysql":
                result = new DishRepositoryImplMysql();
                break;
        }

        return result;

    }
}
