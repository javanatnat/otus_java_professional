package ru.otus.crm.repository;

import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClientDeepExtractor {
    private static final String CLIENT_ID      = "client_id";
    private static final String CLIENT_NAME    = "client_name";
    private static final String ADDRESS_ID     = "address_id";
    private static final String ADDRESS_STREET = "address_street";
    private static final String PHONE_ID       = "phone_id";
    private static final String PHONE_NUMBER   = "phone_number";

    private final ResultSet rs;

    public ClientDeepExtractor(ResultSet rs) {
        this.rs = rs;
    }

    public boolean next() throws SQLException {
        return rs.next();
    }

    public long getClientId() throws SQLException {
        return rs.getLong(CLIENT_ID);
    }

    public long getAddressId() throws SQLException {
        return rs.getLong(ADDRESS_ID);
    }

    public Client getClient() throws SQLException {
        return new Client(getClientId(), getClientName());
    }

    public Address getAddressIfExists() throws SQLException {
        if (getAddressId() == 0) {
            return null;
        }
        return new Address(getAddressId(), getAddressStreet());
    }

    public Phone getPhoneIfExists() throws SQLException {
        if (getPhoneId() == 0) {
            return null;
        }
        return new Phone(getPhoneId(), getPhoneNumber(), getClientId());
    }

    public long getPhoneId() throws SQLException {
        return rs.getLong(PHONE_ID);
    }

    public String getClientName() throws SQLException {
        return rs.getString(CLIENT_NAME);
    }

    public String getAddressStreet() throws SQLException {
        return rs.getString(ADDRESS_STREET);
    }

    public String getPhoneNumber() throws SQLException {
        return rs.getString(PHONE_NUMBER);
    }
}
