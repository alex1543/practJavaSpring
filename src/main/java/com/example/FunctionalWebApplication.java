package com.example;

import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.HttpHandler;
import org.springframework.http.server.reactive.ReactorHttpHandlerAdapter;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.netty.http.server.HttpServer;

import java.sql.*;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.BodyInserters.fromObject;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static org.springframework.web.reactive.function.server.ServerResponse.ok;

public class FunctionalWebApplication {

    static RouterFunction getRouter() {
        System.out.println("Please, open: http://localhost:8080/");

        StringBuilder str_out= new StringBuilder("<!DOCTYPE html>\n<html><head><meta charset=\"UTF-8\"></head><body>");
        str_out.append("<style>html{font-family: monospace;} table {width: 100%;border-collapse: collapse;} td {border: 1px solid black;padding: 3px;} tr:hover {background: #d0e3f7;} p {font-size: large;font-weight: bold;}</style>");
        str_out.append("<p>Применённые технологии: Java Spring + MySQL.</p>");

        // Получение таблицы из MySQL ...
        try {
            Connection conn= DriverManager.getConnection("jdbc:mysql://localhost:3306/test", "root", "");
            Statement stmt=conn.createStatement();
            ResultSet resultSet=stmt.executeQuery("SELECT * FROM myarttable WHERE id>14 ORDER BY id DESC");
            str_out.append("<table>");
            while (resultSet.next()){
                int colCount=resultSet.getMetaData().getColumnCount();
                str_out.append("<tr>");
                for (int i=1; i <= colCount;i++)
                    str_out.append("<td>").append(resultSet.getString(i)).append("</td>");
                str_out.append("</tr>");
            }
            str_out.append("</table>");
            System.out.println(str_out);
            resultSet.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        str_out.append("<p>Применённые технологии: Java Spring + PostgreSQL.</p>");
        // Получение таблицы из PostgreSQL ...
        try {
            Connection conn= DriverManager.getConnection("jdbc:postgresql://localhost:5432/leti", "postgres", "test");
            Statement stmt=conn.createStatement();
            ResultSet resultSet=stmt.executeQuery("SELECT * FROM zoo WHERE id>14 ORDER BY id DESC");
            str_out.append("<table>");
            while (resultSet.next()){
                int colCount=resultSet.getMetaData().getColumnCount();
                str_out.append("<tr>");
                for (int i=1; i <= colCount;i++)
                    str_out.append("<td>").append(resultSet.getString(i)).append("</td>");
                str_out.append("</tr>");
            }
            str_out.append("</table>");
            System.out.println(str_out);
            resultSet.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        str_out.append("</html>");

        // Отправка HTML.
        return route(GET("/*"), request -> ok().contentType(MediaType.TEXT_HTML).body(fromObject(str_out.toString())));
        //    .andRoute(GET("/json"), req -> ok().contentType(APPLICATION_JSON).body(fromObject(new Hello("world"))));
    }

    public static void main(String[] args) throws InterruptedException {
        HttpHandler httpHandler = RouterFunctions.toHttpHandler(getRouter());
        HttpServer.create().port(8080).handle(new ReactorHttpHandlerAdapter(httpHandler)).bindNow();
        Thread.currentThread().join();
    }
}
