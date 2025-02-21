package com.ftn.MyHousebackend.controller;

import com.ftn.MyHousebackend.dto.UserDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import net.bytebuddy.utility.RandomString;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.security.auth.login.LoginException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ftn.MyHousebackend.model.User;
import com.ftn.MyHousebackend.model.UserTokenState;
import com.ftn.MyHousebackend.security.security.TokenUtils;
import com.ftn.MyHousebackend.security.security.authentication.JwtAuthenticationRequest;
import com.ftn.MyHousebackend.service.CustomUserDetailsService;
import com.ftn.MyHousebackend.service.UserService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController
@RequestMapping(value = "api/auth", produces = MediaType.APPLICATION_JSON_VALUE)
public class AuthenticationController {

	private static final Logger LOG = LoggerFactory.getLogger(AuthenticationController.class);

    @Autowired
    private TokenUtils tokenUtils;
    
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomUserDetailsService userDetailsService;

    @Autowired
    private UserService userService;

    //log in endpoint
    @PostMapping("/login")
	public ResponseEntity<UserTokenState> createAuthenticationToken(@RequestBody JwtAuthenticationRequest authenticationRequest,
			HttpServletResponse response) throws LoginException {

		LOG.info("Received request for login");
		Authentication authentication;
		try{
			authentication = authenticationManager
					.authenticate(new UsernamePasswordAuthenticationToken(authenticationRequest.getUsername(),
							authenticationRequest.getPassword()));

			LOG.info("Authentication passed");

			userService.removeFailedLogins(authenticationRequest.getUsername());

			// Ubaci korisnika u trenutni security kontekst
			SecurityContextHolder.getContext().setAuthentication(authentication);

			// Kreiraj token za tog korisnika
			User user = (User) authentication.getPrincipal();
			String fingerprint = tokenUtils.generateFingerprint();
			String jwt = tokenUtils.generateToken(user.getUsername(), fingerprint);
			int expiresIn = tokenUtils.getExpiredIn();

			UserTokenState userTokenState = new UserTokenState(jwt, expiresIn);
			userTokenState.setUsername(user.getUsername());
			userTokenState.setRole(user.getRole().name());

			// Kreiraj cookie
			// String cookie = "__Secure-Fgp=" + fingerprint + "; SameSite=Strict; HttpOnly; Path=/; Secure";  // kasnije mozete probati da postavite i ostale atribute, ali tek nakon sto podesite https
			String cookie = "Fingerprint=" + fingerprint + "; HttpOnly; Path=/";

			HttpHeaders headers = new HttpHeaders();
			headers.add("Set-Cookie", cookie);

			// Vrati token kao odgovor na uspesnu autentifikaciju
			return ResponseEntity.ok().headers(headers).body(userTokenState);
		}
		catch (Exception e){
			if (userService.doesUserExist(authenticationRequest.getUsername())){
				LOG.info("Adding failed login to user: "+authenticationRequest.getUsername());
				UserDTO editedUser = userService.addFailedLogin(authenticationRequest.getUsername());
				if (editedUser.isBlocked()) return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
				return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
			}
			LOG.info("Authentication fail caught!");
			throw new LoginException("Bad creditentials!");
		}


//		if (!authentication.isAuthenticated() && userService.doesUserExist(authenticationRequest.getUsername())){
//			LOG.info("Adding failed login to user: "+authenticationRequest.getUsername());
//			userService.addFailedLogin(authenticationRequest.getUsername());
//		}
//		LOG.info("Authentication passed");
//
//		// Ubaci korisnika u trenutni security kontekst
//		SecurityContextHolder.getContext().setAuthentication(authentication);
//
//		// Kreiraj token za tog korisnika
//		User user = (User) authentication.getPrincipal();
//        String fingerprint = tokenUtils.generateFingerprint();
//        String jwt = tokenUtils.generateToken(user.getUsername(), fingerprint);
//        int expiresIn = tokenUtils.getExpiredIn();
//
//		UserTokenState userTokenState = new UserTokenState(jwt, expiresIn);
//		userTokenState.setUsername(user.getUsername());
//		userTokenState.setRole(user.getRole().name());
//
//        // Kreiraj cookie
//        // String cookie = "__Secure-Fgp=" + fingerprint + "; SameSite=Strict; HttpOnly; Path=/; Secure";  // kasnije mozete probati da postavite i ostale atribute, ali tek nakon sto podesite https
//        String cookie = "Fingerprint=" + fingerprint + "; HttpOnly; Path=/";
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.add("Set-Cookie", cookie);
//
//        // Vrati token kao odgovor na uspesnu autentifikaciju
//        return ResponseEntity.ok().headers(headers).body(userTokenState);
	}

	@PostMapping("/logout")
	public void logout(HttpServletResponse response) {
		// create a cookie
		Cookie cookie = new Cookie("accessToken", null);
		cookie.setMaxAge(0);
		// cookie.setSecure(true);
		cookie.setHttpOnly(true);
		cookie.setPath("/");

		//add cookie to response
		response.addCookie(cookie);
	}

    // //refresh endpoint
    // @PostMapping(value = "/refresh")
	// public void refreshAuthenticationToken(HttpServletRequest request) {

	// 	String token = tokenUtils.getToken(request);
	// 	String username = this.tokenUtils.getUsernameFromToken(token);
	// 	User user = (User) this.userDetailsService.loadUserByUsername(username);

	// 	if (this.tokenUtils.canTokenBeRefreshed(token)) {
	// 		String jwt = tokenUtils.refreshToken(token);
	// 		Cookie cookie = new Cookie("accessToken", jwt);
	// 		cookie.setMaxAge(7 * 24 * 60 * 60); // Expires in 7 days
	// 		// cookie.setSecure(true);
	// 		cookie.setHttpOnly(true);
	// 		cookie.setPath("/"); // Global cookie accessible everywhere
	// 	}
	// }

    
}
