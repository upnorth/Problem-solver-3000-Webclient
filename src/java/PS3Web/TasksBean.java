/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package PS3Web;

import com.sun.rowset.CachedRowSetImpl;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.sql.DataSource;
import javax.sql.rowset.CachedRowSet;

/**
 *
 * @author ingand
 */
@ManagedBean(name="TasksBean")
@RequestScoped
public class TasksBean {
    private int taskId;
    private int techId;
    private String status;
    private int UsedTime;
    private String comments;
    CachedRowSet tasks;
    DataSource ds;
    
    /**
     * Creates a new instance of StudentBean
     */
    public TasksBean() {
        this.tasks = null;
    }

    public int getTaskId() {
        return taskId;
    }

    public void setTaskId(int id) {
        this.taskId = id;
    }    
    
    public int getTechId() {
        return techId;
    }

    public void setTechId(int techId) {
        this.techId = techId;
    }

    public String getstatus() {
        return status;
    }

    public void setstatus(String status) {
        this.status = status;
    }
    
    public int getUsedTime() {
        return UsedTime;
    }

    public void setUsedTime(int time) {
        this.UsedTime = time;
    }
    
    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }
    public ResultSet getTasks() throws SQLException, ClassNotFoundException {
        Connection cn = null;
         
        try{
            //Laddar db-library (derby) till applikationens minne
            Class.forName("org.apache.derby.jdbc.ClientDriver");
//            Class.forName("org.apache.derby.jdbc.ClientDriver").newInstance();ibland krävs "newInstance()"
            //specificerar uppgifter som behövs för att koppla upp sig mot databasen
            cn = DriverManager.getConnection("jdbc:derby://localhost:1527/ps3;create=true;user=ps3;password=ps3");
            //specificerar vilken SQL-fråga som ska köras mot db
            PreparedStatement stmt =
             cn.prepareStatement("SELECT ID, PROBLEM_ID, CATEGORY, ESTTIME, USEDTIME, PRIORITY, COMMENTS FROM TASK WHERE TECH_ID = ?");
            //Läser in en variabel som ersätter "?" i SQL-frågan 
            stmt.setInt(1, getTechId());
            //skapa en instans av det objekt som ska hålla resultatet av SQL-frågan
            tasks = new CachedRowSetImpl(); 
            //kör SQL-frågan "select..." och returnerar ett Resulset som läses in i result-objektet "CachedRowSet"
            tasks.populate(stmt.executeQuery()); 
            return tasks;//returnerar posterna (resultset) från denna metod... 
        }
        /*catch (ClassNotFoundException | SQLException ex) {
            System.out.println("Source: " + TasksBean.class.getName() + " Error: " +  ex.getMessage());
            //Skriver följande typ av meddelande i output från GlassFish
            //Info:   Källa: se.ltu.d0007n.TasksBean Error: java.net.ConnectException : Error connecting to server localhost on port 1526 with message Connection refused.
        }*/
        finally{
            if (cn!=null) cn.close();
        }
    }
    public String updateTask() throws SQLException, ClassNotFoundException {
//        Läs in information om db-uppkoppling via connection-pool OBS används ej i denna lösning!
//        InitialContext ctx = new InitialContext();
//        ds = (DataSource) ctx.lookup("jdbc/students");
        //deklarera objekt-ref som används för uppkoppling mot db
        Connection cn = null;
        boolean newStatus;
        try{
            //Laddar db-library (derby) till applikationens minne
            Class.forName("org.apache.derby.jdbc.ClientDriver");
            //specificerar uppgifter som behövs för att koppla upp sig mot databasen
            cn = DriverManager.getConnection("jdbc:derby://localhost:1527/ps3;create=true;user=ps3;password=ps3");
            //specificerar vilket SQL-uttryck som ska köras mot db           
            if("ongoing".equals(getstatus())){
                PreparedStatement stmt =
                    cn.prepareStatement("UPDATE TASK SET USEDTIME=?, COMMENTS=? WHERE ID=?");
                
                stmt.setInt(1, getUsedTime());
                stmt.setString(2, getComments());
                stmt.setInt(3, getTaskId());

                //Kör SQL-uttryck
                stmt.executeUpdate();
            }
            else {
                newStatus = Boolean.parseBoolean(getstatus());
                            PreparedStatement stmt =
                    cn.prepareStatement("UPDATE TASK SET USEDTIME=?, COMMENTS=?, STATUS=? WHERE ID=?");
                
                stmt.setInt(1, getUsedTime());
                stmt.setString(2, getComments());
                stmt.setBoolean(3, newStatus);
                stmt.setInt(4, getTaskId());

                //Kör SQL-uttryck
                stmt.executeUpdate();
            }
            
            // return "index"; //navigera till index.xhtml
            return "success"; //navigering via faces-config.xml
        }
        catch (ClassNotFoundException | SQLException ex) {
            System.out.println("Source: " + TasksBean.class.getName() + " Error: " +  ex.getMessage());
            //Skriver följande typ av meddelande i output från GlassFish
            //Info:   Källa: se.ltu.d0007n.TasksBean Error: java.net.ConnectException : Error connecting to server localhost on port 1526 with message Connection refused.
            return "failure";
        }
        finally{
            if (cn!=null) cn.close();
        }
    }
}
