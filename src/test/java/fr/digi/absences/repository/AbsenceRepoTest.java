package fr.digi.absences.repository;

import fr.digi.absences.consts.Roles;
import fr.digi.absences.consts.StatutAbsence;
import fr.digi.absences.consts.TypeConge;
import fr.digi.absences.entity.Absence;
import fr.digi.absences.entity.Departement;
import fr.digi.absences.entity.Employee;
import fr.digi.absences.service.JwtService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

import static org.mockito.BDDMockito.*;


/*
1. Créer un contexte Spring
2. Initialiser les beans repositories (Bean AbsenceRepo créé)
3. Appliquer un contexte transactionnel à chaque méthode (@Transactional)
=> rollback à la fin de chaque méthode
*/

@Testcontainers
@Slf4j
@WebMvcTest(ConfigurableApplicationContext.class)
class AbsenceRepoTest {

    @Container
    private static MariaDBContainer container = new MariaDBContainer(DockerImageName.parse("mariadb:latest"));

    @DynamicPropertySource
    static void setUpProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", container::getJdbcUrl);
        registry.add("spring.datasource.username", container::getUsername);
        registry.add("spring.datasource.password", container::getPassword);
    }

    @BeforeAll
    static void startContainer() {
        container.start();
    }

    @MockBean
    TestEntityManager tem;

    @MockBean
    AbsenceRepo absenceRepo;

    @MockBean
    Employee employee;

    @MockBean
    Departement departement;

    @MockBean
    Absence absence;

    @MockBean
    EmployeeRepo employeeRepo;

    @MockBean
    DepartementRepo departementRepo;

    @MockBean
    JwtService jwtService;


    @BeforeEach
    void setUp() {
        LocalDate dateDebut = LocalDate.of(2023, 7, 17);
        LocalDate dateFin = LocalDate.of(2023, 7, 25);

        this.employee = new Employee();
        this.employee.setId(1L);
        this.employee.setNom("Toto");
        this.employee.setPrenom("Latete");
        this.employee.setEmail("toto@gmail.com");
        this.employee.setRole(Roles.EMPLOYEE);
        this.employee.setAbsences(new ArrayList<>());
        this.employee.setAbsenceRejetees(new ArrayList<>());
        List<Absence> absences = this.employee.getAbsences();

        this.departement = new Departement();
        this.departement.setId(1L);
        this.departement.setLibelle("Hérault 34");
        this.departement.setEmployees(new ArrayList<>());
        this.departement.getEmployees().add(this.employee);

        this.absence = new Absence();
        this.absence.setId(1L);
        this.absence.setDateDebut(dateDebut);
        this.absence.setDateFin(dateFin);
        this.absence.setMotif("Congé Juilletiste");
        this.absence.setEmployee(employee);
        this.absence.setTypeConge(TypeConge.SANS_SOLDE);
        this.absence.setStatus(StatutAbsence.ATTENTE_VALIDATION);

        absences.add(this.absence);

    }


    /**
     * Test de la méthode findById() de l'interface AbsenceRepo
     */
    @Sql("classpath*:sql/inserts.sql")
    @Test
    void getAbsenceRepoTest() {
        // Hypothese
        when(this.absenceRepo.getReferenceById(1L)).thenReturn(this.absence);

        // Execution code
        Optional<Absence> byId = absenceRepo.findById(1L);

        // Gestion de l'exception en arrière plan
        doThrow(EntityNotFoundException.class).when(this.absenceRepo).getReferenceById(1L);

        // Vérification du résultat
        Assertions.assertThat(byId).contains(this.absence);

    }

    /**
     *
     */
    @Sql("classpath*:sql/inserts.sql")
    @Test
    void findByDateDemandeAndStatus() {

        // mise en place pour le test

        // Invocation de la méthode

        // Verification du resultat

    }

    /**
     *
     */
    @Sql("classpath*:sql/inserts.sql")
    @Test
    void findAllByAnneeTest() {
        // mise en place pour le test

        // Invocation de la méthode

        // Verification du resultat

    }

    /**
     *
     */
    @Sql("classpath*:sql/inserts.sql")
    @Test
    void getNbAbsencesBetweenDateDebutAndDateFinTest() {

    }

    /**
     *
     */
    @Sql("classpath*:sql/inserts.sql")
    @Test
    void findAbsenceMatchRttEmployeurTest() {

    }

    @AfterAll
    static void stopContainer() {
        container.stop();
    }
}