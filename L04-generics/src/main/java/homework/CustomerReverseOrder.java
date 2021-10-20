package homework;

import java.util.ArrayDeque;

public class CustomerReverseOrder {

    private final ArrayDeque<Customer> stack;

    //todo: 2. надо реализовать методы этого класса
    //надо подобрать подходящую структуру данных, тогда решение будет в "две строчки"

    public CustomerReverseOrder() {
        stack = new ArrayDeque<>();
    }

    public void add(Customer customer) {
        if (customer != null) {
            stack.push(customer);
        }
    }

    public Customer take() {
        return stack.poll();
    }
}
