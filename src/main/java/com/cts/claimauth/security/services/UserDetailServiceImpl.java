package com.cts.claimauth.security.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cts.claimauth.models.User;
import com.cts.claimauth.repository.UserRepository;


@Service
public class UserDetailServiceImpl implements UserDetailsService {
	
	private static final Logger logger=LoggerFactory.getLogger(UserDetailServiceImpl.class);
  
	@Autowired
	UserRepository userRepository;
	
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		logger.info("inside the loadUserByUsername");
		Long id=Long.parseLong(userId);
		User user=userRepository.findByUserId(id)
				.orElseThrow(()->new UsernameNotFoundException("User not Found with username: "+userId));
		logger.info(user.getUserId().toString());
		return UserDetailsImpl.build(user);
	}

}
