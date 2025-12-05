package com.example.studentapp.service;

import com.example.studentapp.model.Student;
import com.example.studentapp.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentService {
    
    @Autowired
    private StudentRepository studentRepository;
    
    /**
     * Lấy danh sách tất cả students với cache 30 giây
     */
    @Cacheable(value = "students", key = "'all'")
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }
    
    /**
     * Xóa cache khi có student mới được tạo
     */
    @CacheEvict(value = "students", key = "'all'")
    public void evictAllStudentsCache() {
        // Method này được gọi để xóa cache
    }
}

