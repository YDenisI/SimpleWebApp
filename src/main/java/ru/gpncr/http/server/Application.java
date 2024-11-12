package ru.gpncr.http.server;

import org.apache.logging.log4j.LogManager;

import java.sql.SQLException;
import java.util.Scanner;

public class Application {
    private static final org.apache.logging.log4j.Logger log = LogManager.getLogger(Application.class);
    public static void main(String[] args) {

        int portDefault = 8189;

        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите порт (или нажмите Enter для выбора порта по умолчанию"+ portDefault+"): ");
        String input = scanner.nextLine();

        if (!input.isEmpty()) {
            try {
                int port;
                port = Integer.parseInt(input);
                if (port <= 0 || port > 65535) {
                    throw new IllegalArgumentException("Порт должен быть в диапазоне от 1 до 65535.");
                }
                portDefault = port;
            } catch (NumberFormatException e) {
                log.error("Неверный формат числа. Используется порт по умолчанию: " + portDefault);
            } catch (IllegalArgumentException e) {
                log.error(e.getMessage() + " Используется порт по умолчанию: " + portDefault);
            }
        }

        new HttpServer(portDefault).start();
    }
}
