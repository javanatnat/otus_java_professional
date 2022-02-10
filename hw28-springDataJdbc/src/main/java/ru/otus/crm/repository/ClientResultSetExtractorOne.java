package ru.otus.crm.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ClientResultSetExtractorOne implements ResultSetExtractor<Optional<Client>> {
    @Override
    public Optional<Client> extractData(ResultSet rs) throws SQLException, DataAccessException {

        long prevClientId = 0;
        long prevAddressId = 0;
        Client client = null;

        ClientDeepExtractor e = new ClientDeepExtractor(rs);

        while (e.next()) {
            long clientId = e.getClientId();
            long addressId = e.getAddressId();

            if (prevClientId >0 && prevClientId != clientId) {
                break;
            }

            if (prevClientId == 0) {
                client = e.getClient();
                prevClientId = clientId;
            }

            if (prevAddressId == 0 || prevAddressId != addressId) {
                Address address = e.getAddressIfExists();
                if (address != null) {
                    client.setAddress(address);
                }
                prevAddressId = addressId;
            }

            Phone phone = e.getPhoneIfExists();
            if (phone != null) {
                client.addPhone(phone);
            }
        }

        return (client == null) ? Optional.empty() : Optional.of(client);
    }
}
