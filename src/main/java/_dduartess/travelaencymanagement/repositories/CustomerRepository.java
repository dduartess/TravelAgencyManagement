package _dduartess.travelaencymanagement.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import _dduartess.travelaencymanagement.entities.customers.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long>{

    Optional<Customer> findByDocumentNumber(String documentNumber);

}
