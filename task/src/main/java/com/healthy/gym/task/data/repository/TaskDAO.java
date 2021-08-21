package com.healthy.gym.task.data.repository;

import com.healthy.gym.task.data.document.TaskDocument;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDate;
import java.util.List;

public interface TaskDAO  extends MongoRepository<TaskDocument, String> {

    TaskDocument findByTaskId(String taskId);

    Page<List<TaskDocument>> findAllByDueDateBetween(LocalDate startDueDate, LocalDate endDueDate, Pageable pageable);
}
