package com.example.fighting.service;

import com.example.fighting.model.entity.UserInfo;
import com.example.fighting.model.repository.UserInfoRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Log4j2
public class LoginService implements UserDetailsService {
    @Autowired
    private UserInfoRepository userInfoRepository;
    List<GrantedAuthority> auths = AuthorityUtils.commaSeparatedStringToAuthorityList("role");

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserInfo> UserInfoOp = userInfoRepository.findById(username);
        if(!UserInfoOp.isPresent()){
            String errorMsg = "User " + username + " does not exist";
            log.info(errorMsg);
            throw new UsernameNotFoundException(errorMsg);
        }
        UserInfo userInfo = UserInfoOp.get();
        return new User(userInfo.getUsername(),new BCryptPasswordEncoder().encode(userInfo.getPassword()),auths);
    }
}
