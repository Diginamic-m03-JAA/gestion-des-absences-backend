package fr.digi.absences.repository;

import fr.digi.absences.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmployeeRepo extends JpaRepository<Employee,Long>{
    Optional<Employee> findByEmail(String aLong);
}
