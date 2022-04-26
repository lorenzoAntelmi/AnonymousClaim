package com.claim.database;

import java.util.List;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.claim.model.Game;
//SpringDataFrameWork
@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {
	List<Game> findAll();
	
	//@Query("select g from Game where g.phase = 0 and g.playerA = 1")
	//List<Game> testSearchOpen();
	
	List<Game> findByPhaseAndPlayerA(int phase, int player);
	
	List<Game> findByPhase(int phase);
}
