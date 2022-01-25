package ru.otus.crm;

import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        this.address = client.getAddress().getStreet();
        this.phones = client.getPhones()
                .stream()
                .map(Phone::getNumber)
                .collect(Collectors.joining(DELIM));
    }

    public Client getClient() {
        List<String> numbers = getNumbers();
        List<Phone> phoneList = getPhones(numbers);

        return new Client(
                null,
                this.name,
                new Address(null, this.address),
                phoneList
        );
    }

    private List<String> getNumbers() {
        return Arrays.asList(phones.replaceAll(SPACES, EMPTY).trim().split(DELIM));
    }

    private static List<Phone> getPhones(List<String> numbers) {
        List<Phone> phoneList = new ArrayList<>();
        for(String number : numbers) {
            phoneList.add(new Phone(null, number));
        }
        return phoneList;
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
