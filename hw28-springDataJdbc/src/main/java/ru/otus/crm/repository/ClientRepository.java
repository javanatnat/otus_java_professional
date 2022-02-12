package ru.otus.crm.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.otus.crm.model.Client;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends CrudRepository<Client, Long> {
    String SELECT_CLIENT_DEEP = """
            select c.id     as client_id,
                   c.name   as client_name,
                   a.id     as address_id,
                   a.street as address_street,
                   p.id     as phone_id,
                   p.number as phone_number
            from client c
                     left outer join address a
                        on c.address_id = a.id
                     left outer join phone p
                        on c.id = p.client_id""";

    @Override
    @Query(value = SELECT_CLIENT_DEEP + " order by c.id",
            resultSetExtractorClass = ClientResultSetExtractorList.class
    )
    List<Client> findAll();

    @Override
    @Query(value = SELECT_CLIENT_DEEP + " where c.id = :id ",
            resultSetExtractorClass = ClientResultSetExtractorOne.class)
    Optional<Client> findById(@Param("id") Long id);
}
