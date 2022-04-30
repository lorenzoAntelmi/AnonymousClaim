package com.claim.database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.claim.model.Account;

/** Manipulates accounts in the database.
 * @author Deborah Vanzin
*/

@Repository
public interface AccountRepository extends JpaRepository<Account, Integer> {
	boolean existsByUsername(String username);
	boolean existsByEmail(String email);
	/** Checking if entity with given Username/E-Mail exists
	 * in the database
	*/
}
