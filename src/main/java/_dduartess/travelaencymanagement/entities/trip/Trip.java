package _dduartess.travelaencymanagement.entities.trip;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import _dduartess.travelaencymanagement.entities.customers.Customer;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "tb_trips")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Trip {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O destino é obrigatório")
    @Column(nullable = false)
    private String destination;

    @NotNull(message = "A data de início é obrigatória")
    @FutureOrPresent(message = "A data de início não pode ser menor que a data atual")
    @Column(nullable = false)
    private LocalDate startDate;

    @NotNull(message = "A data de fim é obrigatória")
    @Column(nullable = false)
    private LocalDate endDate;

    @ElementCollection
    @CollectionTable(name = "trip_room_prices", joinColumns = @JoinColumn(name = "trip_id"))
    @MapKeyEnumerated(EnumType.STRING)
    @MapKeyColumn(name = "room_type")
    @Column(name = "price", nullable = false)
    private Map<RoomType, BigDecimal> roomPrices = new HashMap<>();

    @ManyToMany
    @JoinTable(name = "trip_passengers", joinColumns = @JoinColumn(name = "trip_id"), inverseJoinColumns = @JoinColumn(name = "customer_id"))
    private Set<Customer> passengers = new HashSet<>();

    @AssertTrue(message = "A data de fim não pode ser menor que a data de início")
    public boolean isEndDateValid() {
        if (startDate == null || endDate == null) return true;
        return !endDate.isBefore(startDate);
    }

    @AssertTrue(message = "É necessário informar pelo menos um valor de quarto e todos devem ser maiores que zero")
    public boolean isRoomPricesValid() {
        if (roomPrices == null || roomPrices.isEmpty()) return false;
        return roomPrices.values().stream()
                .allMatch(price -> price != null && price.compareTo(BigDecimal.ZERO) > 0);
    }
}
