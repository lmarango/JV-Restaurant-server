package co.unicauca.restaurant.server.app;

import co.unicauca.restaurant.server.infra.RestaurantOlineServerSocket;


/**
 * Aplicación principal que lanza el servidor en un hilo
 * @author Libardo, Julio
 */
public class RestaurantOnlineApplication {
    public static void main(String args[]){
        RestaurantOlineServerSocket server = new RestaurantOlineServerSocket();
        server.start();
    }
}
