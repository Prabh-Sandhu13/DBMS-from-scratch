import login.Login;
import sql.QueryEngine;

import java.util.Scanner;

public class DatabaseSystem {
    private String user = null;
    private Scanner sc;
    private QueryEngine queryEngine;

    public DatabaseSystem() {
        sc = new Scanner(System.in);
        queryEngine = new QueryEngine();
    }

    public String authenticate(){
        if(user == null){
            System.out.println("Please authenticate to continue:");
            Login login = new Login();
            System.out.print("Please enter UserName:");
            String userName = sc.nextLine();
            System.out.print("Please enter Password:");
            String password = sc.nextLine();
            user = login.verification(userName, password);
        }
        return user;
    }

    public void init(){
        while(true){
            System.out.println("Enter a SQL query to proceed or exit; to end!");
            String sqlQuery = sc.nextLine();
            if(sqlQuery.equals("exit;")){
                break;
            }
            queryEngine.run(sqlQuery, user);
        }
        sc.close();
    }
}
