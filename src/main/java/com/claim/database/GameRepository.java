package com.claim.database;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.claim.model.Game;

/** Manipulates games in the database.
 * @author Deborah Vanzin
 * -locating a list of games (all, by phase & playerA,
 * and by phase)
 *
 * source: SpringDataFramework
*/

@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {
	@Override
	List<Game> findAll();

	/**@Query of findByPhaseAndPlayerA would be:
	 * ("select g from Game where g.phase = 0 and g.playerA = 1")
	 * List<Game> testSearchOpen();
	*/

	List<Game> findByPhaseAndPlayerA(int phase, int player);
	List<Game> findByPhase(int phase);

	/** locating an element or a list of elements
	 * with findBy___
	*/
}
