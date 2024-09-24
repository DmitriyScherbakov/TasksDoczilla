package org.example;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class TextFiles {
    private static final String REQUIRE_DIRECTIVE = "require";  // Директива для обозначения зависимости

    public static void main(String[] args) {
        Path folderPath = Paths.get("D:\\Tasks Doczilla\\RootFolder");
        Path outputPath = Paths.get("D:\\Tasks Doczilla\\RootFolder\\Сoncatenated File.txt");

        try {
            // Получаем список всех .txt файлов рекурсивно, сортируем их по относительному пути и собираем в список
            List<Path> textFiles = Files.walk(folderPath)
                    .filter(file -> file.toString().endsWith(".txt")) // Фильтруем только .txt файлы
                    .filter(file -> !file.getFileName().toString().equals("Сoncatenated File.txt"))
                    .sorted(Comparator.comparing(file -> folderPath.relativize(file).toString().replace("\\", "/"))) // Сортируем по относительному пути
                    .collect(Collectors.toList());

            // Выводим отсортированные файлы
            textFiles.forEach(file -> System.out.println("Файл: " + folderPath.relativize(file)));

            // Карта зависимостей
            Map<Path, List<Path>> dependencies = new HashMap<>();
            for (Path file : textFiles) {
                dependencies.put(file, parseDependencies(file, folderPath));
            }

            // Топологическая сортировка для разрешения зависимостей
            List<Path> sortedFiles = topologicalSort(dependencies);

            // Если нет циклических зависимостей, продолжаем объединять файлы
            if (sortedFiles != null) {
                // Разворачиваем список для вывода в обратном порядке
                Collections.reverse(sortedFiles);

                System.out.println("\nФайлы в порядке обработки (топологическая сортировка):");
                sortedFiles.forEach(file -> System.out.println(folderPath.relativize(file))); // Выводим относительный путь файла

                // Создаем/очищаем файл для объединенного результата
                Files.write(outputPath, "".getBytes(StandardCharsets.UTF_8), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

                // Читаем каждый файл и записываем его содержимое в итоговый файл
                for (Path file : sortedFiles) {
                    List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);  // Читаем содержимое файла
                    Files.write(outputPath, lines, StandardCharsets.UTF_8, StandardOpenOption.APPEND);  // Добавляем содержимое в итоговый файл
                    Files.write(outputPath, "\n".getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);  // Добавляем перенос строки между файлами
                }

                System.out.println("Все файлы успешно объединены в: " + outputPath);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // Обработка циклической зависимости
            System.out.println("Ошибка: " + e.getMessage());
        }
    }


    // Метод для парсинга зависимостей из файла
    private static List<Path> parseDependencies(Path file, Path rootDirectory) throws IOException {
        List<Path> dependencies = new ArrayList<>();
        List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);

        for (String line : lines) {
            if (line.contains(REQUIRE_DIRECTIVE)) {
                // Извлекаем путь к зависимому файлу
                String requiredFile = line.substring(line.indexOf(REQUIRE_DIRECTIVE) + REQUIRE_DIRECTIVE.length()).trim();

                // Убираем кавычки из пути, если они есть
                requiredFile = requiredFile.replace("‘", "").replace("’", "").replace("'", "").trim();

                if (!requiredFile.endsWith(".txt")) {
                    requiredFile += ".txt";
                }

                if (!requiredFile.startsWith("D:/Tasks Doczilla/RootFolder/")) {
                    requiredFile = "D:/Tasks Doczilla/RootFolder/" + requiredFile;
                }

                // Приведение к правильному пути относительно корневого каталога
                Path requiredPath = rootDirectory.resolve(requiredFile).normalize();  // Получаем абсолютный путь к зависимому файлу

                // Проверяем существование файла
                if (Files.exists(requiredPath)) {
                    dependencies.add(requiredPath);  // Добавляем зависимость только если файл существует
                    //System.out.println("Зависимость найдена и добавлена: " + requiredPath);
                } else {
                    System.out.println("Предупреждение: Файл зависимости не найден " + requiredPath);
                }
            }
        }

        return dependencies;
    }

    // Метод для топологической сортировки файлов с учётом зависимостей
    private static List<Path> topologicalSort(Map<Path, List<Path>> dependencies) {
        // Подсчёт входящих рёбер (in-degree)
        Map<Path, Integer> inDegree = new HashMap<>();
        for (Path file : dependencies.keySet()) {
            inDegree.putIfAbsent(file, 0);
            for (Path dep : dependencies.get(file)) {
                inDegree.put(dep, inDegree.getOrDefault(dep, 0) + 1);
            }
        }

        // Поиск файлов без входящих рёбер (независимых)
        Queue<Path> queue = new LinkedList<>();
        for (Map.Entry<Path, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        // Список для сортированных файлов
        List<Path> sortedFiles = new ArrayList<>();

        // Алгоритм топологической сортировки
        while (!queue.isEmpty()) {
            Path current = queue.poll();
            sortedFiles.add(current);

            for (Path dep : dependencies.get(current)) {
                inDegree.put(dep, inDegree.get(dep) - 1);
                if (inDegree.get(dep) == 0) {
                    queue.add(dep);
                }
            }
        }

        // Если есть файлы, которые не были обработаны, то существует циклическая зависимость
        if (sortedFiles.size() != dependencies.size()) {
            throw new IllegalArgumentException("Обнаружена циклическая зависимость в файлах.");
        }

        return sortedFiles;
    }
}
