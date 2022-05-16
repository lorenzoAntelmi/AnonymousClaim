package com.claim.model;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.claim.database.AccountRepository;

/**
 *
 * @author Lorenzo Antelmi
 * implements the Spring Security UserDetailsService interface
 * It overrides the loadUserByUsername for fetching user details from the database using the username
 *
 */

@Service
public class JwtUserDetailServices implements UserDetailsService {
	
	@Autowired
	private AccountRepository accountRepository;

	/**
	 * This methods checks if the username exists in the Database
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<Account> mayBeAccount = accountRepository.findByEmail(username);
		if (mayBeAccount.isPresent()) {
				Account account = mayBeAccount.get();
			/**
			 *do we need to write Account instead User, because we don't have a User-Class?
			 */
			return new User(username, account.getPasswordHash(), new ArrayList<>());
		} else {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
	}

}
