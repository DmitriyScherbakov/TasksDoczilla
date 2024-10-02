package org.example.task3doczilla;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class TaskController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String API_URL = "https://todo.doczilla.pro/api/todos";  // URL внешнего API

    @GetMapping("/todos")
    public String getTodos(Model model) {
        // Получение данных из внешнего API
        Todo[] todos = restTemplate.getForObject(API_URL, Todo[].class);

        /*// Логируем полученные данные
        for (Todo todo : todos) {
            System.out.println(todo.getName() + " - " + todo.getFullDesc());
            System.out.println();
        }*/

        // Добавляем задачи в модель, чтобы передать их в представление
        model.addAttribute("todos", todos);
        return "mainPage";
    }
}
