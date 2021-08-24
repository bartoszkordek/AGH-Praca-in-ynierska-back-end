package com.healthy.gym.task.controller;

import com.healthy.gym.task.component.Translator;
import com.healthy.gym.task.dto.TaskDTO;
import com.healthy.gym.task.exception.*;
import com.healthy.gym.task.service.TaskService;
import com.healthy.gym.task.validation.ValidDateFormat;
import com.healthy.gym.task.validation.ValidIDFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping(
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
)
public class GeneralTaskController {

    private static final String INTERNAL_ERROR_EXCEPTION = "exception.internal.error";
    private static final String EMPLOYEE_NOT_FOUND_EXCEPTION = "exception.employee.not.found";
    private static final String INVALID_PRIORITY_EXCEPTION = "exception.invalid.priority";
    private final Translator translator;
    private final TaskService taskService;

    @Autowired
    public GeneralTaskController(
            Translator translator,
            TaskService taskService
    ){
        this.translator = translator;
        this.taskService = taskService;
    }


    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')  or principal==#userId")
    @GetMapping("/page/{page}")
    public List<TaskDTO> getTasks(
            @ValidDateFormat @RequestParam(value = "startDueDate", required = false) final String startDueDate,
            @ValidDateFormat @RequestParam(value = "endDueDate", required = false) final String endDueDate,
            @ValidIDFormat @RequestParam(value = "userId", required = false) final String userId,
            @RequestParam(value = "priority", required = false) final String priority,
            @RequestParam(defaultValue = "10", required = false) final int size,
            @PathVariable("page") final int page
    ){
        try{
            Pageable paging = PageRequest.of(page, size);
            return taskService.getTasks(startDueDate, endDueDate, userId, priority, paging);

        } catch (StartDateAfterEndDateException exception) {
            String reason = translator.toLocale("exception.start.after.end");
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (NoTasksException exception) {
            String reason = translator.toLocale("exception.no.tasks");
            throw new ResponseStatusException(HttpStatus.OK, reason, exception);

        } catch (EmployeeNotFoundException exception){
            String reason = translator.toLocale(EMPLOYEE_NOT_FOUND_EXCEPTION);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (InvalidPriorityException exception){
            String reason = translator.toLocale(INVALID_PRIORITY_EXCEPTION);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, reason, exception);

        } catch (Exception exception){
            String reason = translator.toLocale(INTERNAL_ERROR_EXCEPTION);
            exception.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, reason, exception);
        }
    }
}
