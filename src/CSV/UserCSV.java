package CSV;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;


public class UserCSV {
    public static String filePath  = "files\\users.csv";

    public static boolean createCSV(){
        if(!Files.exists(Path.of(filePath))){
            try {
                Files.createFile(Path.of(filePath));
                return true;
            } catch (IOException e) {
                System.err.println("Failed creating the CSV file to register users.");
            }
        }
        return false;
    }

    public static boolean userExists(String username) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] credentials = line.split(",");
                if (credentials.length == 2) {
                    String fileUsername = credentials[0].trim().toLowerCase();
                    if (fileUsername.equals(username.toLowerCase().trim())) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
    //This one is for test purposes
    public static void setFilePath(String newPath) {
        filePath = newPath;
    }

    public static boolean authenticate(String username, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] credentials = line.split(",");
                if (credentials.length == 2) {
                    String fileUsername = credentials[0].trim().toLowerCase();
                    String filePassword = credentials[1].trim();
                    if (fileUsername.equals(username.toLowerCase()) && filePassword.equals(password)) {
                        return true;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean createUser(String username, String password){
        if(UserCSV.userExists(username)) return false;
        try (FileWriter fw = new FileWriter(filePath, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
            out.print(username + "," + password + "\n");
            return true;
        } catch (IOException e) {
            System.err.println("Error writing the file: " + e.getMessage());
            return false;
        }
    }

    public static boolean changePwd(String username, String currPwd, String newPwd) {
        if (authenticate(username, currPwd)) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(filePath));
                StringBuilder rewrittenCSV = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    String[] credentials = line.split(",");
                    if (credentials.length == 2) {
                        if (credentials[0].equals(username)) {
                            rewrittenCSV.append(credentials[0]).append(",").append(newPwd).append("\n"); //Sobreescribir la linea que se guardará
                        } else rewrittenCSV.append(line).append("\n");
                    }
                }
                br.close();
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
                    bw.write(rewrittenCSV.toString());
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean changeUsername(String username, String newUsername, String pwd) {
        if (authenticate(username, pwd)) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(filePath));
                StringBuilder rewrittenCSV = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    String[] credentials = line.split(",");
                    if (credentials.length == 2) {
                        if (credentials[0].equals(username)) {
                            rewrittenCSV.append(newUsername).append(",").append(credentials[1]).append("\n"); //Sobreescribir la linea que se guardará
                        } else rewrittenCSV.append(line).append("\n");
                    }
                }
                br.close();
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(filePath))) {
                    bw.write(rewrittenCSV.toString());
                }
                return true;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}