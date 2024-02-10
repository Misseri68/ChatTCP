package TestsVarios;

import DAOs.UserCSV;

public class TestsCSV {
    /*public static void main(String[] args) {
        String s = "/login ari a a";
        System.out.println(s.split(" ", 3).length);
    }*/

    public static void main(String[] args) {
        System.out.println(UserCSV.createUser("Dragos", "el más weño <3"));
        System.out.println(UserCSV.createUser("ari", "123"));
        System.out.println(UserCSV.userExists("ari"));
        System.out.println(UserCSV.userExists("ARI"));
        System.out.println(UserCSV.authenticate("ari", "123"));
        System.out.println(UserCSV.authenticate("ARI", "123"));
        System.out.println(UserCSV.changePwd("ari", "123", "321"));
        System.out.println(UserCSV.changePwd("ari", "12", "321"));
        System.out.println(UserCSV.changePwd("a", "a", "a"));
    }
}