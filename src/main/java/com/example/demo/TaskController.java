package com.example.demo;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@RestController("/tasks")
public class TaskController {
    private final TaskRepository repository;

    @Autowired
    public TaskController(TaskRepository repository) {
        this.repository = repository;
    }

    @PostMapping()
    @ResponseBody
    ResponseEntity<?> create(@RequestBody TaskDto taskDto) {
        Task task = new Task(taskDto.getTitle());
        task.setDescription(taskDto.getDescription());


        Task savedTask = repository.save(task);
        return new ResponseEntity<>(savedTask.getId(), HttpStatus.OK);

    }

    @GetMapping("/{id}")
    @ResponseBody
    ResponseEntity<?> read(@PathVariable Long id) {
        Optional<Task> optionalTask = repository.findById(id);
        if (!optionalTask.isPresent()) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);

        } else {
            return new ResponseEntity<>(optionalTask.get().toDto(), HttpStatus.OK);
        }
    }

    @GetMapping
    @ResponseBody
    ResponseEntity<?> findAll(@PathVariable Long id) {
        Iterable<Task> tasks = repository.findAll();
        List<TaskDto> taskDtoList = StreamSupport.stream(tasks.spliterator(), false)
                .map(Task::toDto).collect(Collectors.toList());

        return new ResponseEntity<>(taskDtoList, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @ResponseBody
    ResponseEntity<?> update(@PathVariable Long id, @RequestBody TaskDto taskDto) {
        TaskStatus status;
        try {
            status = TaskStatus.valueOf(taskDto.getStatus());
        } catch (IllegalArgumentException ex) {
            return new ResponseEntity<>("Available statuses are: CREATED, APPROVED, REJECTED, BLOCKED, DONE.", HttpStatus.BAD_REQUEST);
        }
        Optional<Task> optionalTask = repository.findById(id);

        if (optionalTask.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);

        } else {
            Task task = optionalTask.get();
            task.setTitle(taskDto.getTitle());
            task.setDescription(taskDto.getDescription());
            task.setTaskStatus(status);

            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        Optional<Task> optionalTask = repository.findById(id);
        if (optionalTask.isEmpty()) {
            return new ResponseEntity<>(null, HttpStatus.NO_CONTENT);

        } else {
            repository.delete(optionalTask.get());
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
    }
}


