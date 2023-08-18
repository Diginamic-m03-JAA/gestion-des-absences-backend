package fr.digi.absences.mapper;

import fr.digi.absences.dto.EmployeCreationDto;
import fr.digi.absences.dto.EmployeeDto;
import fr.digi.absences.entity.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class EmployeeMap {

    private final PasswordEncoder passwordEncoder;

    public EmployeeDto toEmployeeDto(Employee employee){
        return EmployeeDto.builder()
                .id(employee.getId())
                .nom(employee.getNom())
                .prenom(employee.getPrenom())
                .email(employee.getEmail())
                .role(employee.getRole())
                .build();
    }

    public Employee toEmployee(EmployeCreationDto employeCreationDto) {
        return Employee.builder()
                .email(employeCreationDto.getEmail())
                .prenom(employeCreationDto.getPrenom())
                .role(employeCreationDto.getRole())
                .nom(employeCreationDto.getNom())
                .password(passwordEncoder.encode(employeCreationDto.getPassword()))
                .build();
    }

}
