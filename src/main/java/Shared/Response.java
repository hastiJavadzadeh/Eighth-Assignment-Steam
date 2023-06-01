package Shared;

import org.json.JSONObject;

import java.io.*;
import java.sql.*;
import java.util.*;

public class Response {
    public static String responseCreator(JSONObject request,Statement statement) throws SQLException, IOException {

        if (request.getString("type").equals("initial menu")){
            return initialMenu();
        }
        else if(request.getString("type").equals("sign up")){
            return signUp(statement,request);
        }
        else if (request.getString("type").equals("log in")){
            return logIn(statement,request);
        }
        else if (request.getString("type").equals("menu")){
            return menu(request);
        }
        else if (request.getString("type").equals("search")){
            return search(request,statement);
        }
        else if (request.getString("type").equals("details")){
            return details(request,statement);
        }
        else if (request.getString("type").equals("view game list")){
            return gameList(statement, request);
        }
        else if (request.getString("type").equals("download")){
            return downloadResponse(request,statement);
        }

        return null;
    }

    public static String initialMenu(){
        JSONObject object = new JSONObject();
        object.put("type","initial menu");

        return object.toString();
    }

    public static String menu(JSONObject request){
        JSONObject object = new JSONObject();
        object.put("user",request.getJSONObject("user"));
        object.put("type","menu");

        return object.toString();
    }

    public static String signUp(Statement statement,JSONObject request) throws SQLException {

        JSONObject object = new JSONObject();
        object.put("type","sign up");
        if (!doesUserExist(request, statement)){

            object.put("status", "true");

            JSONObject user = request.getJSONObject("user");
            statement.executeUpdate("INSERT INTO accounts VALUES ('" + user.getString("id") + "','" + user.getString("username") + "', '" + user.getString("password") + "','" + user.getString("date") + "')");

        } else {
            object.put("status", "false");
            object.put("reason","This username already exists");
        }

        object.put("user",request.getJSONObject("user"));

        return object.toString();
    }


    public static boolean doesUserExist(JSONObject object,Statement statement) throws SQLException {

        ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM accounts WHERE username = '" + object.getJSONObject("user").getString("username") + "'");
        resultSet.next();

        return resultSet.getInt("count") != 0;//account doesn't exist ---> false        account exists ---> true
    }


    private static String logIn(Statement statement, JSONObject req) throws SQLException {
        JSONObject acc = req.getJSONObject("user");

        JSONObject json = new JSONObject();
        json.put("type", "log in");

        if (doesUserExist(req, statement)){
            ResultSet result = statement.executeQuery("SELECT * FROM accounts WHERE username = '" + acc.getString("username") + "'");
            result.next();

            if (result.getString("password").equals(acc.getString("password"))){
                acc.put("date",result.getString("date_of_birth"));
                acc.put("id",result.getString("id"));
                json.put("user",acc);
                json.put("status", "true");
            } else {
                json.put("status", "false");
                json.put("reason","Incorrect password");
            }
        }
        else {
            json.put("status", "false");
            json.put("reason","This username doesn't exist");
        }
        return json.toString();
    }

    private static String gameList(Statement statement, JSONObject request) throws SQLException {
        JSONObject json = new JSONObject();

        json.put("type", "view game list");
        json.put("user",request.getJSONObject("user"));

        ArrayList<String> columns = new ArrayList<>();

        for (int i = 2; i <= 9; i++) {
            ResultSet result = statement.executeQuery("SELECT column_name FROM information_schema.columns\n" + "WHERE table_name = '" + "games" + "' AND ordinal_position = " + i + ";");
            result.next();
            columns.add(result.getString("column_name"));
        }

        ResultSet result = statement.executeQuery("SELECT * FROM games");
        result.next();

        JSONObject games = new JSONObject();

        while (!result.isAfterLast()){
            JSONObject details = new JSONObject();
            for (int j = 0; j < 8; j++){
                String column = columns.get(j);
                details.put(column,result.getString(column));
            }
            games.put(result.getString("id"),details);
            result.next();
        }
        json.put("games",games);

        return json.toString();
    }

