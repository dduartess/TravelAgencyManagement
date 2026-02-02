package _dduartess.travelaencymanagement.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import _dduartess.travelaencymanagement.entities.customers.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long>{

}
