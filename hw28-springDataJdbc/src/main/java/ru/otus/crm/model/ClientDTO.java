package ru.otus.crm.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.*;
import java.util.stream.Collectors;

public class ClientDTO {
    private static final String SPACES = "\\s+";
    private static final String EMPTY = "";
    private static final String DELIM = ",";

    private Long id;
    private String name;
    private String address;
    private String phones;

    public ClientDTO() {}

    public ClientDTO(Long id, String name, String address, String phones) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phones = phones;
    }

    public ClientDTO(Client client) {
        this.id = client.getId();
        this.name = client.getName();
        this.address = client.getAddressStr();
        var numbers = client.getPhoneNumbers();
        if (numbers.size() > 0) {
            this.phones = String.join(DELIM, numbers);
        }
    }

    @JsonIgnore
    public Client getClient() {
        return new Client(
                null,
                this.name,
                getAddressForClient(),
                getPhonesForClient(getNumbers())
        );
    }

    private Address getAddressForClient() {
        if (this.address != null && !delSpaces(this.address).isEmpty()) {
            return new Address(null, delSpaces(this.address));
        }
        return null;
    }

    private static String delSpaces(String str) {
        return str.replaceAll(SPACES, EMPTY);
    }

    private List<String> getNumbers() {
        if (phones == null) {
            return null;
        }

        String numbers = delSpaces(phones);

        if(numbers.isEmpty() || numbers.replaceAll(DELIM, EMPTY).isEmpty()) {
            return null;
        }

        return Arrays.asList(numbers.split(DELIM));
    }

    private static Set<Phone> getPhonesForClient(List<String> numbers) {
        if (numbers == null) {
            return null;
        }
        return numbers.stream().map(Phone::new).collect(Collectors.toSet());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhones() {
        return phones;
    }

    @Override
    public String toString() {
        return "ClientDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", phones='" + phones + '\'' +
                '}';
    }
}
