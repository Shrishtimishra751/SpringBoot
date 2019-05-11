package com.bridgelabz.service;

import java.math.BigInteger;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.bridgelabz.model.User;
import com.bridgelabz.repository.UserRepository;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.crypto.MacProvider;

@Service
@Transactional
public class UserServiceImpl implements UserService {

	@Autowired
	public UserRepository userRep;

	@Override
	public String login(User user) {

		List<User> usrlst = userRep.findByEmailAndPassword(user.getEmail(), user.getPassword());
		System.out.println("SIZE : " + usrlst.size());

		if (usrlst.size() > 0 && usrlst != null) {
			System.out.println("Sucessful login");

			return "Welcome " + usrlst.get(0).getName() + "Jwt--->" + generateToken();
		} else {
			System.out.println("wrong emailid or password");
			return "wrong emailid or password";
		}
	}

	String token = generateToken();
	String output = verifyToken(token);

	@Override
	public User userReg(User user) {
		return userRep.save(user);
	}

	@Override
	public String securePassword(User user) {
		String password = user.getPassword();
		String generatedPassword = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");

			md.update(password.getBytes());
			// Get the hash's bytes
			byte[] bytes = md.digest();
			// digest() method called
			// to calculate message digest of an input
			// and return array of byte
			// byte[] messageDigest = md.digest();

			// Convert byte array into signum representation
			BigInteger no = new BigInteger(1, bytes);

			// Convert message digest into hex value
			String hashtext = no.toString(16);
			System.out.println(hashtext);
			while (hashtext.length() < 32) {
				hashtext = "0" + hashtext;
			}

			return hashtext;
		}

		// For specifying wrong message digest algorithms
		catch (NoSuchAlgorithmException e) {
			System.out.println("Exception thrown" + " for incorrect algorithm: " + e);

			return null;
		}

	}

	private static final Key secret = MacProvider.generateKey(SignatureAlgorithm.HS256);
	private static final byte[] secretBytes = secret.getEncoded();
	private static final String base64SecretBytes = Base64.getEncoder().encodeToString(secretBytes);

	@Override
	public String generateToken() {
		String id = UUID.randomUUID().toString().replace("-", "");
		Date now = new Date();
		Date exp = new Date(System.currentTimeMillis() + (1000 * 30)); // 30 seconds

		String token = Jwts.builder().setId(id).setIssuedAt(now).setNotBefore(now).setExpiration(exp)
				.signWith(SignatureAlgorithm.HS256, base64SecretBytes).compact();

		return token;
	}

	@Override
	public User saveUser(User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String verifyToken(String secretKey) {

		Claims claims = Jwts.parser().setSigningKey(base64SecretBytes).parseClaimsJws(token).getBody();
		System.out.println("----------------------------");
		System.out.println("ID: " + claims.getId());
		System.out.println("Subject: " + claims.getSubject());
		System.out.println("Issuer: " + claims.getIssuer());
		System.out.println("Expiration: " + claims.getExpiration());
		return token;

	}

}
