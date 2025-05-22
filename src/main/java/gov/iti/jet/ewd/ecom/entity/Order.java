package gov.iti.jet.ewd.ecom.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.*;

@Getter
@Setter
//@Data
@ToString(exclude = {"user", "items"}) // Exclude both user and items
@EqualsAndHashCode(exclude = {"user", "items"}) // Exclude from equals/hashCode
@Entity
@Table(name = "orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private int orderId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_order_user"))
    private User user;

    @Min(0)
    @Column(name = "total_price", nullable = false)
    private double totalPrice;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();


    @Transient
    public double getOrderTotalPrice() {
        return items.stream()
                .map(item -> item.getItemPrice() * item.getQuantity())
                .reduce(0.0, Double::sum);
    }
}