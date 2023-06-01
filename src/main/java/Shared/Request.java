package Shared;

import org.json.JSONObject;

import java.util.*;


public class Request {
    public static String createRequest(JSONObject response,Scanner scan){

        if (response.getString("type").equals("initial menu")){
            return initialMenu(scan);
        }
        else if (response.getString("type").equals("menu")){
            return menu(scan,response);
        }
        else if (response.getString("type").equals("sign up")){
            if (response.get("status").equals("true")){
                return menuReq(response);
            }
            else if (response.get("status").equals("false")){
                System.out.println(response.get("reason"));
                System.out.println("Try again?\n1. Yes   2. No");
                int x = Integer.parseInt(scan.nextLine());

                if (x == 1) {
                    return signUp(scan);
                } else {
                    return initialMenuReq();
                }
            }
        }

        else if (response.getString("type").equals("log in")){
            if (response.getString("status").equals("true")){
                return menuReq(response);
            } else {
                System.out.println(response.getString("reason"));
                System.out.println("Try again");
                return logIn(scan);
            }
        }

        else if (response.getString("type").equals("view game list")){
            return showListOfGames(response, scan);
        }

        else if (response.getString("type").equals("details")){
            printDetails(response);
            System.out.println("Do you want to download this game?\n1. yes   2. no");
            int x = Integer.parseInt(scan.nextLine());
            if (x == 1){
                //return downloadGameRequest(response.getString("id"),response);
            } else{
                return menuReq(response);
            }
        }

        else if (response.getString("type").equals("search")){
            return showListOfGames(response,scan);
        }

        return null;
    }

    private static String menu(Scanner scan, JSONObject response) {

        System.out.println("1. List of available games\n2. Browse games\n3. Exit");
        int choice = Integer.parseInt(scan.nextLine());
        if (choice == 1){
            return listOfGames(response);
        } else if (choice == 2) {
            return search(response, scan);
        } else if (choice == 3) {
            return initialMenuReq();
        }
        return null;
    }

    public static String initialMenuReq(){
        JSONObject json = new JSONObject();
        json.put("type","initial menu");

        return json.toString();
    }

    public static String menuReq(JSONObject response){
        JSONObject json = new JSONObject();

        json.put("type","menu");
        json.put("user",response.getJSONObject("user"));

        return json.toString();
    }

    public static String initialMenu(Scanner scan){

        System.out.println("Welcome!\n1. Log in\n2. Sign up");
        int input = Integer.parseInt(scan.nextLine());

        if (input == 1){
            return logIn(scan);
        } else if (input == 2) {
            return signUp(scan);
        }
        return null;
    }

    private static String logIn(Scanner scan) {

        System.out.println("Enter username");
        String username = scan.nextLine();
        System.out.println("Enter your password");
        String pass = scan.nextLine();

        JSONObject object = new JSONObject();
        JSONObject acc = new JSONObject();

        acc.put("username",username);
        acc.put("password",pass);
        object.put("type","log in");
        object.put("user",acc);

        return object.toString();
    }

    public static String signUp(Scanner scan){

        UUID uuid = UUID.randomUUID();

        System.out.println("Enter a username");
        String username = scan.nextLine();

        System.out.println("Enter password");
        String password = scan.nextLine();

        System.out.println("Enter your birthday");
        String dob = scan.nextLine();

        JSONObject object = new JSONObject();
        object.put("type","sign up");

        JSONObject acc = new JSONObject();

        acc.put("username",username);
        acc.put("password",password);
        acc.put("date",dob);
        acc.put("id",uuid.toString());

        object.put("user",acc);

        return object.toString();
    }

    private static String listOfGames(JSONObject response) {
        JSONObject json = new JSONObject();
        json.put("type", "view game list");
        json.put("user", response.getJSONObject("user"));

        return json.toString();
    }
    private static String search(JSONObject response,Scanner scan) {

        JSONObject object = new JSONObject();
        object.put("type","search");
        object.put("user",response.getJSONObject("user"));

        System.out.println("Enter the game title : ");
        String title = scan.nextLine();
        object.put("title",title);
        return object.toString();
    }

    private static String detailsReq(String id, JSONObject resp) {

        JSONObject object = new JSONObject();
        object.put("type","details");
        object.put("user",resp.getJSONObject("user"));

        object.put("id",id);
        return object.toString();
    }
    private static String showListOfGames(JSONObject response,Scanner scan) {

        JSONObject list = response.getJSONObject("games");
        List<String> gamesIds = new ArrayList<>(list.keySet());
        for (int i = 0; i < gamesIds.size(); i++) {
            JSONObject game = list.getJSONObject(gamesIds.get(i));
            System.out.println(i+1 + "- " + game.getString("title"));
        }
        System.out.println("Enter number of the game you want:");
        int x = Integer.parseInt(scan.nextLine());
        String id = gamesIds.get(x - 1);

        return detailsReq(id,response);

    }

    private static void printDetails(JSONObject object) {

        JSONObject game = object.getJSONObject("details");
        System.out.println("Title: " + game.getString("title"));
        System.out.println("Developer: " + game.getString("developer"));
        System.out.println("Genre: " + game.getString("genre"));
        System.out.println("Price: " + game.getString("price"));
        System.out.println("Release year : " + game.getString("release_year"));
        System.out.println("Controller support: " + game.getString("controller_support"));
        System.out.println("Reviews: " + game.getString("reviews"));
        System.out.println("Size: " + game.getString("size"));
    }


//    private static String downloadGameRequest(String id, JSONObject resp) {
//
//        JSONObject object = new JSONObject();
//        object.put("type", "download");
//        object.put("user",resp.getJSONObject("user"));
//        object.put("id",id);
//        return object.toString();
//    }
}
