package cz.cvut.nss.investmentmanagementsystem.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Data
public class Portfolio extends AbstractEntity implements Serializable {
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private BigDecimal totalValue;
    @OneToMany(mappedBy = "portfolio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Asset> assets;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
