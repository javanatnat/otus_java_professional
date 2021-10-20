package homework;

import java.util.*;

public class CustomerService {

    //todo: 3. надо реализовать методы этого класса
    //важно подобрать подходящую Map-у, посмотрите на редко используемые методы, они тут полезны
    private final TreeMap<Customer, String> customerData;

    public CustomerService() {
        customerData = new TreeMap<>(Comparator.comparing(Customer::getScores));
    }

    public Map.Entry<Customer, String> getSmallest() {
        Map.Entry<Customer, String> smallest = customerData.firstEntry();
        return getImmutableEntry(smallest);
    }

    private Map.Entry<Customer, String> getImmutableEntry(Map.Entry<Customer, String> entry) {
        if (entry == null) {
            return null;
        }

        Customer returnKey = getCustomerCopy(entry.getKey());
        return new AbstractMap.SimpleImmutableEntry<>(returnKey, entry.getValue());
    }

    private Customer getCustomerCopy(Customer src) {
        if (src == null) {
            return null;
        }

        return new Customer(
                src.getId(),
                src.getName(),
                src.getScores());
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        if (customer != null) {
            Map.Entry<Customer, String> higher = customerData.higherEntry(customer);
            return getImmutableEntry(higher);
        }
        return null;
    }

    public void add(Customer customer, String data) {
        if (customer != null) {
            customerData.put(customer, data);
        }
    }
}
