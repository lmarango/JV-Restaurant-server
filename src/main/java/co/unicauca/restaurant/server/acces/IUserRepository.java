package co.unicauca.restaurant.server.acces;

import co.unicauca.restaurant.commons.domain.User;

/**
 *
 * @author Luis Arango
 */
public interface IUserRepository {
    /**
     * Metodo encargado de buscar un usuario
     * @param prmUserName userName a buscar
     * @return objeto de tipo User
     */
    public User findUser(String prmUserName);
    /**
     * Metodo para crear un usuario 
     * @param prmObjUser Objeto usuario a crear
     * @return cadena de texto que contiene el userName de prmObjUser
     */
    public String createUser(User prmObjUser);
}
