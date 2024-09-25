package com.example.taskaboutstudents;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class StudentRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    public List<Student> findAll() {
        String sql = "SELECT * FROM students";
        return jdbcTemplate.query(sql, new StudentRowMapper());
    }

    public int save(Student student) {
        String sql = "INSERT INTO students (first_name, last_name, middle_name, date_of_birth, group_number, unique_number) VALUES (?, ?, ?, ?, ?, ?)";
        return jdbcTemplate.update(sql, student.getFirstName(), student.getLastName(), student.getMiddleName(), student.getDateOfBirth(), student.getGroup(), student.getUniqueNumber());
    }

    public int deleteById(Long id) {
        String sql = "DELETE FROM students WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    public Student findById(Long id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        return jdbcTemplate.queryForObject(sql, new StudentRowMapper(), id);
    }
}
