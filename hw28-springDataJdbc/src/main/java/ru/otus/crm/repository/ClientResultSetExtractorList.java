package ru.otus.crm.repository;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClientResultSetExtractorList implements ResultSetExtractor<List<Client>> {
    @Override
    public List<Client> extractData(ResultSet rs) throws SQLException, DataAccessException {

        List<Client> clientList = new ArrayList<>();
        long prevClientId = 0;
        long prevAddressId = 0;
        Client client = null;

        ClientDeepExtractor e = new ClientDeepExtractor(rs);

        while (e.next()) {
            long clientId = e.getClientId();
            long addressId = e.getAddressId();

            if (prevClientId == 0 || prevClientId != clientId) {
                client = e.getClient();
                clientList.add(client);
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
        return clientList;
    }
}
