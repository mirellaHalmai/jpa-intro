package com.codecool.jpa.repository;

import com.codecool.jpa.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AddressRepository extends JpaRepository<Address, Long> {

    @Query("update Address a set a.country = 'UK' where a.id in " +
            "(select s.address.id from Student s where s.name like :name)")
    @Modifying(clearAutomatically = true)
    int updateAllToUKByStudentName(@Param("name") String name);

}
