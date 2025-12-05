package com.example.studentapp.controller;

import com.example.studentapp.model.Student;
import com.example.studentapp.repository.StudentRepository;
import com.example.studentapp.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/students")
public class StudentController {
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private StudentService studentService;
    
    // GET /students - Lấy danh sách tất cả students (có cache 30 giây)
    @GetMapping
    public ResponseEntity<List<Student>> getAllStudents() {
        List<Student> students = studentService.getAllStudents();
        return ResponseEntity.ok(students);
    }
    
    // GET /students/{id} - Lấy student theo ID
    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        Optional<Student> student = studentRepository.findById(id);
        
        if (student.isPresent()) {
            return ResponseEntity.ok(student.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    // POST /students - Tạo student mới (xóa cache khi có student mới)
    @PostMapping
    public ResponseEntity<?> createStudent(@Valid @RequestBody Student student) {
        // Kiểm tra email đã tồn tại chưa
        if (studentRepository.findByEmail(student.getEmail()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already exists: " + student.getEmail());
        }
        
        Student savedStudent = studentRepository.save(student);
        // Xóa cache sau khi tạo student mới
        studentService.evictAllStudentsCache();
        return ResponseEntity.status(HttpStatus.CREATED).body(savedStudent);
    }
}

