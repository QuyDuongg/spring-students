package com.example.studentapp.config;

import com.example.studentapp.model.Student;
import com.example.studentapp.repository.StudentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Autowired
    private StudentRepository studentRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra xem đã có dữ liệu chưa
        if (studentRepository.count() == 0) {
            logger.info("Không có dữ liệu. Đang tạo dữ liệu mẫu...");
            
            List<Student> sampleStudents = Arrays.asList(
                new Student("Nguyễn Văn A", "nguyenvana@example.com", 20),
                new Student("Trần Thị B", "tranthib@example.com", 21),
                new Student("Lê Văn C", "levanc@example.com", 22),
                new Student("Phạm Thị D", "phamthid@example.com", 19),
                new Student("Hoàng Văn E", "hoangvane@example.com", 23),
                new Student("Vũ Thị F", "vuthif@example.com", 20),
                new Student("Đặng Văn G", "dangvang@example.com", 21),
                new Student("Bùi Thị H", "buithih@example.com", 22)
            );
            
            studentRepository.saveAll(sampleStudents);
            logger.info("Đã tạo {} students mẫu thành công!", sampleStudents.size());
        } else {
            long count = studentRepository.count();
            logger.info("Đã có {} students trong database. Bỏ qua việc tạo dữ liệu mẫu.", count);
        }
    }
}

