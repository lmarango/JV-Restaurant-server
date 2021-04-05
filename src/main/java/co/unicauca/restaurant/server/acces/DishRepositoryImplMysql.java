
package co.unicauca.restaurant.server.acces;

import co.unicauca.restaurant.commons.domain.Dish;
import co.unicauca.restaurant.server.domain.services.DishService;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author HP
 */
public class DishRepositoryImplMysql implements IDishRepository{
    
    private Connection conn;
      
    public DishRepositoryImplMysql() {
        initDatabase();
    }
     private void initDatabase() {
        // SQL statement for creating a new table
        String sql = "CREATE TABLE IF NOT EXISTS Dish (\n"
                + "	dishId integer PRIMARY KEY,\n"
                + "	dishName text NOT NULL,\n"
                + "	dishDescription text NOT NULL,\n"
                + "	dishPrice real\n"
                + ");";

        try {
            this.connect();
            Statement stmt = conn.createStatement();
            stmt.execute(sql);
            //this.disconnect();

        } catch (SQLException ex) {
            Logger.getLogger(DishService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void connect() {
        // SQLite connection string
        //String url = "jdbc:sqlite:./mydatabase.db";
        String url = "jdbc:sqlite::memory:";

        try {
            conn = DriverManager.getConnection(url);

        } catch (SQLException ex) {
            Logger.getLogger(DishService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void disconnect() {
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }

    }
    @Override
    public String save(Dish newDish) {
        try {

            this.connect();
            String sql = "INSERT INTO dish(id, first_name, last_name, address, mobile, email, gender) VALUES (?,?,?,?,?,?,?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, String.valueOf(newDish.getDishID()));
            pstmt.setString(2, newDish.getDishName());
            pstmt.setString(3, newDish.getDishDescription());
            pstmt.setString(4, String.valueOf(newDish.getDishPrice()));
            //pstmt.setString(5, String.newDish.getDishImage()); Falta
            
            pstmt.executeUpdate();
            pstmt.close();
            this.disconnect();
        } catch (SQLException ex) {
            Logger.getLogger(DishRepositoryImplMysql.class.getName()).log(Level.SEVERE, "Error al insertar el registro", ex);
        }
        return String.valueOf(newDish.getDishID());
    }

    @Override
    public boolean update(Dish newDish) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Dish find(int id) {
        Dish plate = null;

        this.connect();
        try {
            String sql = "SELECT * from dish where id=? ";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, String.valueOf(id));
            ResultSet res = pstmt.executeQuery();
            if (res.next()) {
                plate = new Dish();
                plate.setDishID(Integer.parseInt(res.getString("Id_plate")));
                plate.setDishName(res.getString("Name_plate"));
                plate.setDishDescription(res.getString("Description_plate"));
                plate.setDishPrice(Double.parseDouble(res.getString("Price_plate")));

            }
            pstmt.close();
            this.disconnect();
        } catch (SQLException ex) {
            Logger.getLogger(DishRepositoryImplMysql.class.getName()).log(Level.SEVERE, "Error al consultar Dish de la base de datos", ex);
        }
        return plate;
    }

    @Override
    public List<Dish> list() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
