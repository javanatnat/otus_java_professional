package ru.otus.crm.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.NonNull;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Table("client")
public class Client implements Cloneable {

    @Id
    private Long id;
    @NonNull
    private String name;
    private Long addressId;
    @Transient
    private Address address;
    @MappedCollection(idColumn = "client_id")
    private Set<Phone> phones;

    public Client(String name) {
        this.id = null;
        this.name = name;
        this.phones = new HashSet<>();
    }

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
        this.phones = new HashSet<>();
    }

    public Client(Long id, String name, Long addressId) {
        this.id = id;
        this.name = name;
        this.addressId = addressId;
        this.phones = new HashSet<>();
    }

    public Client(String name, Address address, Set<Phone> phones) {
        this.id = null;
        this.name = name;
        setAddress(address);
        setPhones(phones);
    }

    public Client(Long id, String name, Address address, Set<Phone> phones) {
        this.id = id;
        this.name = name;
        setAddress(address);
        setPhones(phones);
    }

    @PersistenceConstructor
    public Client(Long id, String name, Long addressId, Set<Phone> phones) {
        this.id = id;
        this.name = name;
        this.addressId = addressId;
        setPhones(phones);
    }

    @Override
    public Client clone() {
        return new Client(this.id, this.name, this.address, this.phones);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getAddressId() {
        return addressId;
    }

    public void setAddressId(Long addressId) {
        this.addressId = addressId;
    }

    public Address getAddress() {
        return address;
    }

    public String getAddressStr() {
        if (address != null) {
            return address.getStreet();
        }
        return null;
    }

    public void setAddress(Address address) {
        this.address = address;
        if (address != null) {
            this.addressId = address.getId();
        }
    }

    public Set<Phone> getPhones() {
        return phones;
    }

    public List<String> getPhoneNumbers() {
        if (phones != null) {
            return phones
                    .stream()
                    .map(Phone::getNumber)
                    .toList();
        }
        return new ArrayList<>();
    }

    public void setPhones(Set<Phone> phones) {
        this.phones = phones;
        setClientToPhones();
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address=" + address +
                ", phones=" + phones +
                '}';
    }

    private void setClientToPhones() {
        if (phones != null) {
            phones.forEach(p -> p.setClientId(this.id));
        }
    }

    public void addPhone(Phone phone) {
        phones.add(phone);
        phone.setClientId(this.id);
    }

    public void removePhone(Phone phone) {
        phones.remove(phone);
        phone.setClientId(null);
    }

    public void removePhones() {
        phones.forEach(p -> p.setClientId(null));
        phones.clear();
    }
}
