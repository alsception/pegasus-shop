package org.alsception.pegasus.features.users;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends JpaRepository<PGSUser, Long> 
{
    Optional<PGSUser> findByUsernameIgnoreCase(String username);

    List<PGSUser> findByUsernameContainingIgnoreCaseOrderByCreatedDesc(String username);
    
    //this one causes exception
    /*List<PGSUser> findByUsernameContainingOrFirstNameContainingOrLastNameContainingOrRoleContaining(String search);*/
}