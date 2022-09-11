package challenges.challenges.repository.challenge;


import challenges.challenges.domain.Challenge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.*;

public interface ChallengeSearchRepository extends JpaRepository<Challenge, Long> {

    @Query(value = "SELECT c FROM Challenge c WHERE c.c_title LIKE %:keyword%")
    List<Challenge> findByTitleContaining(@Param("keyword")String keyword);

    @Query(value = "SELECT c FROM Challenge c WHERE c.c_donation_destination LIKE %:destination%")
    List<Challenge> findByDestinationContaining(@Param("destination") String destination);

}
