package app.Errand;

import app.User.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface ErrandRepository extends JpaRepository<Errand, UUID> {
    Errand findByUser(User user);

    List<Errand> findByUserAndErrandStatus(User user, ErrandStatus errandStatus);

    List<Errand> findByErrandStatus(ErrandStatus errandStatus);
}
