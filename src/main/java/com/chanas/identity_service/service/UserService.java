package com.chanas.identity_service.service;

import com.chanas.identity_service.dto.request.UserCreationRequest;
import com.chanas.identity_service.dto.request.UserUdateRequest;
import com.chanas.identity_service.dto.response.UserResponse;
import com.chanas.identity_service.entity.User;
import com.chanas.identity_service.enums.Role;
import com.chanas.identity_service.exception.AppException;
import com.chanas.identity_service.exception.ErrorCode;
import com.chanas.identity_service.mapper.UserMapper;
import com.chanas.identity_service.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserService {
   UserRepository userRepository;
   UserMapper userMapper;
   PasswordEncoder passwordEncoder;

   public UserResponse createUser(UserCreationRequest request){

       if (userRepository.existsByUsername(request.getUsername()))
           throw new AppException(ErrorCode.USER_EXISTED);

       User user = userMapper.toUser(request);
       user.setPassword(passwordEncoder.encode(user.getPassword()));

       HashSet<String> roles = new HashSet<>();
       roles.add(Role.USER.name());

       user.setRole(roles);

       return userMapper.toUserResponse(userRepository.save(user));
   }

   public void deleteUser(String userId){
       userRepository.deleteById(userId);
   }

   public UserResponse updateUser(String userId,UserUdateRequest request){
       User user = userRepository.findById(userId)
               .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

       userMapper.updateUser(user, request);

       return userMapper.toUserResponse(userRepository.save(user));
   }

   @PreAuthorize("hasRole('ADMIN')")
   public List<UserResponse> getUsers(){
       log.info("In method get Users");
       return userRepository.findAll().stream().map(userMapper::toUserResponse).toList();
   }

   @PostAuthorize("returnObject.username == authentication.name")
   public UserResponse getUser(String id){
       log.info("In method get user by id");
       return userMapper.toUserResponse(userRepository.findById(id)
               .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND)));
   }

   public UserResponse getMyInfo(){
       var context = SecurityContextHolder.getContext();
       String name = context.getAuthentication().getName();

       User user = userRepository.findByUsername(name).orElseThrow(
               () -> new AppException(ErrorCode.USER_NOT_FOUND));

       return userMapper.toUserResponse(user);
   }
}
