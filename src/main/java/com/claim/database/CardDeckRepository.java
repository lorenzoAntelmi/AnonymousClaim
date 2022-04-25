package com.claim.database;

import org.springframework.stereotype.Repository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.claim.model.Account;
import com.claim.model.CardDeck;

/** Represents the CardDeckRepository for database
 * @author Rocco Saracino und Valentina Caldana
 * -basic structure for CRUD
*/

@Repository
public interface CardDeckRepository extends CrudRepository<CardDeck, Integer> {

}
