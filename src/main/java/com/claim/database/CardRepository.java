package com.claim.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.claim.model.Card;

/** Represents the CardRepository for database
 * @author Rocco Saracino und Valentina Caldana
 * -basic structure for CRUD
*/

@Repository
public interface CardRepository extends CrudRepository<Card, Integer> {

}
