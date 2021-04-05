package co.unicauca.restaurant.server.infra;

import co.unicauca.restaurant.server.domain.services.DishService;
import co.unicauca.restaurant.commons.domain.Dish;
import co.unicauca.restaurant.commons.infra.Protocol;
import co.unicauca.restaurant.commons.infra.JsonError;
import co.unicauca.restaurant.commons.infra.Utilities;
import co.unicauca.restaurant.server.acces.DishFactory;
import co.unicauca.restaurant.server.acces.IDishRepository;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.List;

/**
 * Servidor Socket que está escuchando permanentemente solicitudes de los
 * clientes. Cada solicitud la atiende en un hilo de ejecución
 *
 * @author Libardo, Julio
 */
public class RestaurantOlineServerSocket implements Runnable {

    /**
     * Servicio de clientes
     */
    private final DishService service;
    /**
     * Server Socket, la orejita
     */
    private static ServerSocket ssock;
    /**
     * Socket por donde se hace la petición/respuesta
     */
    private static Socket socket;
    /**
     * Permite leer un flujo de datos del socket
     */
    private Scanner input;
    /**
     * Permite escribir un flujo de datos del scoket
     */
    private PrintStream output;
    /**
     * Puerto por donde escucha el server socket
     */
    private static final int PORT = Integer.parseInt(Utilities.loadProperty("server.port"));

    /**
     * Constructor
     */
    public RestaurantOlineServerSocket() {
        // Se hace la inyección de dependencia
        IDishRepository repository = DishFactory.getInstance().getRepository();
        service = new DishService(repository);
    }

    /**
     * Arranca el servidor y hace la estructura completa
     */
    public void start() {
        openPort();

        while (true) {
            waitToClient();
            throwThread();// atiende el socket en un hilo
        }
    }

    /**
     * Lanza el hilo
     */
    private static void throwThread() {
        new Thread(new RestaurantOlineServerSocket()).start();
    }

    /**
     * Instancia el server socket y abre el puerto respectivo
     */
    private static void openPort() {
        try {
            ssock = new ServerSocket(PORT);
            Logger.getLogger("Server").log(Level.INFO, "Servidor iniciado, escuchando por el puerto {0}", PORT);
        } catch (IOException ex) {
            Logger.getLogger(RestaurantOlineServerSocket.class.getName()).log(Level.SEVERE, "Error del server socket al abrir el puerto", ex);
        }
    }

    /**
     * Espera que el cliente se conecta y le devuelve un socket
     */
    private static void waitToClient() {
        try {
            socket = ssock.accept(); //cuando un cliente se conecta se devuelve socket
            Logger.getLogger("Socket").log(Level.INFO, "Socket conectado");
        } catch (IOException ex) {
            Logger.getLogger(RestaurantOlineServerSocket.class.getName()).log(Level.SEVERE, "Eror al abrir un socket", ex);
        }
    }

    /**
     * Cuerpo del hilo
     */
    @Override
    public void run() {
        try {
            createStreams();
            readStream();
            closeStream();

        } catch (IOException ex) {
            Logger.getLogger(RestaurantOlineServerSocket.class.getName()).log(Level.SEVERE, "Eror al leer el flujo", ex);
        }
    }

    /**
     * Crea los flujos con el socket
     *
     * @throws IOException
     */
    private void createStreams() throws IOException {
        output = new PrintStream(socket.getOutputStream());
        input = new Scanner(socket.getInputStream());
    }

    /**
     * Lee el flujo del socket
     */
    private void readStream() {
        if (input.hasNextLine()) {
            // Extrae el flujo que envió la aplicación cliente
            String request = input.nextLine();
            processRequest(request);

        } else {
            output.flush();
            String errorJson = generateErrorJson();
            output.println(errorJson);
        }
    }

    /**
     * Procesar la solicitud que proviene de la aplicación cliente
     *
     * @param requestJson petición que proviene del cliente socket en formato
     * json que viene de esta manera:
     * "{"resource":"customer","action":"get","parameters":[{"name":"id","value":"1"}]}"
     *
     */
    private void processRequest(String requestJson) {
        // Convertir la solicitud a objeto Protocol para poderlo procesar
        Gson gson = new Gson();
        Protocol protocolRequest = gson.fromJson(requestJson, Protocol.class);

        switch (protocolRequest.getResource()) {
            case "plate":
                if (protocolRequest.getAction().equals("get")) {
                    // Consultar un plato especial
                    processGetDish(protocolRequest);
                }

                if (protocolRequest.getAction().equals("post")) {
                    // Agregar un plato especial    
                    processPostDish(protocolRequest);

                }
                break;
        }

    }

    /**
     * Procesa la solicitud de consultar un plato especial
     *
     * @param protocolRequest Protocolo de la solicitud
     */
    private void processGetDish(Protocol protocolRequest) {
        // Extraer el id del primer parámetro
        String id = protocolRequest.getParameters().get(0).getValue();
        Dish plate = service.find(Integer.parseInt(id));
        if (plate == null) {
            String errorJson = generateNotFoundErrorJson();
            output.println(errorJson);
        } else {
            output.println(objectToJSON(plate));
        }
    }

    /**
     * Procesa la solicitud de agregar un plato especial
     *
     * @param protocolRequest Protocolo de la solicitud
     */
    private void processPostDish(Protocol protocolRequest) {
        Dish plate = new Dish();
        // Reconstruir el customer a partid de lo que viene en los parámetros
        plate.setDishID(Integer.parseInt(protocolRequest.getParameters().get(0).getValue()));
        plate.setDishName(protocolRequest.getParameters().get(1).getValue());
        plate.setDishDescription(protocolRequest.getParameters().get(2).getValue());
        plate.setDishPrice(Double.parseDouble(protocolRequest.getParameters().get(3).getValue()));
        plate.setDishImage(protocolRequest.getParametersIcon().get(4).getValue());                
        
        String response = service.save(plate);
        output.println(response);
    }

    /**
     * Genera un ErrorJson de plato no encontrado
     *
     * @return error en formato json
     */
    private String generateNotFoundErrorJson() {
        List<JsonError> errors = new ArrayList<>();
        JsonError error = new JsonError();
        error.setCode("404");
        error.setError("NOT_FOUND");
        error.setMessage("Plato no encontrado. Id no existe");
        errors.add(error);

        Gson gson = new Gson();
        String errorsJson = gson.toJson(errors);

        return errorsJson;
    }

    /**
     * Genera un ErrorJson genérico
     *
     * @return error en formato json
     */
    private String generateErrorJson() {
        List<JsonError> errors = new ArrayList<>();
        JsonError error = new JsonError();
        error.setCode("400");
        error.setError("BAD_REQUEST");
        error.setMessage("Error en la solicitud");
        errors.add(error);

        Gson gson = new Gson();
        String errorJson = gson.toJson(errors);

        return errorJson;
    }

    /**
     * Cierra los flujos de entrada y salida
     *
     * @throws IOException
     */
    private void closeStream() throws IOException {
        output.close();
        input.close();
        socket.close();
    }

    /**
     * Convierte el objeto Dish a json para que el servidor lo envie como
     * respuesta por el socket
     *
     * @param plate plato especial
     * @return plate en formato json
     */
    private String objectToJSON(Dish plate) {
        Gson gson = new Gson();
        String strObject = gson.toJson(plate);
        return strObject;
    }

}
