package _dduartess.travelaencymanagement.entities.contract;

import java.math.BigDecimal;
import _dduartess.travelaencymanagement.entities.customers.Customer;
import _dduartess.travelaencymanagement.entities.trip.RoomType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "tb_contract_passengers",
        uniqueConstraints = @UniqueConstraint(columnNames = {"contract_id", "customer_id"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ContractPassenger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contract_id", nullable = false)
    private Contract contract;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private Customer customer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ChargeType chargeType;

    @Enumerated(EnumType.STRING)
    private RoomType roomType;

    private BigDecimal priceSnapshot;

    @Column(columnDefinition = "TEXT")
    private String notes;
}
