package com.example.payroll;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.IanaLinkRelations;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
class EmployeeController {

    private final EmployeeRepository repository;
    private final EmployeeModelAssembler assembler;

    EmployeeController(EmployeeRepository repository, EmployeeModelAssembler assembler) {
        this.repository = repository;
        this.assembler = assembler;
    }

    @GetMapping("/employees")
    CollectionModel<EntityModel<Employee>> index() {

        List<EntityModel<Employee>> employees = repository.findAll().stream().map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(employees, linkTo(methodOn(EmployeeController.class).index()).withSelfRel());
    }

    @PostMapping("/employees")
    ResponseEntity<?> store(@RequestBody Employee employee) {
        EntityModel<Employee> entityModel = assembler.toModel(repository.save(employee));
        return ResponseEntity.created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri()).body(entityModel);
    }

    @GetMapping("/employees/{id}")
    EntityModel<Employee> show(@PathVariable Long id) {
        Employee employee = repository.findById(id).orElseThrow(() -> new EmployeeNotFoundException(id));

        return assembler.toModel(employee);
    }

    @PutMapping("/employees/{id}")
    ResponseEntity<?> update(@PathVariable Long id, @RequestBody Employee employee) {
        Employee updatedEmployee = repository.findById(id).map(oldEmployee -> {
            oldEmployee.setName(employee.getName());
            oldEmployee.setRole(employee.getRole());
            return repository.save(oldEmployee);
        }).orElseGet(() -> {
            employee.setId(id);
            return repository.save(employee);
        });

        EntityModel<Employee> entityModel = assembler.toModel(updatedEmployee);

        return ResponseEntity
            .created(entityModel.getRequiredLink(IanaLinkRelations.SELF).toUri())
            .body(entityModel);
    }

    @DeleteMapping("/employees/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        repository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
