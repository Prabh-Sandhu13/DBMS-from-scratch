import SQLDump.SQLDump;
import datadictionarygenerator.DataDictionaryGenerator;
import erdgenerator.ERDGenerator;

import java.util.Scanner;

public class Main {
    private static Scanner sc;

    public static void main(String[] args) {
        System.out.println ("*** Welcome to DBMS5408!***");
        System.out.println ("");
        System.out.println ("###########################");

        DatabaseSystem databaseSystem = new DatabaseSystem ();
        sc = new Scanner (System.in);
        String username = databaseSystem.authenticate ();
        if (username != null) {
            System.out.println ("Choose from one of the operations");
            System.out.println ("1. Enter Query");
            System.out.println ("2. SQL Dump");
            System.out.println ("3. Generate ERD");
            System.out.println ("4. Generate data dictionary");
            String userInput = sc.nextLine ();
            switch (userInput) {
                case "1":
                    databaseSystem.init ();
                    break;
                case "2":
                    SQLDump dump = new SQLDump ();
                    System.out.println ("Enter database name");
                    String databaseName = sc.nextLine ();
                    dump.generateSQLDump (username, databaseName);
                    break;
                case "3":
                    ERDGenerator erdObj = new ERDGenerator ();
                    System.out.println ("Enter a database name");
                    String database = sc.nextLine ();
                    erdObj.generateERD (username, database);
                    break;
                case "4":
                    DataDictionaryGenerator dataDictionaryGenerator = new DataDictionaryGenerator ();
                    System.out.println ("Enter a database name");
                    String database2 = sc.nextLine ();
                    dataDictionaryGenerator.generate (username, database2);
                    break;
                default:
                    System.out.println ("Invalid input!");

            }

        }
    }
}
