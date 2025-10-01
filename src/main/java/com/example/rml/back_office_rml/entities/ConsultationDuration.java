import jakarta.persistence.*;

@Entity
@Table(name = "consultation_durations")
public class ConsultationDuration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Integer minutes;           // 15, 20, 30, 45, 60

    @Column(nullable = false)
    private String displayName;        // "15 mins", "30 mins", "1 hour"

    @Column(nullable = false)
    private Boolean active = true;     // Pour désactiver certaines durées

    // Constructeurs, getters, setters
}