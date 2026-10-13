package com.example.taskmanager;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación Task Manager API.
 *
 * @SpringBootApplication es una meta-anotación que combina:
 *   - @Configuration: esta clase puede definir beans.
 *   - @EnableAutoConfiguration: activa la configuración automática de Spring.
 *   - @ComponentScan: escanea el paquete actual y subpaquetes buscando componentes.
 */
@SpringBootApplication
public class TaskManagerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagerApplication.class, args);
    }
}
