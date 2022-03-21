package ru.otus.antibruteforce.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.relational.core.mapping.Table;

import javax.annotation.Nonnull;

@Table("whitelist")
public class Whitelist {
    @Id
    private final Long id;
    @Nonnull
    private final Ipv4Type ip;

    public Whitelist(String ip) {
        this(null, new Ipv4Type(ip));
    }

    @PersistenceConstructor
    public Whitelist(Long id, Ipv4Type ip) {
        this.id = id;
        this.ip = ip;
    }

    public Long getId() {
        return id;
    }

    @Nonnull
    public Ipv4Type getIp() {
        return ip;
    }

    @Override
    public String toString() {
        return "Whitelist{" +
                "id=" + id +
                ", ip='" + ip + '\'' +
                '}';
    }

    @Override
    public int hashCode() {
        return ip.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (! (obj instanceof Whitelist whitelist)) {
            return false;
        }

        return ip.equals(whitelist.getIp());
    }
}
