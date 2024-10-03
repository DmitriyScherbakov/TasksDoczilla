package org.example.task3doczilla;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.RestTemplate;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.util.Map;

@Controller
public class TaskController {

    @Autowired
    private RestTemplate restTemplate;

    private static final String API_URL = "https://todo.doczilla.pro/api/todos";  // URL внешнего API
    private static final String API_URL_DATE = "https://todo.doczilla.pro/api/todos/date";

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

    @GetMapping("/todos/today")
    public String getTodosForToday(Model model) {
        // Получаем текущую дату
        LocalDate today = LocalDate.now();
        // Устанавливаем начало и конец дня
        LocalDateTime fromDate = today.atStartOfDay();
        LocalDateTime toDate = today.atTime(23, 59, 59);

        // Конвертируем в миллисекунды
        long fromEpoch = convertToEpochMilli(fromDate);
        long toEpoch = convertToEpochMilli(toDate);

        // Создаем параметры для API
        Map<String, Object> params = new HashMap<>();
        params.put("from", fromEpoch);
        params.put("to", toEpoch);

        // Запрос к API
        Todo[] todos = restTemplate.getForObject(API_URL_DATE + "?from={from}&to={to}", Todo[].class, params);

        // Форматируем дату для каждого Todo
        for (Todo todo : todos) {
            todo.setFormattedDate(formatDate(todo.getDate()));
        }

        model.addAttribute("todos", todos);
        //model.addAttribute("activeButton", "today");
        return "mainPage";
    }

    @GetMapping("/todos/week")
    public String getTodosForWeek(Model model) {
        // Получаем текущую дату
        LocalDate today = LocalDate.now();
        // Устанавливаем начало недели (понедельник) и конец недели (воскресенье)
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        // Устанавливаем начало и конец дня
        LocalDateTime fromDate = startOfWeek.atStartOfDay();
        LocalDateTime toDate = endOfWeek.atTime(23, 59, 59);

        // Конвертируем в миллисекунды
        long fromEpoch = convertToEpochMilli(fromDate);
        long toEpoch = convertToEpochMilli(toDate);

        // Создаем параметры для API
        Map<String, Object> params = new HashMap<>();
        params.put("from", fromEpoch);
        params.put("to", toEpoch);

        // Запрос к API
        Todo[] todos = restTemplate.getForObject(API_URL_DATE + "?from={from}&to={to}", Todo[].class, params);

        // Форматируем дату для каждого Todo
        for (Todo todo : todos) {
            todo.setFormattedDate(formatDate(todo.getDate()));
        }

        model.addAttribute("todos", todos);
        return "mainPage";
    }

    private long convertToEpochMilli(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
}
