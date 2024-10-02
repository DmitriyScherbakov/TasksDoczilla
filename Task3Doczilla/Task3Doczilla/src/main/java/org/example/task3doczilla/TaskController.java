package org.example.task3doczilla;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Controller
public class TaskController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String API_URL = "https://todo.doczilla.pro/api/todos";  // URL внешнего API

    private String formatDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return date.format(formatter);
    }

    @GetMapping("/todos")
    public String getTodos(Model model) {
        // Получение данных из внешнего API
        Todo[] todos = restTemplate.getForObject(API_URL, Todo[].class);

        // Форматируем дату для каждого Todo
        for (Todo todo : todos) {
            todo.setFormattedDate(formatDate(todo.getDate()));
        }

        // Добавляем задачи в модель, чтобы передать их в представление
        model.addAttribute("todos", todos);
        return "mainPage";
    }

}
