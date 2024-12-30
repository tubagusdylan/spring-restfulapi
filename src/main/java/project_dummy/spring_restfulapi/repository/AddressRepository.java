package project_dummy.spring_restfulapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import project_dummy.spring_restfulapi.entity.Address;
import project_dummy.spring_restfulapi.entity.Contact;

@Repository
public interface AddressRepository extends JpaRepository<Address, String>{

    Optional<Address> findFirstByContactAndId(Contact contact, String id);

    List<Address> findAllByContact(Contact contact);
}
