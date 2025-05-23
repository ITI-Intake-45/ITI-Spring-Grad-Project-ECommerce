package gov.iti.jet.ewd.ecom.entity;


import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
//make conflict
//@Data
@ToString(exclude = {"orders", "cart"})
@EqualsAndHashCode(exclude = {"orders", "cart"})
@Entity
@Table(name = "users", indexes = {
        @Index(columnList = "email"),
        @Index(columnList = "phone")
})
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private int userId;

    @NonNull
    @Email(regexp = ".+@.+\\..+")
    @Column(nullable = false, unique = true)
    private String email;

    @NonNull
    @Column(nullable = false)
    private String password;

    @NonNull
    @Column(nullable = false)
    private String name;

    @NonNull
    @Column(nullable = false)
    private String address;

    @NonNull
    @PositiveOrZero
    @Column(name = "credit_balance", nullable = false)
    private double creditBalance;

    @NonNull
    @Pattern(regexp = "^\\+?[0-9]{8,15}$")
    @Column(nullable = false, unique = true)
    private String phone;

    @OneToOne(mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.REMOVE}, orphanRemoval = true)
    private Cart cart;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Order> orders = new ArrayList<>();

}
