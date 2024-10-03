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
import java.util.Arrays;
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

    private long convertToEpochMilli(LocalDateTime dateTime) {
        return dateTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    @GetMapping("/todos")
    public String getTodos(@RequestParam(value = "onlyIncomplete", required = false, defaultValue = "false") boolean onlyIncomplete, Model model) {
        // Получение данных из внешнего API
        Todo[] todos = restTemplate.getForObject(API_URL, Todo[].class);

        // Фильтруем задачи по статусу, если необходимо
        if (onlyIncomplete) {
            todos = Arrays.stream(todos)
                    .filter(todo -> !todo.isStatus())
                    .toArray(Todo[]::new);
        }

        // Форматируем дату для каждого Todo
        for (Todo todo : todos) {
            todo.setFormattedDate(formatDate(todo.getDate()));
        }

        // Форматируем текущую дату
        String formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        model.addAttribute("currentDate", formattedDate); // Добавляем текущую дату в модель

        // Добавляем задачи в модель
        model.addAttribute("todos", todos);
        return "mainPage";
    }




    @GetMapping("/todos/today")
    public String getTodosForToday(
            @RequestParam(value = "onlyIncomplete", required = false, defaultValue = "false") boolean onlyIncomplete,
            Model model) {
        // Получаем текущую дату
        LocalDate today = LocalDate.now();
        LocalDateTime fromDate = today.atStartOfDay();
        LocalDateTime toDate = today.atTime(23, 59, 59);

        long fromEpoch = convertToEpochMilli(fromDate);
        long toEpoch = convertToEpochMilli(toDate);

        Map<String, Object> params = new HashMap<>();
        params.put("from", fromEpoch);
        params.put("to", toEpoch);

        Todo[] todos = restTemplate.getForObject(API_URL_DATE + "?from={from}&to={to}", Todo[].class, params);

        // Фильтруем задачи по статусу, если включен фильтр только для невыполненных
        if (onlyIncomplete) {
            todos = Arrays.stream(todos)
                    .filter(todo -> !todo.isStatus())
                    .toArray(Todo[]::new);
        }

        // Форматируем дату для каждого Todo
        for (Todo todo : todos) {
            todo.setFormattedDate(formatDate(todo.getDate()));
        }

        String formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        model.addAttribute("currentDate", formattedDate);
        model.addAttribute("todos", todos);
        model.addAttribute("onlyIncomplete", onlyIncomplete);
        return "mainPage";
    }

    @GetMapping("/todos/week")
    public String getTodosForWeek(
            @RequestParam(value = "onlyIncomplete", required = false, defaultValue = "false") boolean onlyIncomplete,
            Model model) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        LocalDateTime fromDate = startOfWeek.atStartOfDay();
        LocalDateTime toDate = endOfWeek.atTime(23, 59, 59);

        long fromEpoch = convertToEpochMilli(fromDate);
        long toEpoch = convertToEpochMilli(toDate);

        Map<String, Object> params = new HashMap<>();
        params.put("from", fromEpoch);
        params.put("to", toEpoch);

        Todo[] todos = restTemplate.getForObject(API_URL_DATE + "?from={from}&to={to}", Todo[].class, params);

        if (onlyIncomplete) {
            todos = Arrays.stream(todos)
                    .filter(todo -> !todo.isStatus())
                    .toArray(Todo[]::new);
        }

        for (Todo todo : todos) {
            todo.setFormattedDate(formatDate(todo.getDate()));
        }

        // Форматируем текущую дату
        String formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        model.addAttribute("currentDate", formattedDate);
        model.addAttribute("todos", todos);
        model.addAttribute("onlyIncomplete", onlyIncomplete);
        return "mainPage";
    }

    @GetMapping("/todos/range")
    public String getTodosForDateRange(
            @RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "onlyIncomplete", required = false, defaultValue = "false") boolean onlyIncomplete,
            Model model) {
        LocalDateTime fromDate = startDate.atStartOfDay();
        LocalDateTime toDate = endDate.atTime(23, 59, 59);

        long fromEpoch = convertToEpochMilli(fromDate);
        long toEpoch = convertToEpochMilli(toDate);

        Map<String, Object> params = new HashMap<>();
        params.put("from", fromEpoch);
        params.put("to", toEpoch);

        Todo[] todos = restTemplate.getForObject(API_URL_DATE + "?from={from}&to={to}", Todo[].class, params);

        if (onlyIncomplete) {
            todos = Arrays.stream(todos)
                    .filter(todo -> !todo.isStatus())
                    .toArray(Todo[]::new);
        }

        for (Todo todo : todos) {
            todo.setFormattedDate(formatDate(todo.getDate()));
        }


        String formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        model.addAttribute("currentDate", formattedDate);
        model.addAttribute("todos", todos);
        model.addAttribute("onlyIncomplete", onlyIncomplete);
        return "mainPage";
    }


}
