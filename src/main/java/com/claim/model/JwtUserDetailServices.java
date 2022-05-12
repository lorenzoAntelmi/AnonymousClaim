package com.claim.model;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 *
 * @author Lorenzo Antelmi
 * implements the Spring Security UserDetailsService interface
 * It overrides the loadUserByUsername for fetching user details from the database using the username
 *
 */

@Service
public class JwtUserDetailServices implements UserDetailsService {

	/**
	 * This methods checks if the username exists in the Database
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		if ("username".equals(username)) {

			/**
			 *do we need to write Account instead User, because we don't have a User-Class?
			 */
			return new User("username", "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6",
					new ArrayList<>());
		} else {
			throw new UsernameNotFoundException("User not found with username: " + username);
		}
	}

}
