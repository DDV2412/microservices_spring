package com.ipmugo.userservice.service;

import com.ipmugo.userservice.model.Role;
import com.ipmugo.userservice.model.UserRole;
import com.ipmugo.userservice.repository.RoleRepository;
import com.ipmugo.userservice.utils.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    /***
     * Create Role
     */

    public Role createRole(Role role) throws CustomException {
        try{

            Optional<Role> roleCheck = roleRepository.findByName(role.getName());

            if(roleCheck.isPresent()){
                throw new CustomException("Role name is exits", HttpStatus.NOT_FOUND);
            }

            return roleRepository.save(role);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /***
     * Find AllRole by Name
     */
    public Iterable<Role> getAllRole() throws CustomException{
        try{
            return roleRepository.findAll();
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /***
     * Find Role by Name
     */
    public Role getRole(UserRole roleEnum) throws CustomException{
        try{
            Optional<Role> roleCheck = roleRepository.findByName(roleEnum);

            if(roleCheck.isEmpty()){
                throw new CustomException("Role name not found", HttpStatus.NOT_FOUND);
            }
            return roleCheck.get();
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /***
     * Update Role By roleName
     */
    public Role updateRole(Role role, UserRole roleEnum) throws CustomException{
        try{
            Role updateRole = this.getRole(roleEnum);
            updateRole.setName(role.getName());

            return roleRepository.save(updateRole);
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    /***
     * Delete Role by Role Name
     */
    public void deleteRole(UserRole roleEnum) throws CustomException{
        try{
            Role updateRole = this.getRole(roleEnum);

            roleRepository.deleteById(updateRole.getId());
        }catch (Exception e){
            throw new CustomException(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }
}