    private static String search(JSONObject request, Statement statement) throws SQLException {

        ArrayList<String> columnsNames = new ArrayList<>();
        JSONObject json = new JSONObject();
        JSONObject result = new JSONObject();

        for (int i = 2; i <= 9; i++) {
            ResultSet resultSet = statement.executeQuery("SELECT column_name FROM information_schema.columns\n" + "WHERE table_name = '" + "games" + "' AND ordinal_position = " + i + ";");
            resultSet.next();
            columnsNames.add(resultSet.getString("column_name"));
        }

        json.put("type","search");
        json.put("user",request.getJSONObject("user"));

        ResultSet resultSet = statement.executeQuery("SELECT * FROM games WHERE LOWER(title) LIKE '%" + request.getString("title").toLowerCase(Locale.ROOT) + "%'");
        resultSet.next();

        while (!resultSet.isAfterLast()){

            JSONObject object = new JSONObject();
            for (int i = 0; i < 8; i++){
                String columnName = columnsNames.get(i);
                object.put(columnName,resultSet.getString(columnName));
            }
            result.put(resultSet.getString("id"),object);
            resultSet.next();
        }

        json.put("games",result);

        return json.toString();
    }

    public static String details(JSONObject req,Statement statement) throws SQLException {

        JSONObject object = new JSONObject();

        ResultSet rs = statement.executeQuery("SELECT * FROM games WHERE id = '"+req.getString("id")+"'");
        rs.next();

        object.put("title",rs.getString("title"));
        object.put("developer",rs.getString("developer"));
        object.put("genre",rs.getString("genre"));
        object.put("price",rs.getString("price"));
        object.put("release_year",rs.getString("release_year"));
        object.put("controller_support",rs.getString("controller_support"));
        object.put("reviews",rs.getString("reviews"));
        object.put("size",rs.getString("size"));

        JSONObject res = new JSONObject();
        res.put("type","details");
        res.put("details",object);
        res.put("id",req.getString("id"));

        return res.toString();
    }
//    public static String downloadResponse

    public static String downloadResponse(JSONObject request,Statement statement) throws SQLException {
        insertDownload(request,statement,downloadCount(request,statement));
        JSONObject json = new JSONObject();
        json.put("type","menu");
        json.put("user", request.getJSONObject("user"));

        String id = request.getString("id");

        String path = "C:\\Users\\astan\\Eighth-Assignment-Steam\\src\\main\\java\\Client\\Downloads\\";
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        ArrayList<String> fileNames = new ArrayList<>();

        for (File file:listOfFiles){
            if (file.getName().endsWith(".png")) {
                fileNames.add(file.getName().substring(0,file.getName().length() - 4));
            }
        }

        int i = 1;
        String id1 = id;
        while (fileNames.contains(id)){
            id = id1 + " (" + i + ")";
            i++;
        }
        return json.toString();

    }

    public static void insertDownload(JSONObject request, Statement statement, int download_count) throws SQLException {
        JSONObject user = request.getJSONObject("user");
        String sql = "";

        if (download_count  == 0) {
            sql = "INSERT INTO downloads VALUES ('" + user.getString("id") + "','" + request.getString("id") + "','" + 1 + "')";
        }
        else {
            sql = "UPDATE downloads SET download_count = " + (download_count + 1) + " WHERE account_id = '" + user.getString("id") + "' AND game_id = '" + request.getString("id") + "'";
        }
        statement.executeUpdate(sql);
    }

    public static int downloadCount(JSONObject request,Statement statement) throws SQLException {
        ResultSet result = statement.executeQuery("SELECT COUNT(*) FROM downloads WHERE account_id = '" + request.getJSONObject("user").getString("id") + "' AND game_id = '" + request.getString("id") + "'");
        result.next();

        if (result.getInt("count") == 0){
            return 0;
        }

        else{
            result = statement.executeQuery("SELECT * FROM downloads WHERE account_id = '" + request.getJSONObject("user").getString("id") + "' AND game_id = '" + request.getString("id") + "'");
            result.next();

            return result.getInt("download_count");
        }
    }
}
