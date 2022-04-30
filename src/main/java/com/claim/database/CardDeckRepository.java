package com.claim.database;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.claim.model.CardDeck;

/** Represents the CardDeckRepository for database
 * @author Rocco Saracino und Valentina Caldana
 * -basic structure for CRUD
*/

@Repository
public interface CardDeckRepository extends JpaRepository<CardDeck, Integer> {

}
