package com.claim.database;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import com.claim.model.Account;

/** Manipulates accounts in the database.
 * @author Deborah Vanzin
*/
@Repository
public interface AccountRepository extends CrudRepository<Account, Integer> {
	boolean existsByUsername(String username);
	boolean existsByEmail(String email);
}
