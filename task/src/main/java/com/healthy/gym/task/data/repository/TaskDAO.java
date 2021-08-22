package com.healthy.gym.task.data.repository;

import com.healthy.gym.task.data.document.TaskDocument;
import com.healthy.gym.task.data.document.UserDocument;
import com.healthy.gym.task.enums.Priority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;

public interface TaskDAO  extends MongoRepository<TaskDocument, String> {

    TaskDocument findByTaskId(String taskId);

    Page<TaskDocument> findAllByDueDateBetween(LocalDate startDueDate, LocalDate endDueDate, Pageable pageable);

    Page<TaskDocument> findAllByDueDateBetweenAndEmployeeAndPriorityEquals(
            LocalDate startDueDate,
            LocalDate endDueDate,
            UserDocument employee,
            Priority priority,
            Pageable pageable
    );

    Page<TaskDocument> findAllByDueDateBetweenAndEmployee(
            LocalDate startDueDate,
            LocalDate endDueDate,
            UserDocument employee,
            Pageable pageable
    );

    Page<TaskDocument> findAllByDueDateBetweenAndPriorityEquals(
            LocalDate startDueDate,
            LocalDate endDueDate,
            Priority priority,
            Pageable pageable
    );
}
