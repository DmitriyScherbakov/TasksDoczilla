package com.example.taskaboutstudents;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class StudentRowMapper implements RowMapper<Student> {

    @Override
    public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
        Student student = new Student();
        student.setId(rs.getLong("id"));
        student.setFirstName(rs.getString("first_name"));
        student.setLastName(rs.getString("last_name"));
        student.setMiddleName(rs.getString("middle_name"));
        student.setDateOfBirth(rs.getDate("date_of_birth").toLocalDate());
        student.setGroup(rs.getString("group_number"));
        student.setUniqueNumber(rs.getString("unique_number"));
        return student;
    }
}
